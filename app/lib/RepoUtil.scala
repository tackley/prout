/*
 * Copyright 2014 The Guardian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lib

import org.eclipse.jgit.api.{GitCommand, TransportCommand, Git}
import org.eclipse.jgit.transport.{RefSpec, CredentialsProvider, UsernamePasswordCredentialsProvider}
import com.madgag.git._
import scalax.io.Resource
import java.io.File
import org.eclipse.jgit.lib.{Constants, Repository}
import scalax.file.ImplicitConversions._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import play.api.Logger
import collection.convert.wrapAll._

object RepoUtil {

  def getGitRepo(dataDirectory: File, uri: String, credentials: Option[CredentialsProvider] = None): Repository = {

    def invoke[C <: GitCommand[_], R](command: TransportCommand[C, R]): R = {
      command.setTimeout(5)
      credentials.foreach(command.setCredentialsProvider)
      command.call()
    }

    def getUpToDateRepo(): Repository = {
      val gitdir = dataDirectory / "people.git"

      if (gitdir.exists) {
        Logger.info("Updating Git repo with fetch...")
        val repo = FileRepositoryBuilder.create(gitdir)
        invoke(repo.git.fetch())
        repo
      } else {
        gitdir.doCreateParents()
        Logger.info("Cloning new Git repo...")
        invoke(Git.cloneRepository().setBare(true).setDirectory(gitdir).setURI(uri)).getRepository
      }
    }

    getUpToDateRepo()
  }

}