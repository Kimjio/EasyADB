package com.kimjio.easyadb.app.controller.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class ProgressDialog extends Dialog<Void> {
    /**************************************************************************
     *
     * Fields
     *
     **************************************************************************/

    private final GridPane grid;
    private final Label label;
    private final JFXProgressBar jfxProgressBar;
    //private final JFXTextField textField;


    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    public ProgressDialog() {
        this(-1);
    }

    /**
     * Creates a new APKSelectDialog without a default value entered into the
     * dialog {@link TextField}.
     */
    public ProgressDialog(double progress) {
        final DialogPane dialogPane = getDialogPane();

        /*// -- textfield
        this.textField = new JFXTextField(defaultValue);
        this.textField.setMaxWidth(Double.MAX_VALUE);
        this.textField.setPromptText("APK 경로");
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);*/
        jfxProgressBar = new JFXProgressBar();
        GridPane.setHgrow(jfxProgressBar, Priority.ALWAYS);
        GridPane.setFillWidth(jfxProgressBar, true);
        // -- label
        label = new Label();
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.setFont(new Font(10));
        //label.textProperty().bind(dialogPane.contentTextProperty());

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        if (!(progress < 0)) {
            jfxProgressBar.setProgress(progress);
            label.setText(progress+"%");
        }
        /*Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(jfxProgressBar.progressProperty(), 0)),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(jfxProgressBar.secondaryProgressProperty(), 1)),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(jfxProgressBar.progressProperty(), 1)));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();*/

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        final ObservableList<String> stylesheets = dialogPane.getStylesheets();

        stylesheets.addAll(IPConnectDialog.class.getResource("/css/dialog.css").toExternalForm());
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

    }


    /**************************************************************************
     *
     * Public API
     *
     **************************************************************************/

    public final void updateProgress(double progress) {
        jfxProgressBar.setProgress(progress);
        label.setText(progress+"%");
    }

    /**************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(jfxProgressBar, 0, 0);
        grid.add(label, 0, 1);
        /*
        grid.add(jfxButton, 2, 0);*/
        getDialogPane().setContent(grid);

        //Platform.runLater(() -> textField.requestFocus());
    }
}
