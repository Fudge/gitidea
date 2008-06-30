package com.aspiro.git;

import com.intellij.openapi.vcs.history.VcsRevisionNumber;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 23, 2007
 * Time: 10:21:44 AM
 *
 * Modified for Git by Erlend Simonsen
 *
 * Copyright 2007 Decentrix Inc
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
public class GitRevisionNumber implements VcsRevisionNumber
{
	public static final String TIP = "HEAD";
	
	private String rev;
   private long time = System.currentTimeMillis();

   public GitRevisionNumber( String version )
	{
		this.rev = version;
	}

   public GitRevisionNumber(String version, long timeStamp) {
      this.time = timeStamp;
      this.rev = version;
   }

   public String asString()
	{
		return rev;
	}

   public long getTime() {
      return time;
   }

   public int compareTo( VcsRevisionNumber vcsRevisionNumber )
	{
		GitRevisionNumber rev = (GitRevisionNumber) vcsRevisionNumber;

		if( getTime() > rev.getTime() )
			return -1;
		else if( getTime() == rev.getTime() )
			return 0;
		else
			return 1;
	}

	public String getRev()
	{
		return rev;
	}

   public String getShortRev() {
      return rev.length() == 40 ? rev.substring(0, 8) : rev;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      GitRevisionNumber that = (GitRevisionNumber) o;

      return rev == null ? that.rev == null : rev.equals(that.rev);
   }

   public int hashCode() {
      return rev == null ? 0 : rev.hashCode();
   }
}
