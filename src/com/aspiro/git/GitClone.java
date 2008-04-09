package com.aspiro.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: erlends
 * Date: Aug 13, 2007
 * Time: 8:08:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class GitClone extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(DataKeys.PROJECT);
        final String repository = Messages.showInputDialog(project, "Specify source repository url\n(password prompting for ssh not supported)", "clone", Messages.getQuestionIcon());
        if (repository == null)
            return;

        FileChooserDescriptor fcd = new FileChooserDescriptor(false, true, false, false, false, false);
        fcd.setShowFileSystemRoots(true);
        fcd.setTitle("Destination Directory");
        fcd.setDescription("Select destination directory for clone.");
        fcd.setHideIgnored(false);
        VirtualFile[] files = FileChooser.chooseFiles(project, fcd, null);
        if (files.length != 1 || files[0] == null) {
            System.out.println("No file");
            return;
        }
        final File dir = new File(files[0].getPath());

        final List<String> cmdLine = new ArrayList<String>();
        cmdLine.add("git");
        cmdLine.add("clone");
        cmdLine.add(repository);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            public void run() {

        try {
            ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
            progressIndicator.setText("Cloning " + repository);
            System.out.println("Starting run.." + cmdLine.toString());
            final Map<String, String> environment = EnvironmentUtil.getEnviromentProperties();

            final ProcessBuilder builder = new ProcessBuilder(cmdLine);
            builder.directory(dir);

            Map<String, String> defaultEnv = builder.environment();
            // TODO: This may be completely redundant. Where does Idea get it's env. from?
            // This assumes that we replace the envionment
            for (String key : environment.keySet())
                defaultEnv.put(key, environment.get(key));

            // Push stderr into stdout...
            builder.redirectErrorStream(true);
            System.out.println("Starting run..");
            Process proc = builder.start();
            proc.waitFor();
            System.out.println("Clone done..");
            InputStream in = proc.getInputStream();
            byte[] buf = new byte[in.available()];
            in.read(buf, 0, in.available());
            in.close();
            System.out.println(buf);
        }
        catch (InterruptedException ex) {
            System.out.println("Interrupted Exception");
        }
        catch (IOException
                ex) {
            System.out.println("IO Exception " + ex);
        }
            }
        }, "Cloning..", false, project);
        /*
        * Get the output from the process.
        */
    }

    protected void perform(@NotNull Project project, GitVcs mksVcs, @NotNull List<VcsException> exceptions, @NotNull VirtualFile[] affectedFiles) throws VcsException {
    }

    @NotNull
    protected String getActionName(@NotNull AbstractVcs abstractvcs) {
        return "Git Clone";
    }

    protected boolean isEnabled(@NotNull Project project, @NotNull GitVcs mksvcs, @NotNull VirtualFile... vFiles) {
        return true;
    }
}
