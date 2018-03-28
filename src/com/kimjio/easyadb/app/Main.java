package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXPopup;
import com.kimjio.easyadb.app.controller.dialog.APKSelectDialog;
import com.kimjio.easyadb.app.controller.dialog.Alert;
import com.kimjio.easyadb.app.controller.dialog.ProgressDialog;
import com.kimjio.easyadb.app.controller.dialog.TextInputDialog;
import com.kimjio.easyadb.tool.Command;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Optional;

public class Main extends Application implements Command<String>{

    private boolean rootState = false;
    private Label rootStateLabel;
    private String deviceID;
    private String deviceModel;
    private Stage primaryStage;
    private final String adbLocal;
    private final String userID;
    private String userName;

    public Main(String deviceID, String deviceModel, String adbLocal, String userID, String userName) {
        this.deviceID = deviceID;
        this.deviceModel = deviceModel;
        this.adbLocal = adbLocal;
        this.userID = userID;
        this.userName = userName;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/Main.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.getIcons().add(new Image("/drawable/ic_launcher.png"));
        this.primaryStage.setTitle("EasyADB");
        JFXDecorator decorator = new JFXDecorator(this.primaryStage, root, false, false, true);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("종료할까요?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.orElse(null) == ButtonType.OK) {
                    System.exit(0);
                }
            }
        });
        Label deviceConnected = (Label) root.lookup("#deviceConnected");
        deviceConnected.setText(deviceModel + " 연결 됨");
        Label userNameLabel = (Label) root.lookup("#userName");
        userNameLabel.setText("Hello, "+userName);
        userNameLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setHeaderText(null);
                textInputDialog.setTitle("이름 변경");
                textInputDialog.getEditor(0).setPromptText("사용자 이름");
                if (textInputDialog.showAndWait().isPresent()) {
                    if (textInputDialog.getEditor(0).getText().isEmpty()||userName.equals(textInputDialog.getEditor(0).getText()))
                        return;
                    else {
                        //TODO
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_id", "client", "0000");

                            connection.prepareStatement("update userID set name = "+"'"+textInputDialog.getEditor(0).getText()+"'"+" where id = "+"'"+userID+"'").execute();
                            userName = textInputDialog.getEditor(0).getText();
                            userNameLabel.setText("Hello, "+textInputDialog.getEditor(0).getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        rootStateLabel = (Label) root.lookup("#rootState");
        rootStateLabel.setText("ROOT : NO");

        JFXButton logcatButton = (JFXButton) root.lookup("#logcatButton");
        logcatButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new Logcat(deviceID, userID).start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        JFXButton apkButton = (JFXButton) root.lookup("#apkButton");
        apkButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                APKSelectDialog apkSelectDialog = new APKSelectDialog();
                apkSelectDialog.setTextFieldEditable(false);
                apkSelectDialog.setHeaderText(null);
                if (apkSelectDialog.showAndWait().isPresent()) {
                    synchronized (Main.this) {
                        final String[] result = new String[1];
                        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                        alertInfo.setHeaderText(null);
                        alertInfo.setContentText("잠시만 기다려주세요...\n연결 방식에 따라 다를 수 있습니다.");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    result[0] = runProcess(adbLocal, "-s", deviceID, "shell", "install", apkSelectDialog.getResult());
                                    //Main.this.notify();
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!result[0].equals("OK")) {
                                                alertInfo.close();
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setHeaderText(null);
                                                alert.setContentText("오류 :"+ result[0]);
                                            }
                                        }
                                    });
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
        JFXButton rootButton = (JFXButton) root.lookup("#rootButton");
        rootButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new Root(Main.this, deviceID).start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        JFXButton powerMenu = (JFXButton) root.lookup("#powerButton");
        powerMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new PowerMenu(Main.this, deviceID, userID, userName).start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JFXButton aboutButton = (JFXButton) root.lookup("#about");
        aboutButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new 한글().start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Scene scene = new Scene(decorator);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(Main.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/easyadb.css").toExternalForm());
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void setRootState(boolean rootState) {
        this.rootState = rootState;
        updateRootState();
    }

    private void updateRootState() {
        if (this.rootState) {
            rootStateLabel.setText("ROOT : YES");
        } else {
            rootStateLabel.setText("ROOT : NO");
        }
    }

    public void close() {
        primaryStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public String runProcess(String... command) throws IOException, InterruptedException {
        String error = null;

        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("Failure [")) {
                error = line.substring(line.indexOf("["), line.indexOf("]"));
            }
        }
        process.waitFor();

        bufferedReader.close();
        return error != null ? error : "OK";
    }
}
