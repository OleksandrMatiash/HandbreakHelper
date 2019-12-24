package com.vortex.handbrake.encoder;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;

public class EncoderStrategiesFactory {

    private static final List<String> VIDEO_FILES = asList("mov", "mp4");
    private static final List<String> PHOTO_FILES = asList("jpg", "jpeg", "png");

    private final EncoderStrategy videoEncoderStrategy;
    private final EncoderStrategy photoEncoderStrategy;

    public EncoderStrategiesFactory() {
        this.videoEncoderStrategy = new VideoEncoderStrategy();
        this.photoEncoderStrategy = new PhotoEncoderStrategy();
    }

    public EncoderStrategy create(File fileToConvert) {
        validate(fileToConvert);
        String fileExtension = getFileExtension(fileToConvert);
        if (VIDEO_FILES.stream().anyMatch(file -> file.equals(fileExtension))) {
            return videoEncoderStrategy;
        }
        if (PHOTO_FILES.stream().anyMatch(file -> file.equals(fileExtension))) {
            return photoEncoderStrategy;
        }
        System.out.println("[ERR] not supported format: " + fileExtension);
        throw new IllegalArgumentException("not supported format: " + fileExtension);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf('.');
        if (lastIndexOf > 0) {
            return name.substring(lastIndexOf + 1).toLowerCase();
        }
        return "";
    }

    private void validate(File fileToConvert) {
        if (!fileToConvert.isFile()) {
            throw new IllegalArgumentException("file " + fileToConvert + " is not a file");
        }
        if (!fileToConvert.exists()) {
            throw new IllegalArgumentException("file " + fileToConvert + " doesn't exist");
        }
    }

}
