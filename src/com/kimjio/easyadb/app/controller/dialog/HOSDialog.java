package com.kimjio.easyadb.app.controller.dialog;

import com.jfoenix.controls.JFXButton;
import com.kimjio.easyadb.tool.FontTools;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HOSDialog extends Dialog<ButtonType> {

    public HOSDialog(int speed, boolean autoReverse) {

        final DialogPane dialogPane = getDialogPane();

        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image("/drawable/ic_heroes.png"));

        setTitle("히오스");
        dialogPane.setContentText("히오스 대화상자");

        Rectangle rect = new Rectangle(32, 32);
        rect.setFill(new ImagePattern(new Image("/drawable/ic_heroes.png")));
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), rect);
        rotateTransition.setByAngle(360 * speed);
        rotateTransition.setCycleCount(-1);
        rotateTransition.setAutoReverse(autoReverse);
        rotateTransition.play();

        setGraphic(rect);

        final ObservableList<String> stylesheets = dialogPane.getStylesheets();

        stylesheets.addAll(HOSDialog.class.getResource("/css/dialog.css").toExternalForm());
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final JFXButton okButton = (JFXButton) dialogPane.lookupButton(ButtonType.OK);
        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        setResultConverter((dialogButton) -> dialogButton);
    }
}
