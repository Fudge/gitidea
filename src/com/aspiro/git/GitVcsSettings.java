package com.aspiro.git;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * @author mike
 *
 * Modified for Git by Erlend Simonsen
 */
@State(
		name = "Git.Settings",
		storages = {
		@Storage(
				id = "ws",
				file = "$WORKSPACE_FILE$"
		)}
)
public class GitVcsSettings implements PersistentStateComponent<GitVcsSettings>
{
	public String GIT_EXECUTABLE = "git";

	public GitVcsSettings getState()
	{
		return this;
	}

	public void loadState( GitVcsSettings gitVcsSettings)
	{
		XmlSerializerUtil.copyBean(gitVcsSettings, this );
	}

	public static GitVcsSettings getInstance( Project project )
	{
		return ServiceManager.getService( project, GitVcsSettings.class );
	}
}
