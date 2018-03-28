package com.kimjio.easyadb.app.controller.dialog;

/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * A dialog that shows a text input control to the user.
 *
 * @see javafx.scene.control.Dialog
 * @since JavaFX 8u40
 */
public class APKSelectDialog extends Dialog<String> {

    /**************************************************************************
     *
     * Fields
     *
     **************************************************************************/

    private final GridPane grid;
    private final Label label;
    private final JFXTextField textField;
    private final JFXButton jfxButton;
    private final String defaultValue;
    private String apkName;



    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    /**
     * Creates a new APKSelectDialog without a default value entered into the
     * dialog {@link TextField}.
     */
    public APKSelectDialog() {
        this("");
    }

    /**
     * Creates a new APKSelectDialog with the default value entered into the
     * dialog {@link TextField}.
     */
    public APKSelectDialog(@NamedArg("defaultValue") String defaultValue) {
        final DialogPane dialogPane = getDialogPane();

        // -- textfield
        this.textField = new JFXTextField(defaultValue);
        this.textField.setMaxWidth(Double.MAX_VALUE);
        this.textField.setPromptText("APK 경로");
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);

        this.jfxButton = new JFXButton("...");
        this.jfxButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open APK File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("APK", "*.apk"),
                        new FileChooser.ExtensionFilter("모든 파일", "*.*")
                );
                File file = fileChooser.showOpenDialog(dialogPane.getScene().getWindow());
                textField.setText(file != null ? file.getPath() : textField.getText());
                apkName = file != null ? file.getName() : "";
            }
        });

        // -- label
        label = DialogPane.createContentLabel(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        this.defaultValue = defaultValue;

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/drawable/ic_launcher.png"));
        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        final ObservableList<String> stylesheets = dialogPane.getStylesheets();

        stylesheets.addAll(IPConnectDialog.class.getResource("/css/dialog.css").toExternalForm());
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? textField.getText() : null;
        });
    }



    /**************************************************************************
     *
     * Public API
     *
     **************************************************************************/

    /**
     * Returns the {@link TextField} used within this dialog.
     */
    public final TextField getEditor() {
        return textField;
    }

    /**
     * Returns the default value that was specified in the constructor.
     */
    public final String getDefaultValue() {
        return defaultValue;
    }

    public final String getFileName() {
        return apkName;
    }

    public final void setTextFieldEditable(boolean editable) {
        textField.setEditable(editable);
    }
    public final void setTextFieldText(String text) {
        textField.setText(text);
    }

    /**************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        grid.add(jfxButton, 2, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> textField.requestFocus());
    }
}

