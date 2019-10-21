package com.vortex.handbrake;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    private boolean yesPressed;

    public void createAndShow(String msg, Type type) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        Label label = new Label(msg);
        VBox vbox = new VBox();
        if (Type.CLOSE == type) {
            Button closeBtn = new Button("CLOSE");
            closeBtn.setOnAction(event -> window.close());
            vbox.getChildren().addAll(label, closeBtn);
        } else if (Type.YES_CANCEL == type) {
            Button yesBtn = new Button("YES");
            yesBtn.setOnAction(event -> {
                yesPressed = true;
                window.close();
            });
            Button cancelBtn = new Button("CANCEL");
            cancelBtn.setOnAction(event -> window.close());
            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(yesBtn, cancelBtn);
            vbox.getChildren().addAll(label, hBox);
        }

        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setSpacing(20);

        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.showAndWait();
    }

    public boolean isYesPressed() {
        return yesPressed;
    }

    public enum Type {
        CLOSE, YES_CANCEL
    }
}
