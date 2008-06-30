/*
 * $Id$
 */
package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * GitAnnotationProvider.
 * Based on the JetBrains SVNAnnotationProvider.
 *
 * @author Brett Sealey
 *         <p/>
 *         <p/>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <p/>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <p/>
 *         Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *         specific language governing permissions and limitations under the License.
 */
public class GitAnnotationProvider implements AnnotationProvider {
   private final Project project;
   private GitVcsSettings settings;

   public GitAnnotationProvider(@NotNull Project project, @NotNull GitVcsSettings settings) {
      this.project = project;
      this.settings = settings;
   }

   public FileAnnotation annotate(@NotNull VirtualFile file) throws VcsException {
      return annotate(file, new GitFileRevision(project, VcsUtil.getFilePath(file.getPath()), new GitRevisionNumber(GitRevisionNumber.TIP), null, null, null));
   }

   public FileAnnotation annotate(@NotNull final VirtualFile file, @NotNull VcsFileRevision revision) throws VcsException {
      if (file.isDirectory()) {
         throw new VcsException("Cannot annotate a directory");
      }

      final FileAnnotation[] annotation = new FileAnnotation[1];
      final Exception[] exception = new Exception[1];

      Runnable command = new Runnable() {
         public void run() {
            final ProgressIndicator progress = ProgressManager.getInstance().getProgressIndicator();
            try {

               final File ioFile = new File(file.getPath()).getAbsoluteFile();

               GitCommand command = new GitCommand(project, settings, GitUtil.getVcsRoot(project, file));
               FilePath filePath = VcsUtil.getFilePath(file.getPath());
               if (progress != null) {
                  progress.setText("Computing annotation for " + file.getName());
               }
               final GitFileAnnotation result = command.annotate(filePath);

               if (progress != null) {
                  progress.setText("Getting history for " + file.getName());
               }
               final List<VcsFileRevision> revisions = command.log(filePath);

               result.addLogEntries(revisions);
               annotation[0] = result;
            }
            catch (Exception e) {
               exception[0] = e;
            }
         }
      };
      if (ApplicationManager.getApplication().isDispatchThread()) {
         ProgressManager.getInstance().runProcessWithProgressSynchronously(command, "Annotate", false, project);
      } else {
         command.run();
      }
      if (exception[0] != null) {
         throw new VcsException("Failed to annotate: " + exception[0], exception[0]);
      }
      return annotation[0];
   }

   public boolean isAnnotationValid(VcsFileRevision rev) {
      return true;
   }
}
