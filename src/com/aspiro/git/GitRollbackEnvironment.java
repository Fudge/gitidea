package com.aspiro.git;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: erlends
 * Date: Aug 16, 2007
 */
public class GitRollbackEnvironment implements RollbackEnvironment {

   private final Project project;
   private final GitVcsSettings settings;

   public GitRollbackEnvironment(Project project, GitVcsSettings settings) {
      this.project = project;
      this.settings = settings;
   }

   public String getRollbackOperationName() {
      return "Rollback";
   }

   public List<VcsException> rollbackModifiedWithoutCheckout(List<VirtualFile> files) {
      Messages.showInfoMessage(project, "rollbackModifiedWithoutCheckout", "Rollback");
      return null;
   }

   public List<VcsException> rollbackMissingFileDeletion(List<FilePath> files) {
      Messages.showInfoMessage(project, "rollbackMissingFileDeletion", "Rollback");
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public void rollbackIfUnchanged(VirtualFile file) {
      Messages.showInfoMessage(project, "rollbackIfUnchanged", "Rollback");

   }

   public List<VcsException> rollbackChanges(List<Change> changes) {
      Messages.showInfoMessage(project, "rollbackChanges", "Rollback");
      for( Change change : changes ) {
         FilePath f = change.getAfterRevision().getFile();
         
      }
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }
}
