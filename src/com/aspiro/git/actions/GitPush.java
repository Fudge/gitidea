package com.aspiro.git.actions;

import com.aspiro.git.GitUtil;
import com.aspiro.git.GitVcs;
import com.aspiro.git.GitBranch;
import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class implements the pull functionality from a Git respository. 
 * <p/>
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 26, 2007
 * Time: 11:34:07 AM
 *
 * Modified to support Git by Erlend Simonsen
 */
public class GitPush extends BasicAction
{
	protected void perform( @NotNull Project project, GitVcs vcs, @NotNull List<VcsException> exceptions,
	                        @NotNull VirtualFile[] affectedFiles ) throws VcsException
	{
		saveAll();

        final VirtualFile[] roots = ProjectLevelVcsManager.getInstance(project).getRootsUnderVcs(vcs);
        //todo
        assert roots.length == 1 : "more than 1 root push is not supported";

        for (VirtualFile root : roots) {
            GitCommand command = new GitCommand( project, vcs.getSettings(), GitUtil.getVcsRoot(project, root) );
            command.push();
        }
	}

	@NotNull
	protected String getActionName( @NotNull AbstractVcs abstractvcs )
	{
		return "Push";
	}

	protected boolean isEnabled( @NotNull Project project, @NotNull GitVcs vcs, @NotNull VirtualFile... vFiles )
	{
      return true;
	}
}