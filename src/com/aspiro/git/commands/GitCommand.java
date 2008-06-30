package com.aspiro.git.commands;

import com.aspiro.git.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: gevesson
 * Date: Jul 19, 2007
 * Time: 9:54:48 AM
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
@SuppressWarnings( {"ResultOfMethodCallIgnored"} )
public class GitCommand
{
	public static final String ADD_CMD = "add";
	public static final String REVERT_CMD = "checkout";
	private static final String SHOW_CMD = "show";
	private static final String DELETE_CMD = "rm";
   private static final String SYMBOLIC_REF_CMF = "symbolic-ref";
   private static final String DIFF_CMD = "diff";

   private Project project;
	private final GitVcsSettings settings;
   private VirtualFile vcsRoot;

   public GitCommand( @NotNull final Project project, @NotNull GitVcsSettings settings, @NotNull VirtualFile vcsRoot)
	{
      this.vcsRoot = vcsRoot;
      this.project = project;
		this.settings = settings;
	}


	public void add( VirtualFile[] files ) throws VcsException
	{
		String baseDirStr = getBasePath();
		String[] fixedFileNames = new String[files.length];
		int count = 0;
		for( VirtualFile file : files )
		{
			if( file.getPath().substring( 0, baseDirStr.length() ).equals( baseDirStr ) )
			{
				fixedFileNames[count] = getRelativeFilePath( file, vcsRoot);
				count++;
			}
			else
				GitVcs.getInstance( project ).showMessages( "Not in scope: " + file.getPath() );
		}

		execute( ADD_CMD, (String[]) null, fixedFileNames );
	}

	private String getRelativeFilePath(VirtualFile file, @NotNull final VirtualFile baseDir)
	{
		return getRelativeFilePath(file.getPath(), baseDir);
	}

	private String getRelativeFilePath(String file, @NotNull final VirtualFile baseDir)
	{
        final String basePath = baseDir.getPath();
        if (!file.startsWith(basePath)) return file;
        else if (file.equals(basePath)) return ".";
        return file.substring( baseDir.getPath().length() + 1 );
	}


	public void add( Object[] files ) throws VcsException
	{
		VirtualFile[] arr = new VirtualFile[files.length];
		for( int i = 0; i < files.length; i++ )
			arr[i] = (VirtualFile) files[i];
		add( arr );
	}

	/**
	 * Returns a list of files that contain the status of that file.
	 *
	 * @return The set of files.
	 * @throws com.intellij.openapi.vcs.VcsException
	 *          If it fails.
	 */
	public Set<GitFile> status() throws VcsException
	{
        return status(null, false);
    }


    public Set<GitFile> status(String path, boolean includeAll) throws VcsException {
       Set<GitFile> files = new HashSet<GitFile>();
       List<String> args = new ArrayList<String>();

       args.add("--name-status");
       args.add("--diff-filter=ADMRUX");

       args.add("--");

       if (path != null) args.add(getRelativeFilePath(path, vcsRoot));
       String output = convertStreamToString( execute(DIFF_CMD, args) );

       // Also include files scheduled for commit with git add
       args.add(0, "--cached");
       output += convertStreamToString( execute(DIFF_CMD, args) );

       StringTokenizer i = new StringTokenizer( output, "\n\r" );
       while(  i.hasMoreTokens() )
       {
          final String s = i.nextToken();
          String[] larr = s.split( "\t" );
          if(larr.length == 2) {
             GitFile file = new GitFile( getBasePath() + File.separator + larr[1], convertStatus( larr[0] ) );
             files.add( file );
          }
       }

       args.clear();
       args.add("--others");
       if (path != null) args.add(getRelativeFilePath(path, vcsRoot));

       output = convertStreamToString( execute("ls-files", args) );
       i = new StringTokenizer( output, "\n\r" );
       while(  i.hasMoreTokens() )
        {
           final String s = i.nextToken();
           GitFile file = new GitFile( getBasePath() + File.separator + s.trim(), GitFile.Status.UNVERSIONED  );
           files.add( file );
        }

       return files;
    }

