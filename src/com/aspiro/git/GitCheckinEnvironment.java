package com.aspiro.git;

import com.aspiro.git.actions.GitAdd;
import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 20, 2007
 * Time: 4:09:59 PM
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
public class GitCheckinEnvironment implements CheckinEnvironment
{
	private Project project;
	private final GitVcsSettings settings;

	public GitCheckinEnvironment( Project project, GitVcsSettings settings )
	{
		this.project = project;
		this.settings = settings;
	}

	@Nullable
	public RefreshableOnComponent createAdditionalOptionsPanel( CheckinProjectPanel panel )
	{
		return null;
	}

	@Nullable
	public String getDefaultMessageFor( FilePath[] filesToCheckin )
	{
		return "# Please enter the commit message for your changes.\n# (Comment lines starting with '#' will not be included)";
	}

	public String prepareCheckinMessage( String text )
	{
		return null;
	}

	@Nullable
	@NonNls
	public String getHelpId()
	{
		return null;
	}

	public String getCheckinOperationName()
	{
		return "Commit";
	}

	public boolean showCheckinDialogInAnyCase()
	{
		return true;
	}

	public List<VcsException> commit( List<Change> changes, String preparedComment )
	{
		List<VcsException> exceptions = new ArrayList<VcsException>();

        Map<VirtualFile, List<Change>> sortedChanges = sortChangesByVcsRoot(changes);

        for (VirtualFile root : sortedChanges.keySet()) {
            GitCommand command = new GitCommand( project, settings, root);
            Set<String> paths = new HashSet<String>();
            for( Change change : changes )
            {
                if( change.getFileStatus().equals( FileStatus.MODIFIED ) )
                    paths.add( change.getAfterRevision().getFile().getPath() );
                else if( change.getFileStatus().equals( FileStatus.ADDED ))
                    paths.add( change.getAfterRevision().getFile().getPath() );
                else if( change.getFileStatus().equals( FileStatus.DELETED ) )
                    paths.add( change.getBeforeRevision().getFile().getPath() );
            }
            try
            {
                command.commit( paths, preparedComment );
            }
            catch( VcsException e )
            {
                exceptions.add( e );
            }
        }


		return exceptions;
	}

    private Map<VirtualFile, List<Change>> sortChangesByVcsRoot(List<Change> changes) {
        Map<VirtualFile, List<Change>> result = new HashMap<VirtualFile, List<Change>>();

        for (Change change : changes) {
            final ContentRevision afterRevision = change.getAfterRevision();
            final ContentRevision beforeRevision = change.getBeforeRevision();

            final FilePath filePath = afterRevision != null ? afterRevision.getFile() : beforeRevision.getFile();
            final VirtualFile vcsRoot = GitUtil.getVcsRoot(project, filePath);

            List<Change> changeList = result.get(vcsRoot);
            if (changeList == null) {
                changeList = new ArrayList<Change>();
                result.put(vcsRoot, changeList);
            }
            changeList.add(change);
        }

        return result;
    }

    public List<VcsException> scheduleMissingFileForDeletion( List<FilePath> files )
	{
        return null;
    }

	public List<VcsException> scheduleUnversionedFilesForAddition( List<VirtualFile> files )
	{
        try {
            GitAdd.addFiles(project, files.toArray(new VirtualFile[files.size()]));
            return Collections.emptyList();
        } catch (VcsException e) {
            return Collections.singletonList(e);
        }
	}
}
