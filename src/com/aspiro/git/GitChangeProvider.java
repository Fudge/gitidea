package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 17, 2007
 * Time: 5:18:44 PM
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
public class GitChangeProvider implements ChangeProvider
{
	private Project project;
	private final GitVcsSettings settings;

	public GitChangeProvider( Project project, GitVcsSettings settings )
	{
		this.project = project;
		this.settings = settings;
	}

	public void getChanges( VcsDirtyScope dirtyScope, ChangelistBuilder builder, ProgressIndicator progress ) throws VcsException
	{
        final Set<FilePath> dirtyDirectories = dirtyScope.getRecursivelyDirtyDirectories();
        for (FilePath filePath : dirtyDirectories) {
            getChanges(builder, filePath);
        }

        final Set<FilePath> dirtyFiles = dirtyScope.getDirtyFiles();
        for (FilePath filePath : dirtyFiles) {
            getChanges(builder, filePath);
        }
    }

    private void getChanges(ChangelistBuilder builder, FilePath filePath) throws VcsException {
        GitCommand command = new GitCommand( project, settings, GitUtil.getVcsRoot(project, filePath) );
        Set<GitFile> files = command.status(filePath.getPath(), true);
        for( GitFile file : files )
        {
            getChange(builder, file);
        }
    }

    private void getChange(ChangelistBuilder builder, GitFile file) {
        switch (file.getStatus()) {
            case ADDED: {
                builder.processChange(
                        new Change( null, CurrentContentRevision.create( VcsUtil.getFilePath( file.getPath() ) ), FileStatus.ADDED ) );

                break;
            }
            case DELETED: {
                FilePath path = VcsUtil.getFilePath( file.getPath() );
                builder.processChange(
                        new Change(
                                new GitContentRevision( path, new GitRevisionNumber( GitRevisionNumber.TIP ), project ),
                                null, FileStatus.DELETED ) );
                break;
            }
            case MODIFIED: {
                FilePath path = VcsUtil.getFilePath( file.getPath() );
                builder.processChange(
                        new Change(
                                new GitContentRevision( path, new GitRevisionNumber( GitRevisionNumber.TIP ), project ),
                                CurrentContentRevision.create( path ), FileStatus.MODIFIED ) );
                break;
            }
            case UNMODIFIED: {
                FilePath path = VcsUtil.getFilePath( file.getPath() );
                VirtualFile virtualFile = VcsUtil.getVirtualFile( file.getPath() );
                if (virtualFile != null) {
                    final boolean modified = FileDocumentManager.getInstance().isFileModified(virtualFile);
                    if (modified) {
                        builder.processChange(
                                new Change(
                                        new GitContentRevision( path, new GitRevisionNumber( GitRevisionNumber.TIP ), project ),
                                        CurrentContentRevision.create( path ), FileStatus.MODIFIED )
                        );
                    }
                }
                break;
            }
            case UNVERSIONED: {
                VirtualFile virtualFile = VcsUtil.getVirtualFile( file.getPath() );
                builder.processUnversionedFile( virtualFile );
                break;
            }
            default : {
                throw new IllegalArgumentException("Unknown status: " + file.getStatus());
            }
        }
    }

    public boolean isModifiedDocumentTrackingRequired()
	{
		return true;
	}
}
