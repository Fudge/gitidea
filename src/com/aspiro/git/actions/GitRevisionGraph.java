package com.aspiro.git.actions;

import com.aspiro.git.GitFileSystem;
import com.aspiro.git.GitVcs;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mike.aizatsky
 * Date: Jul 19, 2007
 * Time: 9:25:44 AM
 *
 * Modified for Git by Erlend Simonsen
 *
 * <p/>
 * Copyright 2007 Decentrix Inc
 * Copyright 2007 Aspiro AS
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
public class GitRevisionGraph extends BasicAction
{
	public void perform( @NotNull Project project, GitVcs vcs, @NotNull List<VcsException> exceptions,
	                     @NotNull VirtualFile[] affectedFiles ) throws VcsException
	{
        String url = GitFileSystem.getRevisionGraphUrl(affectedFiles[0].getPath());

        final VirtualFile vFile = VirtualFileManager.getInstance().findFileByUrl(url);

        FileEditorManager.getInstance(project).openFile(vFile, true);
    }

	@NotNull
	protected String getActionName( @NotNull AbstractVcs abstractvcs )
	{
		return "Tag";
	}

	protected boolean isEnabled( @NotNull Project project, @NotNull GitVcs vcs, @NotNull VirtualFile... vFiles )
	{
		return vFiles.length == 1 && FileStatusManager.getInstance(project).getStatus(vFiles[0]) != FileStatus.UNKNOWN;
	}
}
