package com.aspiro.git;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.diff.DiffProvider;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 23, 2007
 * Time: 10:05:02 AM
 *
 * Modified for Git by Erlend Simonsen
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
public class GitDiffProvider implements DiffProvider
{
	private Project project;

	public GitDiffProvider( Project project )
	{
		this.project = project;
	}

	@Nullable
	public VcsRevisionNumber getCurrentRevision( VirtualFile file )
	{
		return new GitRevisionNumber( GitRevisionNumber.TIP );
	}

	@Nullable
	public VcsRevisionNumber getLastRevision( VirtualFile virtualFile )
	{
		return new GitRevisionNumber( GitRevisionNumber.TIP );
	}

	@Nullable
	public ContentRevision createFileContent( VcsRevisionNumber revisionNumber, VirtualFile selectedFile )
	{
		return new GitContentRevision(
				VcsUtil.getFilePath( selectedFile.getPath() ), (GitRevisionNumber) revisionNumber, project );
	}
}
