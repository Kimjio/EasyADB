package com.kimjio.easyadb.app.controller.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.kimjio.easyadb.tool.FontTools;
import com.kimjio.easyadb.app.Main;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class IPConnectDialog extends Dialog<String> {

    /**************************************************************************
     *
     * Fields
     *
     **************************************************************************/

    private final GridPane grid;
    private final Label label;
    private final JFXTextField textField;
    private final String defaultValue;

    /**************************************************************************
     *
     * Constructors
     *
     **************************************************************************/

    /**
     * Creates a new APKSelectDialog without a default value entered into the
     * dialog {@link JFXTextField}.
     */
    public IPConnectDialog() {
        this("");
    }

    /**
     * Creates a new APKSelectDialog with the default value entered into the
     * dialog {@link JFXTextField}.
     */
    public IPConnectDialog(@NamedArg("defaultValue") String defaultValue) {
        final DialogPane dialogPane = getDialogPane();

        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        // -- textfield
        this.textField = new JFXTextField(defaultValue);
        this.textField.setPromptText("IP 주소");
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("필수");
        this.textField.setValidators(requiredFieldValidator);
        this.textField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(this.textField, new Insets(0,0,15,0));
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);


        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image("/drawable/ic_confirmation_title.png"));
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
        final ObservableList<String> stylesheets = dialogPane.getStylesheets();

        stylesheets.addAll(IPConnectDialog.class.getResource("/css/dialog.css").toExternalForm());
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final JFXButton okButton = (JFXButton) dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                this.textField.validate();
            }
        });

        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (textField.getText().length() <= 0 ) {
                this.textField.validate();
            }
            okButton.setDisable(textField.getText().length() <= 0);
        });


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
     * Returns the {@link JFXTextField} used within this dialog.
     */
    public final JFXTextField getEditor() {
        return textField;
    }

    /**
     * Returns the default value that was specified in the constructor.
     */
    public final String getDefaultValue() {
        return defaultValue;
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
        getDialogPane().setContent(grid);

        Platform.runLater(textField::requestFocus);
    }
}
