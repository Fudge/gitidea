package com.aspiro.git;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author mike
 *
 * Modified for Git by Erlend Simonsen
 * 
 **/
public class GitUtil {
    @NotNull
    public static VirtualFile getVcsRoot(@NotNull final Project project, @NotNull final FilePath filePath) {
        return ApplicationManager.getApplication().runReadAction(new Computable<VirtualFile>() {
            public VirtualFile compute() {
                final VirtualFile vcsRoot = ProjectLevelVcsManager.getInstance(project).getVcsRootFor(filePath);
                assert vcsRoot != null;

                return vcsRoot;
            }
        });
    }

    public static VirtualFile getVcsRoot(@NotNull final Project project, final VirtualFile virtualFile) {
        return ApplicationManager.getApplication().runReadAction(new Computable<VirtualFile>() {
            public VirtualFile compute() {
                final VirtualFile vcsRoot = ProjectLevelVcsManager.getInstance(project).getVcsRootFor(virtualFile);
                assert vcsRoot != null;

                return vcsRoot;
            }
        });
    }

    public static Map<VirtualFile, List<VirtualFile>> sortFilesByVcsRoot(
            @NotNull Project project,
            @NotNull List<VirtualFile> virtualFiles) {
        Map<VirtualFile, List<VirtualFile>> result = new HashMap<VirtualFile, List<VirtualFile>>();

        for (VirtualFile file : virtualFiles) {
            final VirtualFile vcsRoot = getVcsRoot(project, file);
            assert vcsRoot != null;

            List<VirtualFile> files = result.get(vcsRoot);
            if (files == null) {
                files = new ArrayList<VirtualFile>();
                result.put(vcsRoot, files);
            }
            files.add(file);
        }

        return result;
    }

    public static Map<VirtualFile, List<VirtualFile>> sortFilesByVcsRoot(Project project, VirtualFile[] affectedFiles) {
        return sortFilesByVcsRoot(project, Arrays.asList(affectedFiles)); 
    }
}
