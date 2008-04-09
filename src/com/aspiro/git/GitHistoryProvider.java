package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.*;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 24, 2007
 * Time: 11:28:00 AM
 *
 * Modified for Git by Erlend Simonsen
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
public class GitHistoryProvider implements VcsHistoryProvider
{
	private final Project project;
	private final GitVcsSettings settings;

	public GitHistoryProvider( Project project, GitVcsSettings settings )
	{
		this.project = project;
		this.settings = settings;
	}

	public ColumnInfo[] getRevisionColumns()
	{
		return new ColumnInfo[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public AnAction[] getAdditionalActions( FileHistoryPanel panel )
	{
		return new AnAction[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean isDateOmittable()
	{
		return false;
	}

	@Nullable
	@NonNls
	public String getHelpId()
	{
		return null;
	}

	/**
	 * Provides the list of revisions for the specified file. This basically devolves to calling git log for the file, parsing the output
	 * and stuffing it into the requisite structure for Idea.
	 *
	 * @param filePath The path to the file.
	 * @return A VcsSessionHistory that contains all the revisions of this file.
	 * @throws VcsException If it breaks.
	 */
	@Nullable
	public VcsHistorySession createSessionFor( FilePath filePath ) throws VcsException
	{
		GitCommand command = new GitCommand( project, settings, GitUtil.getVcsRoot(project, filePath) );
		List<VcsFileRevision> revisions = command.log( filePath );

		return new VcsHistorySession( revisions )
		{
			@Nullable
			protected VcsRevisionNumber calcCurrentRevisionNumber()
			{
				//todo
				return null;
			}
		};
	}

	@Nullable
	public HistoryAsTreeProvider getTreeHistoryProvider()
	{
		return null; // Not implemented.
	}
}
