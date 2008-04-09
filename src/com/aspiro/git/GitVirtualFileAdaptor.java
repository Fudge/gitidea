package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.vfs.*;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 18, 2007
 * Time: 3:53:42 PM
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
public class GitVirtualFileAdaptor extends VirtualFileAdapter
{
	private Project project;
	private GitVcs host;
	private static final String TITLE = "Add file(s)";
	private static final String MESSAGE = "Add files to Git?\n{0}";

	public GitVirtualFileAdaptor( @NotNull GitVcs host, @NotNull Project project )
	{
		this.host = host;
		this.project = project;
	}

	public void propertyChanged( VirtualFilePropertyEvent event )
	{
		super.propertyChanged( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void contentsChanged( VirtualFileEvent event )
	{
		super.contentsChanged( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * Called when a new file is added.
	 *
	 * @param event The event from Idea.
	 */
	public void fileCreated( VirtualFileEvent event )
	{
		if( event.isFromRefresh() )
			return;

		final VirtualFile file = event.getFile();

		if( isFileProcessable( file ) )
		{
			VcsShowConfirmationOption option = host.getAddConfirmation();
			if( option.getValue() == VcsShowConfirmationOption.Value.SHOW_CONFIRMATION )
			{
				List<VirtualFile> files = new ArrayList<VirtualFile>();
				files.add( file );

				AbstractVcsHelper helper = AbstractVcsHelper.getInstance( project );
				Collection<VirtualFile> filesToAdd = helper.selectFilesToProcess( files, TITLE, null, TITLE, MESSAGE, option );

				if( filesToAdd != null )
				{
					GitCommand command = new GitCommand( project, host.getSettings(), GitUtil.getVcsRoot( project, file ) );
					try
					{
						command.add( filesToAdd.toArray() );
					}
					catch( VcsException e )
					{
						List<VcsException> es = new ArrayList<VcsException>();
						es.add( e );
						GitVcs.getInstance( project ).showErrors( es, "Changes" );
					}
				}
			}
		}

	}

	public void fileDeleted( VirtualFileEvent event )
	{
		super.fileDeleted( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void fileMoved( VirtualFileMoveEvent event )
	{
		super.fileMoved( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void fileCopied( VirtualFileCopyEvent event )
	{
		super.fileCopied( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void beforePropertyChange( VirtualFilePropertyEvent event )
	{
		super.beforePropertyChange( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void beforeContentsChange( VirtualFileEvent event )
	{
		super.beforeContentsChange( event );
	}

	public void beforeFileDeletion( VirtualFileEvent event )
	{
		@NonNls final String TITLE = "Delete file(s)";
		@NonNls final String MESSAGE = "Do you want to schedule the following file for deletion from Git?\n{0}";

		VirtualFile file = event.getFile();

		//  In the case of multi-vcs project configurations, we need to skip all
		//  notifications on non-owned files
		if( !VcsUtil.isFileForVcs( file, project, host ) )
			return;

		//  Do not ask user if the files created came from the vcs per se
		//  (obviously they are not new).
		if( event.isFromRefresh() )
			return;

		//  Take into account only processable files.
		if( isFileProcessable( file ) && VcsUtil.isPathUnderProject( project, file ) )
		{
			VcsShowConfirmationOption option = host.getDeleteConfirmation();

			//  In the case when we need to perform "Delete" vcs action right upon
			//  the file's creation, put the file into the host's cache until it
			//  will be analyzed by the ChangeProvider.
			if( option.getValue() == VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY )
			{
				deleteFile( file );
			}
			else if( option.getValue() == VcsShowConfirmationOption.Value.SHOW_CONFIRMATION )
			{
				List<VirtualFile> files = new ArrayList<VirtualFile>();
				files.add( file );

				AbstractVcsHelper helper = AbstractVcsHelper.getInstance( project );
				Collection<VirtualFile> filesToAdd =
						helper.selectFilesToProcess( files, TITLE, null, TITLE, MESSAGE, option );

				if( filesToAdd != null )
				{
					deleteFile( file );
				}
				else
				{
					deleteFile( file );
				}
			}
			else
			{
				deleteFile( file );
			}
		}
	}

	public void deleteFile( VirtualFile file )
	{
		GitCommand command = new GitCommand( project, host.getSettings(), GitUtil.getVcsRoot( project, file ) );
		VirtualFile[] files = new VirtualFile[1];
		files[0] = file;
		try
		{
			command.delete( files );
		}
		catch( VcsException e )
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}

	public void beforeFileMovement( VirtualFileMoveEvent event )
	{
		super.beforeFileMovement( event );    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * File is not processable if it is outside the vcs scope or it is in the
	 * list of excluded project files.
	 *
	 * @param file The file to check.
	 * @return Returns true of the file can be added.
	 */
	private boolean isFileProcessable( VirtualFile file )
	{
		VirtualFile base = project.getBaseDir();
		assert base != null;
		return file.getPath().startsWith( base.getPath() ) && !FileTypeManager.getInstance().isFileIgnored( file.getName() );
	}
}
