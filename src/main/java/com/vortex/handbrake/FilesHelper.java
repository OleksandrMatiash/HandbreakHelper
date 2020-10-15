package com.vortex.handbrake;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesHelper {

    private FilesHelper() {
    }

    public static boolean copyAttributes(File srcFile, File dstFile) {
        try {
            BasicFileAttributes srcAttr = Files.getFileAttributeView(srcFile.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
            BasicFileAttributeView dstAttrView = Files.getFileAttributeView(dstFile.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

            dstAttrView.setTimes(srcAttr.lastModifiedTime(), srcAttr.lastAccessTime(), srcAttr.creationTime());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getFileFullPath(String relativePathToFile) {
        try {
            return new File(URLDecoder.decode(FilesHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8").substring(1))
                    .getParentFile().getAbsoluteFile() + relativePathToFile;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void deleteFile(File dstFile) {
        if (dstFile != null && dstFile.exists()) {
            for (int i = 0; i < 10; i++) {
                boolean delete = dstFile.delete();
                if (delete) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
