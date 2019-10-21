package com.vortex.handbrake;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesHelper {

    public File getDstFile(File srcFile) {
        return new File(srcFile.getAbsolutePath() + ".mp4");
    }

    public boolean copyAttributes(File srcFile, File dstFile) {
        try {
            BasicFileAttributes srcAttr = Files.getFileAttributeView(srcFile.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
            BasicFileAttributeView dstAttrView = Files.getFileAttributeView(dstFile.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

            dstAttrView.setTimes(srcAttr.lastModifiedTime(), srcAttr.lastAccessTime(), srcAttr.creationTime());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
