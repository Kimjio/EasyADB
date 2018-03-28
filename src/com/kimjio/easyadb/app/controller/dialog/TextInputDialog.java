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

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A dialog that shows a text input control to the user.
 *
 * @see javafx.scene.control.Dialog
 * @since JavaFX 8u40
 */
public class TextInputDialog extends Dialog<String> {

    /**************************************************************************
     *
     * Fields
     *
     **************************************************************************/

    private final GridPane grid;
    private final Label label;
    private final JFXTextField[] textFields = new JFXTextField[3];
    private final String defaultValue;
    private static int index = 0;
    private static int colIndex = 0;


    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    /**
     * Creates a new TextInputDialog without a default value entered into the
     * dialog {@link TextField}.
     */
    public TextInputDialog() {
        this("");
    }

    /**
     * Creates a new TextInputDialog with the default value entered into the
     * dialog {@link TextField}.
     */
    public TextInputDialog(@NamedArg("defaultValue") String defaultValue) {
        final DialogPane dialogPane = getDialogPane();

        // -- textfield
        this.textFields[index] = new JFXTextField(defaultValue);
        this.textFields[index].setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(textFields[index], Priority.ALWAYS);
        GridPane.setFillWidth(textFields[index], true);

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

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final ObservableList<String> stylesheets = dialogPane.getStylesheets();

        stylesheets.addAll(IPConnectDialog.class.getResource("/css/dialog.css").toExternalForm());

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? textFields[index].getText() : null;
        });
    }



    /**************************************************************************
     *
     * Public API
     *
     **************************************************************************/



    public final void setValidators(int id, RequiredFieldValidator... requiredFieldValidators) {
        this.textFields[id].setValidators(requiredFieldValidators);
    }

    /**
     * Returns the {@link TextField} used within this dialog.
     */
    public final JFXTextField getEditor(int id) {
        return textFields[id];
    }

    /**
     * Returns the default value that was specified in the constructor.
     */
    public final String getDefaultValue() {
        return defaultValue;
    }

    public final void addTextFields(String defaultValue) {
        textFields[++index] = new JFXTextField(defaultValue);
        GridPane.setHgrow(textFields[index], Priority.ALWAYS);
        GridPane.setFillWidth(textFields[index], true);
        grid.add(textFields[index], colIndex++, 0);
    }


    /**************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, colIndex++, 0);
        grid.add(textFields[index], colIndex++, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> textFields[index].requestFocus());
    }
}

