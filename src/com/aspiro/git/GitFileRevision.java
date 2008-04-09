package com.aspiro.git;

import com.aspiro.git.commands.GitCommand;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;

import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 24, 2007
 * Time: 11:42:38 AM
 *
 * Modified for Git by Erlend Simonsen
 */
public class GitFileRevision implements VcsFileRevision
{
	private FilePath path;
	private GitRevisionNumber revision;
	private Date revisionDate;
	private String author;
	private String message;
	private byte[] content;
	private Project project;

	public GitFileRevision(
			Project project,
			FilePath path,
			GitRevisionNumber revision,
			Date date,
			String author,
			String message )
	{
		this.project = project;
		this.path = path;
		this.revision = revision;
		this.revisionDate = date;
		this.author = author;
		this.message = message;
	}

	public VcsRevisionNumber getRevisionNumber()
	{
		return revision;
	}

	public String getBranchName()
	{
		return null;
	}

	public Date getRevisionDate()
	{
		return revisionDate;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getCommitMessage()
	{
		return message;
	}

	public void loadContent() throws VcsException
	{
		GitCommand command = new GitCommand( project, GitVcsSettings.getInstance( project ), GitUtil.getVcsRoot(project, path) );
		content = command.show( path.getPath(), revision.getRev() );
	}

	public byte[] getContent() throws IOException
	{
		return content;
	}
}