    /**
	 * Loads a file from Git.
	 *
	 * @param path     The path to the file.
	 * @param revision The revision to load. If the revision is -1, then HEAD will be loaded.
	 * @return The contents of the revision as a String.
	 * @throws VcsException If the load of the file fails.
	 */
	public byte[] show( String path, String revision ) throws VcsException
	{
		if( path == null || path.equals( "" ) )
			throw new VcsException( "Illegal argument to show" );

		String revisionCmd = "HEAD:";
		if(!revision.equals(GitRevisionNumber.TIP))
			revisionCmd = revision + ":";

      String vcsPath = revisionCmd + getRelativeFilePath( path, vcsRoot);

		InputStream in = execute(SHOW_CMD, null, vcsPath );

		try
		{
			byte[] content = new byte[in.available()];
			in.read( content, 0, in.available() );
			in.close();
			return content;
		}
		catch( IOException e )
		{
			throw new VcsException( e );
		}
	}

	/**
	 * Reverts the list of files we are passed.
	 *
	 * @param files The array of files to revert.
	 * @throws VcsException Id it breaks.
	 */
	public void revert( VirtualFile[] files ) throws VcsException
	{
		String baseDirStr = getBasePath();
		String[] fixedFileNames = new String[files.length];
      String[] args = new String[2];
      args[0] = "HEAD";
      args[1] = "--";
      int count = 0;
		for( VirtualFile file : files )
		{
			if( file.getPath().substring( 0, baseDirStr.length() ).equals( baseDirStr ) )
			{
				fixedFileNames[count] = getRelativeFilePath( file, vcsRoot);
				count++;
			}
			else
				GitVcs.getInstance( project ).showMessages( "Not in scope: " + file.getPath() );
		}
		execute( REVERT_CMD, args, fixedFileNames );
	}

	private InputStream execute( String cmd, String arg ) throws VcsException
	{
		return execute( cmd, null, arg );
	}                                         

	private InputStream execute( String cmd, String oneOption, String[] args ) throws VcsException
	{
		String[] options = new String[1];
		options[0] = oneOption;

		return execute( cmd, options, args );
	}

	private InputStream execute( String cmd, String option, String arg ) throws VcsException
	{
		String[] options = null;
		if( option != null )
		{
			options = new String[1];
			options[0] = option;
		}
		String[] args = null;
		if( arg != null )
		{
			args = new String[1];
			args[0] = arg;
		}

		return execute( cmd, options, args );
	}

	private InputStream execute( String cmd, String[] options, String[] args ) throws VcsException
	{
        List<String> cmdLine = new ArrayList<String>();
        if( options != null )
        {
            cmdLine.addAll( Arrays.asList( options ) );
        }
        if( args != null )
        {
            cmdLine.addAll( Arrays.asList( args ) );
        }
        return execute(cmd, cmdLine);
    }

    private InputStream execute(String cmd) throws VcsException {
        return execute(cmd, Collections.<String>emptyList());
    }

    private InputStream execute( String cmd, List<String> cmdArgs ) throws VcsException
	{
		/*
		 * First, we build the proper command line. Then we execute it.
		 */

		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add( settings.GIT_EXECUTABLE);
		cmdLine.add( cmd );
      cmdLine.addAll( cmdArgs );

		String cmdString = StringUtil.join( cmdLine, " " );
		GitVcs.getInstance( project ).showMessages( "CMD: " + cmdString );

		/*
		 * We now have the command line, so we execute it.
		 */
		File directory = VfsUtil.virtualToIoFile(vcsRoot);

        try
		{
			final Map<String, String> environment = EnvironmentUtil.getEnviromentProperties();

			ProcessBuilder builder = new ProcessBuilder( cmdLine );
			builder.directory( directory );

			Map<String, String> defaultEnv = builder.environment();
			// TODO: This may be completely redundant. Where does Idea get it's env. from?
			// This assumes that we replace the envionment
			for( String key : environment.keySet() )
				defaultEnv.put( key, environment.get( key ) );

			// Push stderr into stdout...
			builder.redirectErrorStream( true );

			Process proc = builder.start();

			/*
			 * Get the output from the process.
			 */
			BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int l;
            while( (l = in.read(buf)) != -1 ) {
                out.write(buf, 0, l);
            }

            proc.waitFor();

            buf = out.toByteArray();

            in.close();
            out.close();

//            GitVcs.getInstance( project ).showMessages( "Result: " + convertStreamToString( new ByteArrayInputStream(buf) ) );


            return new ByteArrayInputStream( buf );
		}
		catch( InterruptedException e )
		{
			throw new VcsException( e );
		}
		catch( IOException e )
		{
			throw new VcsException( e );
		}
	}

	public static String convertStreamToString( InputStream in ) throws VcsException
	{
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		StringWriter result = new StringWriter();

		int count;
		char[] buf = new char[4096];
		try
		{
			while( ( count = reader.read( buf, 0, 4096 ) ) != -1 )
				result.write( buf, 0, count );
			reader.close();
		}
		catch( IOException e )
		{
			throw new VcsException( e );
		}
		return result.toString().replaceAll( "\\r", "" );
	}

