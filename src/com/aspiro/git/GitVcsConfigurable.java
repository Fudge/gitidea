package com.aspiro.git;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mike
 *
 * Modified for Git by Erlend Simonsen
 */
public class GitVcsConfigurable implements Configurable
{
	private final GitVcsSettings settings;
	private GitVcsPanel panel;
	private Project project;

	public GitVcsConfigurable( GitVcsSettings settings, Project project )
	{
		this.project = project;
		this.settings = settings;
	}

	@Nls
	public String getDisplayName()
	{
		return "Git";
	}

	@Nullable
	public Icon getIcon()
	{
		return null;
	}

	@Nullable
	@NonNls
	public String getHelpTopic()
	{
		return null;
	}

	public JComponent createComponent()
	{
		panel = new GitVcsPanel( project );
		panel.load( settings );
		return panel.getPanel();
	}

	public boolean isModified()
	{
		return panel.isModified( settings );
	}

	public void apply() throws ConfigurationException
	{
		panel.save( settings );
	}

	public void reset()
	{
		panel.load( settings );
	}

	public void disposeUIResources()
	{
	}
}
