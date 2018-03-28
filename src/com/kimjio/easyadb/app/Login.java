package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.kimjio.easyadb.app.controller.dialog.Alert;
import com.kimjio.easyadb.app.controller.dialog.TextInputDialog;
import com.kimjio.easyadb.tool.FontTools;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Login extends Application {

    private Connection connection; //연결 유지 객체
    private PreparedStatement preparedStatement; // 문장 연결 객체
    private ResultSet resultSet;// 결과 값 담는 객체

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/Login.fxml"));
        primaryStage.getIcons().add(new Image("/drawable/ic_launcher.png"));
        primaryStage.setTitle("로그인");
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, false, false);
        decorator.setCustomMaximize(true);
        JFXTextField textField = (JFXTextField) root.lookup("#id");
        JFXButton loginButton = (JFXButton) root.lookup("#login");
        JFXButton registerButton = (JFXButton) root.lookup("#register");
        loginButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_id", "client", "0000");
                    preparedStatement = connection.prepareStatement("SELECT * from userid");
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        if (resultSet.getString(1).equals(textField.getText())) {
                            new DeviceSelect(resultSet.getString(1), resultSet.getString(2)).start(new Stage());
                            primaryStage.close();
                            connection.close();
                            return;
                        }
                    }
                } catch (ClassNotFoundException | SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        registerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setHeaderText(null);
                RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
                requiredFieldValidator.setMessage("필수");
                inputDialog.setValidators(0, requiredFieldValidator);
                inputDialog.getEditor(0).setPromptText("사용자 ID");
                inputDialog.getEditor(0).focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) { // we only care about loosing focus
                        inputDialog.getEditor(0).validate();
                    }
                });

                inputDialog.getEditor(0).textProperty().addListener((observable, oldValue, newValue) -> {
                    if (inputDialog.getEditor(0).getText().length() <= 1) {
                        inputDialog.getEditor(0).validate();
                    }
                    inputDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(inputDialog.getEditor(0).getText().length() <= 0);
                });
                inputDialog.addTextFields("");
                inputDialog.setValidators(1, requiredFieldValidator);
                inputDialog.getEditor(1).setPromptText("사용자 이름");
                inputDialog.getEditor(1).focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) { // we only care about loosing focus
                        inputDialog.getEditor(1).validate();
                    }
                });

                inputDialog.getEditor(1).textProperty().addListener((observable, oldValue, newValue) -> {
                    if (inputDialog.getEditor(1).getText().length() <= 1) {
                        inputDialog.getEditor(1).validate();
                    }
                    inputDialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(inputDialog.getEditor(1).getText().length() <= 0);
                });
                if (inputDialog.showAndWait().isPresent()) {
                    String id = inputDialog.getEditor(0).getText();
                    String name = inputDialog.getEditor(1).getText();
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_id", "client", "0000");
                        preparedStatement = connection.prepareStatement("SELECT * from userid");
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            if (resultSet.getString(1).equals(id)) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText(null);
                                alert.setContentText("ID가 이미 존재합니다.");
                                alert.show();
                                return;
                            }
                        }
                        connection.prepareStatement("insert into userID values(" + "'" + id + "'" + "," + "'" + name + "'" + ")").execute();
                        connection.close();
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Scene scene = new Scene(decorator);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(Login.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                Login.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Login.class.getResource("/css/easyadb3.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
