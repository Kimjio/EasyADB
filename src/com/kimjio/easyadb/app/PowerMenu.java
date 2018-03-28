package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
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

import java.io.IOException;

public class PowerMenu extends Application implements Command<Void> {

    private final Main main;
    private Stage primaryStage;
    private final GridPane grid;
    private final JFXButton powerOffButton,restartButton, recoveryButton, bootloaderButton;
    private final Parent root = FXMLLoader.load(getClass().getResource("/layout/PowerMenu.fxml"));
    private String deviceID;
    private final String userID, userName;

    public PowerMenu(Main main, String deviceID, String userID, String userName) throws IOException {
        this.main = main;
        this.deviceID = deviceID;
        this.userID = userID;
        this.userName = userName;
        this.grid = (GridPane) root.lookup("#root");
        this.powerOffButton = (JFXButton) root.lookup("#powerOffButton");
        this.restartButton = (JFXButton) root.lookup("#restartButton");
        this.recoveryButton = (JFXButton) root.lookup("#recoveryButton");
        this.bootloaderButton = (JFXButton) root.lookup("#bootloaderButton");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("전원");
        primaryStage.getIcons().add(new Image("/drawable/ic_launcher.png"));
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        JFXDecorator decorator = new JFXDecorator(this.primaryStage, root, false, false, false);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                primaryStage.close();
            }
        });

        Scene scene = new Scene(decorator);

        powerOffButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "-s", deviceID, "reboot", "-p");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "disconnect", deviceID);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                closeAll();
            }
        });

        restartButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "-s", deviceID, "reboot");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "disconnect", deviceID);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                closeAll();
            }
        });
        recoveryButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "-s", deviceID, "reboot", "recovery");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "disconnect", deviceID);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                closeAll();
            }
        });
        bootloaderButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "-s", deviceID, "reboot", "bootloader");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runProcess("adb", "disconnect", deviceID);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                closeAll();
            }
        });

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(PowerMenu.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                PowerMenu.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                PowerMenu.class.getResource("/css/easyadb2.css").toExternalForm());
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void closeAll() {
        this.main.close();
        this.primaryStage.close();
        try {
            new DeviceSelect(userID, userName).start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void runProcess(String... command) throws IOException, InterruptedException {
        //TODO
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();
        process.waitFor();
        return null;
    }
}
