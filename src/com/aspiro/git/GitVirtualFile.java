package com.aspiro.git;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author mike
 *
 * Modified for Git by Erlend Simonsen
*/
class GitVirtualFile extends VirtualFile {
    private final String prefix;
    private final String actualPath;
    private final String path;
    private GitFileSystem gitFileSystem;

    public GitVirtualFile(GitFileSystem gitFileSystem, String prefix, String actualPath, String path) {
        this.gitFileSystem = gitFileSystem;
        this.prefix = prefix;
        this.actualPath = actualPath;
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    @NotNull
        @NonNls
        public String getName() {
        return actualPath.substring(actualPath.lastIndexOf("/"));
    }

    @NotNull
        public VirtualFileSystem getFileSystem() {
        return gitFileSystem;
    }

    public String getPath() {
        return path;
    }

    @NotNull
        public String getUrl() {
        return GitFileSystem.PROTOCOL + "://" + path;
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isDirectory() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    @Nullable
        public VirtualFile getParent() {
        return null;
    }

    public VirtualFile[] getChildren() {
        throw new UnsupportedOperationException("Method getChildren not implemented in " + getClass());
    }

    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        throw new UnsupportedOperationException("Method getOutputStream not implemented in " + getClass());
    }

    public byte[] contentsToByteArray() throws IOException {
        throw new UnsupportedOperationException("Method contentsToByteArray not implemented in " + getClass());
    }

    public long getTimeStamp() {
        return 0;
    }

    public long getLength() {
        throw new UnsupportedOperationException("Method getLength not implemented in " + getClass());
    }

    public void refresh(boolean asynchronous, boolean recursive, Runnable postRunnable) {
        throw new UnsupportedOperationException("Method refresh not implemented in " + getClass());
    }

    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Method getInputStream not implemented in " + getClass());
    }
}