	/**
	 * Returns the base path of the project.
	 *
	 * @return The base path of the project.
	 */
	private String getBasePath()
	{
		return vcsRoot.getPath();
	}

	/**
	 * Helper method to convert String status' from the Mercurial output to a GitFile status
	 *
	 * @param status The status from Mercurial as a String.
	 * @return The Mercurial file status.
     * @throws com.intellij.openapi.vcs.VcsException something bad had happened
	 */
	private GitFile.Status convertStatus( String status ) throws VcsException {
		if( status.equals( "A" ) )
			return GitFile.Status.ADDED;
		else if( status.equals( "M" ) )
			return GitFile.Status.MODIFIED;
		else if( status.equals( "X" ) )
			return GitFile.Status.UNVERSIONED;
		else if( status.equals( "D" ) )
			return GitFile.Status.DELETED;
		else if( status.equals( "C" ) )
			return GitFile.Status.UNMODIFIED;

		throw new VcsException("Unknown status: " + status);
	}

	public void commit( Set<String> paths, String message ) throws VcsException
	{
		String[] options = new String[1];
		options[0] = "-m " + message;

		String[] args = new String[paths.size()];
		int i = 0;
		for( String path : paths )
			args[i++] = getRelativeFilePath( path, vcsRoot);

		execute( "commit", options, args );
	}

   public String currentBranch() throws VcsException {
      return convertStreamToString( execute( SYMBOLIC_REF_CMF, "HEAD" ) ).replace("\n", "").substring(10);
   }

   public void delete( VirtualFile[] files ) throws VcsException
	{
		String baseDirStr = getBasePath();
		String[] fixedFileNames = new String[files.length];
		int count = 0;
		for( VirtualFile file : files )
		{
			if( file.getPath().substring( 0, baseDirStr.length() ).equals( baseDirStr ) )
			{
				fixedFileNames[count] = getRelativeFilePath( file, vcsRoot);
				count++;
			}
			else
				GitVcs.getInstance( project ).showMessages( "Not in scope: " + file.getPath() );
		}

		execute( DELETE_CMD, (String[]) null, fixedFileNames );
	}

   /**
    * Builds the annotation for the specified file.
    *
    * @param filePath The path to the file.
    * @return The GitFileAnnotation.
    * @throws com.intellij.openapi.vcs.VcsException
    *          If it fails...
    */
   public GitFileAnnotation annotate(FilePath filePath) throws VcsException {
      String[] options = new String[]{"-l", "--"};

      String[] args = new String[]{getRelativeFilePath(filePath.getPath(), vcsRoot)};

      InputStream cmdOutput = execute("annotate", options, args);
      BufferedReader in = new BufferedReader(new InputStreamReader(cmdOutput));
      GitFileAnnotation annotation = new GitFileAnnotation(project);

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss Z");

      String Line;
      try {
         while ((Line = in.readLine()) != null) {
            String annValues[] = Line.split("\t", 4);
            if (annValues.length != 4) {
               throw new VcsException("Framing error: unexpected number of values");
            }

            String revision = annValues[0];
            String user = annValues[1];
            String dateStr = annValues[2];
            String numberedLine = annValues[3];

            if (revision.length() != 40) {
               throw new VcsException("Framing error: Illegal revision number: " + revision);
            }

            int idx = numberedLine.indexOf(')');
            if (!user.startsWith("(") || idx <= 0) {
               throw new VcsException("Framing error: unexpected format");
            }
            user = user.substring(1).trim(); // Ditch the (
            Long lineNumber = Long.valueOf(numberedLine.substring(0, idx));
            String lineContents = numberedLine.substring(idx + 1);

            Date date = dateFormat.parse(dateStr);
            annotation.appendLineInfo(date, new GitRevisionNumber(revision, date.getTime()), user, lineContents, lineNumber);
         }

      } catch (IOException e) {
         throw new VcsException("Failed to load annotations", e);
      } catch (ParseException e) {
         throw new VcsException("Failed to load annotations", e);
      }
      return annotation;
   }

