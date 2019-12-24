package com.vortex.handbrake;

import com.vortex.handbrake.encoder.EncoderStrategiesFactory;
import com.vortex.handbrake.encoder.EncoderStrategy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.StringConverter;
import lombok.Data;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements Initializable {

    @FXML
    private Button convertButton;
    @FXML
    private Button terminateButton;
    @FXML
    private Button cleanButton;
    @FXML
    private TextField etaTextField;
    @FXML
    private ListView<FileItem> listView;
    @FXML
    private ListView<String> logListView;

    private FilesHelper filesHelper = new FilesHelper();
    private EncoderStrategiesFactory encoderStrategiesFactory = new EncoderStrategiesFactory();
    private AtomicBoolean conversionInProgress = new AtomicBoolean();
    private ObservableList<FileItem> filesToConvert = FXCollections.observableArrayList();

    private ObservableList<String> logLines = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setCellFactory(lv -> {
            TextFieldListCell<FileItem> cell = new TextFieldListCell<>();
            cell.setConverter(new StringConverter<FileItem>() {
                @Override
                public String toString(FileItem fileItem) {
                    String msg = "";
                    if (fileItem.getErrMsg() != null) {
                        msg = " - " + fileItem.getErrMsg();
                    } else if (fileItem.getConversionProgress() != null) {
                        msg = " - " + fileItem.getConversionProgress();
                    }
                    return fileItem.getFile().getAbsolutePath() + msg;
                }

                @Override
                public FileItem fromString(String string) {
                    return null;
                }
            });
            return cell;
        });

        listView.setItems(filesToConvert);
        logListView.setItems(logLines);
        redraw();
    }

    private EventHandler<DragEvent> getOnDragOver() {
        return event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        };
    }

    private EventHandler<DragEvent> getOnDragDropped() {
        return event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                dragboard.getFiles().stream()
                        .filter(f -> !alreadyExist(f))
                        .forEach(f -> filesToConvert.add(new FileItem(f)));
                redraw();
            }
            event.consume();
        };
    }

    private boolean alreadyExist(File file) {
        return filesToConvert.stream()
                .anyMatch(a -> a.file.getAbsolutePath().equals(file.getAbsolutePath()));
    }

    private void redraw() {
        cleanButton.disableProperty().set(conversionInProgress.get() || filesToConvert.isEmpty());
        convertButton.disableProperty().set(conversionInProgress.get() || filesToConvert.isEmpty());
        terminateButton.disableProperty().set(!conversionInProgress.get());
        if (conversionInProgress.get()) {
            listView.setOnDragOver(Event::consume);
            listView.setOnDragDropped(Event::consume);
        } else {
            listView.setOnDragOver(getOnDragOver());
            listView.setOnDragDropped(getOnDragDropped());
        }
    }

    private void convertAnyNonConverted() {
        FileItem nextFileToConvert = getNextFileToConvert();
        if (nextFileToConvert != null) {
            File srcFile = nextFileToConvert.getFile();
            conversionInProgress.set(true);
            redraw();
            new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        File dstFile = encoderStrategiesFactory.create(srcFile)
                                .encode(srcFile,
                                        logLine -> Platform.runLater(() -> logLines.add(logLine)),
                                        progress -> updatePrgrs(nextFileToConvert, progress));
                        filesHelper.copyAttributes(srcFile, dstFile);
                        updatePrgrs(nextFileToConvert, "100%");
                    } catch (Exception e) {
                        nextFileToConvert.setErrMsg(e.getMessage());
                        listView.refresh();
                    }
                    convertAnyNonConverted();
                    return null;
                }
            }).start();
        } else {
            conversionInProgress.set(false);
            redraw();
        }
    }

    private FileItem getNextFileToConvert() {
        for (FileItem fileItem : filesToConvert) {
            if (fileItem.getErrMsg() == null && fileItem.getConversionProgress() == null) {
                return fileItem;
            }
        }
        return null;
    }

    private void updatePrgrs(FileItem fileItem, String newProgress) {
        fileItem.setConversionProgress(newProgress);
        listView.refresh();
    }

    @FXML
    private void convertButtonClicked() {
        AlertBox box = new AlertBox();
        box.createAndShow("Start conversion?", AlertBox.Type.YES_CANCEL);
        if (box.isYesPressed()) {
            logLines.clear();
            convertAnyNonConverted();
        }
    }

    @FXML
    private void terminateButtonClicked() {
        // TODO
    }

    @FXML
    private void cleanButtonClicked() {
        if (!conversionInProgress.get()) {
            filesToConvert.clear();
            logLines.clear();
            redraw();
        }
    }

    @Data
    private static class FileItem {
        private File file;
        private String conversionProgress;
        private String errMsg;

        FileItem(File file) {
            this.file = file;
        }
    }
}
