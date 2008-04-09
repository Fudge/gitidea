package com.aspiro.git;

import com.intellij.openapi.vcs.vfs.VcsFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mike
 *
 * Modified for git by Erlend Simonsen
 */
public class GitFileSystem extends VcsFileSystem {
    @NonNls
    final static String PROTOCOL = "git";
    static final String PREFIX_REV_GRAPH = "revg";

    private Map<String, VirtualFile> cachedFiles = new HashMap<String, VirtualFile>();

    public VirtualFile findFileByPath(@NotNull final String path) {
        if (cachedFiles.containsKey(path)) return cachedFiles.get(path);

        final String[] strings = path.split("#");
        final String prefix = strings[0];
        final String actualPath = strings[1];

        final GitVirtualFile file = new GitVirtualFile(this, prefix, actualPath, path);
        cachedFiles.put(path, file);
        return file;
    }

    public static String getRevisionGraphUrl(String filePath) {
        return PROTOCOL + "://" + PREFIX_REV_GRAPH + "#" + filePath;
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    @NotNull
    public String getComponentName() {
        return "GitFileSystem";
    }

}
