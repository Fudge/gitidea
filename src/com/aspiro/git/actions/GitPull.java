package com.aspiro.git.actions;

import com.aspiro.git.GitUtil;
import com.aspiro.git.GitVcs;
import com.aspiro.git.GitBranch;
import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class implements the pull functionality from a git respository. A path or a URL is required to
 * execute the command.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 26, 2007
 * Time: 11:34:07 AM
 *
 * Modified to support Git by Erlend Simonsen
 */
public class GitPull extends BasicAction
{
	protected void perform( @NotNull Project project, GitVcs vcs, @NotNull List<VcsException> exceptions,
	                        @NotNull VirtualFile[] affectedFiles ) throws VcsException
	{
		saveAll();

//		final String respository = Messages.showInputDialog( project, "Specify source respository", "Pull", Messages.getQuestionIcon() );
//		if( respository == null )
//			return;

        final VirtualFile[] roots = ProjectLevelVcsManager.getInstance(project).getRootsUnderVcs(vcs);
        //todo
        assert roots.length == 1 : "more than 1 root push is not supported";

        for (VirtualFile root : roots) {
            GitCommand command = new GitCommand( project, vcs.getSettings(), GitUtil.getVcsRoot(project, root) );
            command.pull( null, true );

            /*
            /* TODO: Do a merge after the pull. Currently, this will pop up the configured merge tool for conflicts.
             * Integrating this into Idea may be challenging...
             */
//            command.rebase();
        }

	}

	@NotNull
	protected String getActionName( @NotNull AbstractVcs abstractvcs )
	{
		return "Pull";
	}

	protected boolean isEnabled( @NotNull Project project, @NotNull GitVcs vcs, @NotNull VirtualFile... vFiles )
	{
      return true;
   }
}
