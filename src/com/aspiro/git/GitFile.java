package com.aspiro.git;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 19, 2007
 * Time: 11:41:24 AM
 *
 * Modified for Git by Erlend Simonsen
 *
 * Copyright 2007 Decentrix Inc
 * Copyright 2007 Aspiro AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class GitFile
{
	private String path;
	private Status status;

	public GitFile( String path, Status status )
	{
		this.path = path;
		this.status = status;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath( String path )
	{
		this.path = path;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus( Status status )
	{
		this.status = status;
	}

    public enum Status {
        ADDED,
        MODIFIED,
        UNVERSIONED,
        UNMODIFIED,
        DELETED
    }
}
