package com.aspiro.git.actions;

import com.aspiro.git.GitVcs;
import com.aspiro.git.GitUtil;
import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.actions.StandardVcsGroup;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 19, 2007
 * Time: 9:31:13 AM
 *
 * Modified to support Git by Erlend Simonsen
 *
 * Copyright 2007 Decentrix Inc
 * Copyright 2007 Aspiro AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class GitMenu extends StandardVcsGroup
{
	public AbstractVcs getVcs( Project project )
	{
		return GitVcs.getInstance( project );
	}

	@Override
	public String getVcsName( final Project project )
	{
      return "Git";

//      GitVcs vcs = GitVcs.getInstance(project);
//      final VirtualFile[] roots = ProjectLevelVcsManager.getInstance(project).getRootsUnderVcs(vcs);
//      VirtualFile root = roots[0];
//      GitCommand command = new GitCommand( project, vcs.getSettings(), GitUtil.getVcsRoot(project, root) );
//      try {
//         return "Git (" + command.currentBranch() + ")";
//      } catch (VcsException e) {
//      }

   }
}
