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

    public File getDstFile(File srcFile) {
        String absolutePath = srcFile.getAbsolutePath();
        String pathWithoutExtension = absolutePath.substring(0, absolutePath.lastIndexOf("."));
        return new File(pathWithoutExtension + "_.mp4");
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

    public String getFileFullPath(String relativePathToFile) {
        try {
            return new File(URLDecoder.decode(Encoder.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8").substring(1))
                    .getParentFile().getAbsoluteFile() + relativePathToFile;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
