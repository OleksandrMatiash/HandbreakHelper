package com.vortex.handbrake.encoder;

import com.vortex.handbrake.FilesHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoEncoderStrategy implements EncoderStrategy {

    private FilesHelper filesHelper = new FilesHelper();

    @Override
    public File encode(File srcFile, Consumer<String> logConsumer, Consumer<String> progressConsumer) {
        try {
            File dstFile = getDstFile(srcFile);
            Process process = Runtime.getRuntime().exec(filesHelper.getFileFullPath("/IrfanView/i_view64.exe")
                    + " \"" + srcFile.getAbsolutePath() + "\""
                    + " /convert=\"" + dstFile.getAbsolutePath() + "\""
                    + " /jpgq=70" // quality setting, should be from 1 to 100
                    + " /silent"
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
        int lastIndex = absolutePath.lastIndexOf('.');
        String pathWithoutExtension = absolutePath.substring(0, lastIndex);
        String extension = absolutePath.substring(lastIndex + 1);
        File result = new File(pathWithoutExtension + "_." + extension);
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
