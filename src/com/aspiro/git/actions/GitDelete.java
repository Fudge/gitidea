package com.aspiro.git.actions;

import com.aspiro.git.GitUtil;
import com.aspiro.git.GitVcs;
import com.aspiro.git.GitVcsSettings;
import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 19, 2007
 * Time: 9:25:44 AM
 *
 * Modified to support Git by Erlend Simonsen
 * 
 * <p/>
 * Copyright 2007 Decentrix Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class GitDelete extends BasicAction
{
    public void perform( @NotNull Project project, GitVcs mksVcs, @NotNull List<VcsException> exceptions,
	                     @NotNull VirtualFile[] affectedFiles ) throws VcsException
	{
		ApplicationManager.getApplication().runWriteAction(
				new Runnable()
				{
					public void run()
					{
						FileDocumentManager.getInstance().saveAllDocuments();
					}
				} );

		if( !ProjectLevelVcsManager.getInstance( project ).checkAllFilesAreUnder( GitVcs.getInstance( project ), affectedFiles ) )
			return;

        final Map<VirtualFile,List<VirtualFile>> roots = GitUtil.sortFilesByVcsRoot(project, affectedFiles);

        for (VirtualFile root : roots.keySet()) {
            GitCommand command = new GitCommand( project, GitVcsSettings.getInstance( project ), root );
            command.delete( roots.get(root) );
        }

        VcsDirtyScopeManager mgr = VcsDirtyScopeManager.getInstance( project );
        for( VirtualFile file : affectedFiles )
        {
            mgr.fileDirty( file );
            file.refresh( true, true );
        }
	}

	@NotNull
	protected String getActionName( @NotNull AbstractVcs abstractvcs )
	{
		return "Add";
	}

	protected boolean isEnabled( @NotNull Project project, @NotNull GitVcs vcs, @NotNull VirtualFile... vFiles )
	{
		for( VirtualFile file : vFiles )
		{
			if( FileStatusManager.getInstance( project ).getStatus( file ) == FileStatus.UNKNOWN )
				return false;
		}

		return true;
	}
}
