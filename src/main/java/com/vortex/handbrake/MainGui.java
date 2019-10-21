package com.vortex.handbrake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Properties;

public class MainGui extends Application {

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("gui/main_panel.fxml");
        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream("project.properties"));

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("HandbrakeHelper v." + properties.getProperty("version"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