   /**
	 * Builds the revision history for the specifid file.
	 *
	 * @param filePath The path to the file.
	 * @return The list.
	 * @throws com.intellij.openapi.vcs.VcsException
	 *          If it fails...
	 */
	public List<VcsFileRevision> log( FilePath filePath ) throws VcsException
	{
		String[] options = new String[]
				{
                    "--pretty=format:%H@@@%an <%ae>@@@%ct@@@%s",
                    "--"

            };

		String[] args = new String[]
				{
						getRelativeFilePath( filePath.getPath(), vcsRoot)
				};

		String result = convertStreamToString( execute( "log", options, args ) );
		GitVcs.getInstance( project ).showMessages( result );

		List<VcsFileRevision> revisions = new ArrayList<VcsFileRevision>();

		// Pull the result apart...
		BufferedReader in = new BufferedReader( new StringReader( result ) );
		String line;
		try
		{
			while( ( line = in.readLine() ) != null )
			{
				String[] values = line.split( "@@@" );

            GitFileRevision revision = new GitFileRevision(
                    project,
                    filePath,
                    new GitRevisionNumber( values[0] ),
                    new Date(Long.valueOf(values[2]) * 1000),
                    values[1],
                    values[3] );
            revisions.add( revision );
         }

		}
		catch( IOException e )
		{
			throw new VcsException( e );
		}
		return revisions;
	}

	public String version() throws VcsException
	{
		return convertStreamToString( execute( "version" ) );
	}

	public String tag( String tagName ) throws VcsException
	{
		return convertStreamToString( execute( "tag", tagName ) );
	}

	public void pull( String respository, boolean update ) throws VcsException
	{
        String cmd = null;
		if( update )
			cmd = "pull";
      else
         cmd = "fetch";

      String result = convertStreamToString( execute( cmd, null, respository ) );
		GitVcs.getInstance( project ).showMessages( result );
	}

    public void merge(GitBranch branch) throws VcsException
    {
        String result = convertStreamToString( execute( "merge", branch.getName() ) );
        GitVcs.getInstance( project ).showMessages( result );
    }


	public void merge() throws VcsException
	{
        String result = convertStreamToString( execute( "merge" ) );
        GitVcs.getInstance( project ).showMessages( result );
	}

   public void rebase() throws VcsException
   {
       String result = convertStreamToString( execute( "rebase" ) );
       GitVcs.getInstance( project ).showMessages( result );
   }

	public void push() throws VcsException
	{
        ArrayList<String> args = new ArrayList<String>();
        args.add("--tags");
        String result = convertStreamToString( execute( "push", args ) );
		GitVcs.getInstance( project ).showMessages( result );
	}

    public void delete(List<VirtualFile> files) throws VcsException {
        delete(files.toArray(new VirtualFile[files.size()]));
    }

    public void revert(List<VirtualFile> files) throws VcsException {
        revert(files.toArray(new VirtualFile[files.size()]));
    }

    public void add(List<VirtualFile> files) throws VcsException {
        add(files.toArray(new VirtualFile[files.size()]));
    }

	/**
	 * Clones the repository to the specified path.
	 *
	 * @param src The src repository. May be a URL or a path.
	 * @param target The target directory.
	 * 
	 * @throws com.intellij.openapi.vcs.VcsException If an error occurs.
	 */
	public void cloneRepository( String src, String target ) throws VcsException
	{
		String[] args = new String[2];
		args[0] = src;
		args[1] = target;

		String result = convertStreamToString( execute( "clone", (String) null, args ) );
      GitVcs.getInstance( project ).showMessages( result );
   }

   public List<GitBranch> branch(boolean includeRemote) throws VcsException {
      ArrayList<String> args = new ArrayList<String>();
       if( includeRemote )
         args.add("-a");
      String result = convertStreamToString( execute( "branch", args ) );
      List<GitBranch> branches = new ArrayList<GitBranch>();

      BufferedReader in = new BufferedReader( new StringReader( result ) );
      String line;
      try
      {
         while( ( line = in.readLine() ) != null )
         {
            String branchName = line.trim();

            boolean active = false;
            if( branchName.contains("* ") ) {
               branchName = branchName.substring(2);
               active = true;
            }

            boolean remote = branchName.contains("/");

            GitBranch branch = new GitBranch(
                    project,
                    branchName,
                    active,
                    remote);
            branches.add( branch );
         }
      }
      catch( IOException e )
      {
         throw new VcsException( e );
      }
      return branches;
   }

   public void checkout(String selectedBranch, boolean createBranch) throws VcsException {
      ArrayList<String> args = new ArrayList<String>();
      if(createBranch) {
         args.add("--track");
         args.add("-b");
      }

      args.add(selectedBranch);

      String result = convertStreamToString( execute( "checkout", args ));
      GitVcs.getInstance( project ).showMessages( result );
   }
}
