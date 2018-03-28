package com.kimjio.easyadb.app;

import com.jfoenix.controls.*;
import com.kimjio.easyadb.app.controller.LogcatController;
import com.kimjio.easyadb.app.controller.dialog.Alert;
import com.kimjio.easyadb.tool.Command;
import com.kimjio.easyadb.tool.Tools;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logcat extends Application implements Command<Void> {

    private String deviceID;
    private JFXPopup menu;
    private Connection connection;
    private final String userID;
    private Thread thread;

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

    private final LogcatController logcatController = new LogcatController();

    public static void main(String[] args) {
        launch(args);
    }

    public Logcat(String deviceID, String userID) {
        this.deviceID = deviceID;
        this.userID = userID;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/Logcat.fxml"));
        fxmlLoader.setController(logcatController);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Logcat");
        primaryStage.getIcons().add(new Image("/drawable/ic_logcat_title.png"));
        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, true, true);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                primaryStage.close();
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //FIXME
                thread.stop();
            }
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/log_menu.fxml"));
        JFXListView jfxListView = (JFXListView) ((Parent) loader.load()).lookup("#menuList");
        jfxListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (jfxListView.getSelectionModel().getSelectedIndex() == 0) {
                    try {
                        new DBLogcat(userID).start(new Stage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        menu = new JFXPopup(loader.getRoot());
        JFXHamburger jfxHamburger = (JFXHamburger) root.lookup("#menu");
        jfxHamburger.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                menu.show(jfxHamburger,
                        JFXPopup.PopupVPosition.TOP,
                        JFXPopup.PopupHPosition.RIGHT,
                        -12,
                        15);
            }
        });

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/android_log", "client", "0000");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(decorator);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(Main.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/easyadb.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runProcess("adb", "-s", deviceID, "logcat");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public Void runProcess(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals("") || line.equals("--------- beginning of crash") || line.equals("--------- beginning of main") || line.equals("--------- beginning of system"))
                continue;
            try {
                final String tmp = line.substring(31);
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from log");
                ResultSet resultSet = preparedStatement.executeQuery();

                boolean isAlreadyExited = false;

                while (resultSet.next()) {
                    String result = resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5) + " " + resultSet.getString(6);
                    String log = line.substring(0, 5) + " " + line.substring(5, 19) + tmp.substring(0, 1) + " " + tmp.substring(2, tmp.indexOf(":")) + " " + tmp.substring(tmp.indexOf(":") + 1);

                    if (result.equals(log)) {
                        if (resultSet.getString(1).equals(userID)) {
                            isAlreadyExited = true;
                            break;
                        }
                    }
                }
                if (!isAlreadyExited) {
                    connection.prepareStatement("insert into Log values(" + "'" + userID + "'" + "," + "'" + line.substring(0, 5) + "'" + "," + "'" + line.substring(5, 19) + "'" + "," + "'" + tmp.substring(0, 1) + "'" + "," + "'" + tmp.substring(2, tmp.indexOf(":")) + "'" + "," + "'" + tools.replace(tmp.substring(tmp.indexOf(":") + 1), new String[]{"'"}, "\\'") + "'" + ")").execute();
                }
                logcatController.addLog(line.substring(0, 5), line.substring(5, 19), tmp.substring(0, 1), tmp.substring(2, tmp.indexOf(":")), tmp.substring(tmp.indexOf(":") + 1));
            } catch (SQLException | StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        process.waitFor();

        bufferedReader.close();
        return null;
    }

}
