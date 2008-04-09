package com.aspiro.git.actions;

import com.aspiro.git.GitBranch;
import com.aspiro.git.GitUtil;
import com.aspiro.git.GitVcs;
import com.aspiro.git.commands.GitCommand;
import com.aspiro.git.validators.GitBranchNameValidator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the pull functionality from a mercurial respository. A path or a URL is required to
 * execute the command.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 26, 2007
 * Time: 11:34:07 AM
 *
 * Modified to support Git by Erlend Simonsen
 */
public class GitMerge extends BasicAction
{
	protected void perform( @NotNull Project project, GitVcs vcs, @NotNull List<VcsException> exceptions,
	                        @NotNull VirtualFile[] affectedFiles ) throws VcsException
	{
		saveAll();

      final VirtualFile[] roots = ProjectLevelVcsManager.getInstance(project).getRootsUnderVcs(vcs);
      //todo
      assert roots.length == 1 : "more than 1 root merge is not supported";

      List<GitBranch> branches = new ArrayList<GitBranch>();

      for (VirtualFile root : roots) {
          GitCommand command = new GitCommand( project, vcs.getSettings(), GitUtil.getVcsRoot(project, root) );
         try {
            branches = command.branch( true );

            String[] branchesList = new String[branches.size()];
            GitBranch selectedBranch = null;
            int i = 0;
            for (Object b : branches) {
               GitBranch branch = (GitBranch) b;
               branchesList[i++] = branch.getName();
               if( selectedBranch == null || branch.isActive() )
                  selectedBranch = branch;

            }

             if( selectedBranch == null )
                return;

            int branchNum = Messages.showChooseDialog( "Select branch to merge into this one", "Merge Branch", branchesList, selectedBranch.getName(), Messages.getQuestionIcon());
            if( branchNum < 0 )
               return;
            selectedBranch = branches.get(branchNum);

            command.merge(selectedBranch);

         } catch (VcsException e) {
            return;
         }
      }


   }

	@NotNull
	protected String getActionName( @NotNull AbstractVcs abstractvcs )
	{
		return "Checkout";
	}

	protected boolean isEnabled( @NotNull Project project, @NotNull GitVcs vcs, @NotNull VirtualFile... vFiles )
	{
        final VirtualFile[] roots = ProjectLevelVcsManager.getInstance(project).getRootsUnderVcs(vcs);
        //todo
        assert roots.length == 1 : "more than 1 root push is not supported";

        List<GitBranch> branches = vcs.getBranches();

        if( branches == null ) {
           for (VirtualFile root : roots) {
              GitCommand command = new GitCommand( project, vcs.getSettings(), GitUtil.getVcsRoot(project, root) );
              try {
                 branches = command.branch( true );
              } catch (VcsException e) {

              }
           }
           vcs.setBranches(branches);
        }
        return branches != null && branches.size() > 0;
   }
}