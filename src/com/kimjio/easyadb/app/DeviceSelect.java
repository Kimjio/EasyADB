package com.kimjio.easyadb.app;

import com.jfoenix.controls.*;
import com.kimjio.easyadb.app.controller.dialog.HOSDialog;
import com.kimjio.easyadb.app.controller.dialog.ProgressDialog;
import com.kimjio.easyadb.tool.FontTools;
import com.kimjio.easyadb.app.controller.dialog.Alert;
import com.kimjio.easyadb.app.controller.dialog.IPConnectDialog;
import com.kimjio.easyadb.tool.Command;
import com.kimjio.easyadb.app.data.DeviceListData;
import com.kimjio.easyadb.tool.Tools;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceSelect extends Application implements Command {

    @FXML
    JFXListView listView = new JFXListView<>();

    private boolean isADBExited = true;

    private final String userID, userName;

    private String adbLocal = "adb";

    private RotateTransition rotateTransition;
    private Runnable deviceListRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                runProcess(adbLocal, "devices", "-l");
            } catch (IOException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        isADBExited = false;
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        //alert.getDialogPane().getStylesheets().add();
                        alert.setContentText("ADB를 찾지 못했습니다.");
                        alert.getDialogPane().getStyleClass().add("text-input-dialoga");
                        final ObservableList<String> stylesheets = alert.getDialogPane().getStylesheets();

                        stylesheets.addAll(Main.class.getResource("/css/dialog.css").toExternalForm());
                        Rectangle rect = new Rectangle(32, 32);
                        rect.setFill(new ImagePattern(new Image("/drawable/ic_error.png")));
                        alert.setGraphic(rect);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.orElse(null) == ButtonType.OK) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    System.exit(1);
                                }
                            });
                        }
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private Thread deviceListThread = new Thread(deviceListRunnable);

    private ObservableList<String> data = FXCollections.observableArrayList();

    private Tools<Boolean, String, String> tools = new Tools<Boolean, String, String>() {
        @Override
        public Boolean isStringEmpty(String string) {
            return string.equals("") || string.equals(" ");
        }

        @Override
        public String replace(String s, String[] targets, String replace) {
            for (String e : targets) {
                s = s.replace(e, replace);
            }
            return s;
        }
    };

    public DeviceSelect(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/DeviceSelect.fxml"));
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        FontTools.loadFont(getClass(), 0, FontTools.nanumCodingFonts);

        listView = (JFXListView) root.lookup("#listView");
        primaryStage.setTitle("장치 선택");
        primaryStage.getIcons().add(new Image("/drawable/ic_launcher.png"));
        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, false, true);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });

        Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
        double width = bounds.getWidth() / 5;
        double height = bounds.getHeight() / 3;

        JFXButton jfxRefreshButton = (JFXButton) root.lookup("#refresh");
        JFXButton jfxIPConnectButton = (JFXButton) root.lookup("#ip_connect");

        Rectangle rect = new Rectangle(24, 24);
        rect.setFill(new ImagePattern(new Image("/drawable/ic_refresh_black.png")));

        rotateTransition = new RotateTransition(Duration.millis(1000), rect);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(3);
        rotateTransition.setDelay(Duration.seconds(0));
        rotateTransition.setAutoReverse(true);

        jfxRefreshButton.setGraphic(rect);
        jfxRefreshButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deviceListRunnable.run();
                rotateTransition.play();
            }
        });
        jfxIPConnectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final boolean[] result = {false};
                if (isADBExited) {
                    IPConnectDialog ipConnectDialog = new IPConnectDialog("");
                    ipConnectDialog.setTitle("");
                    ipConnectDialog.setHeaderText(null);
                    if (ipConnectDialog.showAndWait().isPresent()) {
                        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("잠시만 기다려 주세요...");
                        infoAlert.show();
                        synchronized (DeviceSelect.this) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (DeviceSelect.this) {
                                        try {
                                            result[0] = runProcess2("adb", "connect", ipConnectDialog.getEditor().getText());
                                            DeviceSelect.this.notify();
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                            try {
                                DeviceSelect.this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        infoAlert.close();
                        if (!result[0]) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setContentText("장치를 찾을 수 없습니다.\nVPN 또는 장치의 IP 주소를 확인하십시오.");
                            alert.showAndWait();
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        runProcess(adbLocal, "devices", "-l");
                                    } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        Scene scene = new Scene(decorator, width, height);
        listView.setItems(data);
        listView.setCellFactory(new Callback<JFXListView, JFXListCell<DeviceListData>>() {
            @Override
            public JFXListCell<DeviceListData> call(JFXListView param) {
                return new JFXListCell<DeviceListData>() {
                    @Override
                    public void updateItem(DeviceListData deviceListData, boolean empty) {
                        super.updateItem(deviceListData, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);

                            GridPane grid = new GridPane();
                            grid.setHgap(10);
                            grid.setVgap(4);
                            grid.setPadding(new Insets(0, 10, 0, 10));

                            ImageView icon = new ImageView();
                            icon.setImage(new Image(deviceListData.getImageUrl()));
                            grid.add(icon, 0, 0, 1, 3);

                            Label deviceID = new Label(deviceListData.getDeviceID());
                            deviceID.setFont(Font.font(20));

                            grid.add(deviceID, 1, 0);

                            if (!deviceListData.isEmpty()) {
                                deviceID.setFont(new Font(20));

                                Label deviceModel = new Label(deviceListData.getDeviceModel());
                                grid.add(deviceModel, 1, 1);

                                Label deviceStatus = new Label(deviceListData.getDeviceStatus());
                                grid.add(deviceStatus, 1, 2);
                            }

                            setGraphic(grid);
                        }
                    }
                };
            }

        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                if (listView.getSelectionModel().getSelectedItems().toString().equals("[]")) return;
                DeviceListData deviceListData = (DeviceListData) listView.getSelectionModel().getSelectedItem();
                if (!deviceListData.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setContentText(deviceListData.getDeviceModel() + " 로 선택하시겠습니까?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.orElse(null) == ButtonType.OK) {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                try {
                                    new Main(deviceListData.getDeviceID(), deviceListData.getDeviceModel(), adbLocal, userID, userName).start(new Stage());
                                    primaryStage.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        });
        listView.setPrefSize(bounds.getWidth(), bounds.getHeight());
        root.getStyleClass().add("root");
        final ObservableList<String> stylesheets = scene.getStylesheets();

        stylesheets.addAll(DeviceSelect.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                DeviceSelect.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                DeviceSelect.class.getResource("/css/easyadb.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        deviceListThread.start();
    }

    @Override
    public Void runProcess(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
        String line;
        /*
        * 다음과 같은 상태가 있음
        *
        * 56c3555f : 장치 고유 ID, IP 연결의 경우 ip 주소가 표시 됨
        * 10.80.162.233:5555      device
        * 56c3555f        unauthorized
        * 56c3555f        device
        * 56c3555f        offline
        * 56c3555f        recovery
        *
        *
        * -l :
        * 56c3555f               device product:oneplus3 model:ONEPLUS_A3010 device:OnePlus3T
        * 10.80.162.233:5555     device product:angelfish model:LG_Watch_Style device:angelfish
        * */
        List<DeviceListData> deviceListData = new ArrayList<>();

        while ((line = bufferedReader.readLine()) != null) {
            boolean isOffline = false;
            if (line.equals("adb server is out of date.  killing...") || line.indexOf("*") == 0 || line.equals("List of devices attached") || line.equals("\n") || line.equals(""))
                continue;
            isOffline = line.contains(" unauthorized") || line.contains(" offline");
            String tmp = tools.replace(line.substring(23), new String[]{"_"}, " ");
            try {
                if (isOffline) {
                    deviceListData.add(new DeviceListData(line.substring(0, line.indexOf(" ")), "", "", "/drawable/ic_android_black.png", false, isOffline));
                } else {
                    deviceListData.add(new DeviceListData(line.substring(0, line.indexOf(" ")), tools.replace(tmp.substring(tmp.indexOf("model:"), tmp.indexOf("device:") - 1), new String[]{"product:", "model:", "device:"}, ""), tmp.substring(0, tmp.indexOf(" ")), "/drawable/ic_android_black.png", false, isOffline));
                }
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println(line);
            }

        }
        if (deviceListData.isEmpty()) {
            deviceListData.add(new DeviceListData("어떠한 장치도 발견되지 않음", "", "", "/drawable/ic_help_outline.png", true, true));
        }
        listView.getItems().remove(0, listView.getItems().size());

        for (DeviceListData aDeviceListData : deviceListData) {
            listView.getItems().add(aDeviceListData);
        }

        process.waitFor();

        bufferedReader.close();

        return null;
    }

    private Boolean runProcess2(String... command) throws IOException, InterruptedException {
        boolean result = false;
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
        //unable to connect to
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("connected to") || line.contains("already connected to")) {
                result = true;
            }
        }
        process.waitFor();

        bufferedReader.close();
        return result;
    }
}