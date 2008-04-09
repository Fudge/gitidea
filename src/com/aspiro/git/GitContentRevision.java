package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 19, 2007
 * Time: 2:23:54 PM
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
public class GitContentRevision implements ContentRevision
{
	private FilePath file;
	private GitRevisionNumber revision;
	private byte[] content;
	private Project project;

	public GitContentRevision( FilePath filePath, GitRevisionNumber revision, Project project )
	{
		this.project = project;
		this.file = filePath;
		this.revision = revision;
	}

	@Nullable
	public String getContent() throws VcsException
	{
		GitCommand command = new GitCommand(
                project,
                GitVcsSettings.getInstance( project ),
                GitUtil.getVcsRoot(project, file));

        if( content == null )
			content = command.show( file.getPath(), revision.getRev() );
		return GitCommand.convertStreamToString( new ByteArrayInputStream( content ) );
	}

	@NotNull
	public FilePath getFile()
	{
		return file;
	}

	@NotNull
	public VcsRevisionNumber getRevisionNumber()                           
	{
		return revision;
	}
}
