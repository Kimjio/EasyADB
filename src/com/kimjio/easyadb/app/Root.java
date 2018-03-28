package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.kimjio.easyadb.app.controller.dialog.Alert;
import com.kimjio.easyadb.tool.Command;
import com.kimjio.easyadb.tool.FontTools;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Root extends Application implements Command {

    private final Main main;
    private final JFXButton rootButton, unRootButton;
    private final Parent root = FXMLLoader.load(getClass().getResource("/layout/Root.fxml"));
    private String deviceID;

    public Root(Main main, String deviceID) throws IOException {
        this.main = main;
        this.deviceID = deviceID;
        this.rootButton = (JFXButton) root.lookup("#rootButton");
        this.unRootButton = (JFXButton) root.lookup("#unRootButton");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("루트");
        primaryStage.getIcons().add(new Image("/drawable/ic_security_black.png"));
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, false, false);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                primaryStage.close();
            }
        });

        Scene scene = new Scene(decorator);

        rootButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                synchronized (Root.this) {
                    final boolean[] isUserBuild = {false};
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (Root.this) {
                                try {
                                    isUserBuild[0] = runProcess("adb", "-s", deviceID, "root");
                                    Root.this.notify();
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    try {
                        Root.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isUserBuild[0]) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("안드로이드 빌드 타입이 user(배포용) 입니다.\n타입(userdebug 또는 eng)을 수정하거나\n또는, 사용자 펌웨어를 설치해야 할 수 있습니다.\n ");
                        alert.show();
                    } else {
                        main.setRootState(true);
                    }
                }
            }
        });
        unRootButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                synchronized (Root.this) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (Root.this) {
                                try {
                                    runProcess("adb", "-s", deviceID, "unroot");
                                    Root.this.notify();
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }).start();
                    try {
                        Root.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    main.setRootState(false);
                }
            }
        });

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(PowerMenu.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                PowerMenu.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                PowerMenu.class.getResource("/css/easyadb2.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public Boolean runProcess(String... command) throws IOException, InterruptedException {
        //TODO
        boolean isUserBuild = false;
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            isUserBuild = line.equals("adbd cannot run as root in production builds");
        }
        process.waitFor();

        bufferedReader.close();
        return isUserBuild;
    }
}
