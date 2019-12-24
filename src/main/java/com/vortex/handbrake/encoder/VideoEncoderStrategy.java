package com.vortex.handbrake.encoder;

import com.vortex.handbrake.FilesHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoEncoderStrategy implements EncoderStrategy {
    private static final Pattern PATTERN = Pattern.compile(".*\\s(\\d+\\.\\d+)\\s%.*");

    private FilesHelper filesHelper = new FilesHelper();

    @Override
    public File encode(File srcFile, Consumer<String> logConsumer, Consumer<String> progressConsumer) {
        try {
            File dstFile = getDstFile(srcFile);
            Process process = Runtime.getRuntime().exec(filesHelper.getFileFullPath("/Handbrake/HandbrakeCLI.exe")
                            + " -i \"" + srcFile.getAbsolutePath() + "\""
                            + " -o \"" + dstFile.getAbsolutePath() + "\""
                            + " -t 1"
                            + " --angle 1"
                            + " -c 1"
                            + " -f mp4"
//                + " -w 1280"
                            + " --crop 0:0:0:0"
                            + " --loose-anamorphic"
                            + " --modulus 2 -e x264 -q 22 --vfr -a 1 -E av_aac -6 stereo -R Auto "
                            + "-B 224 -D 0 --gain 0 --audio-fallback ac3 --encoder-preset=veryfast  --encoder-level=\"4.0\"  --encoder-profile=main  --verbose=1"
            );
            BufferedReader readerErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader readerOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            boolean processIsAlive = true;
            boolean thereIsSomethingToRead = false;
            while (processIsAlive || thereIsSomethingToRead) {
                thereIsSomethingToRead = false;
                if (readerErr.ready()) {
                    thereIsSomethingToRead = true;
                    String errLine = readerErr.readLine();
                    printToWriter(logConsumer, errLine);
                    System.out.println("[ERR] " + errLine);
                }

                if (readerOut.ready()) {
                    thereIsSomethingToRead = true;
                    String outLine = readerOut.readLine();
                    printToWriter(logConsumer, outLine);
                    System.out.println("[OUT] " + outLine);
                    if (progressConsumer != null) {
                        Matcher matcher = PATTERN.matcher(outLine);
                        if (matcher.matches()) {
                            progressConsumer.accept(matcher.group(1) + "%");
                        }
                    }
                }

                processIsAlive = process.isAlive();
            }
            readerErr.close();
            return dstFile;
        } catch (IOException ex) {
            printToWriter(logConsumer, ex.getMessage());
            System.out.println(ex.getMessage());
            throw new IllegalArgumentException("error");
        }
    }

    private File getDstFile(File srcFile) {
        String absolutePath = srcFile.getAbsolutePath();
        String pathWithoutExtension = absolutePath.substring(0, absolutePath.lastIndexOf('.'));
        File result = new File(pathWithoutExtension + "_.mp4");
        if (result.exists()) {
            throw new IllegalArgumentException("DST file already exists");
        }
        return result;
    }

    private void printToWriter(Consumer<String> logConsumer, String outLine) {
        if (logConsumer != null && outLine != null) {
            logConsumer.accept(outLine);
        }
    }
}
