package com.kimjio.easyadb.app;

import com.jfoenix.controls.*;
import com.kimjio.easyadb.app.controller.DBLogcatController;
import com.kimjio.easyadb.app.controller.LogcatController;
import com.kimjio.easyadb.tool.DBLog;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLogcat extends Application {

    private Connection connection; //연결 유지 객체

    private JFXPopup menu;

    private final String userID;
    private String oldDate = "\0";

    private int index = 0;
    private boolean isFirst = true;

    private List<DBLogcatController> dbLogcatControllerList = new ArrayList<>();

    public DBLogcat(String userID) {
        this.userID = userID;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/DBLogcat.fxml"));
        primaryStage.setTitle("DBLogcat");
        primaryStage.getIcons().add(new Image("/drawable/ic_logcat_title.png"));
        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, true, true);
        decorator.setCustomMaximize(true);
        decorator.setOnCloseButtonAction(new Runnable() {
            @Override
            public void run() {
                primaryStage.close();
            }
        });
        JFXTabPane jfxTabPane = (JFXTabPane) root.lookup("#tabLayout");

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/db_log_menu.fxml"));
        JFXListView jfxListView = (JFXListView) ((Parent) loader.load()).lookup("#menuList");
        jfxListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (jfxListView.getSelectionModel().getSelectedIndex() == 0) {
                    try {
                        //FIXME!!
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/android_log", "client", "0000");
                        PreparedStatement preparedStatement = connection.prepareStatement("delete from Log where userid like "+"'"+userID+"'");
                        preparedStatement.execute();
                        jfxTabPane.getTabs().remove(0, jfxTabPane.getTabs().size());
                        addEmptyTab(jfxHamburger, jfxTabPane);
                        connection.close();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                } else if (jfxListView.getSelectionModel().getSelectedIndex() == 1) {
                    try {
                        //FIXME!!
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/android_log", "client", "0000");
                        PreparedStatement preparedStatement = connection.prepareStatement("delete from Log where userid like '"+userID+"' and logdate like '"+jfxTabPane.getSelectionModel().getSelectedItem().getText()+"'");
                        System.out.println("delete from Log where userid like '"+userID+"' and logdate like '"+jfxTabPane.getSelectionModel().getSelectedItem().getText()+"'");
                        preparedStatement.execute();
                        jfxTabPane.getTabs().remove(jfxTabPane.getSelectionModel().getSelectedItem());
                        if (jfxTabPane.getTabs().isEmpty()) {
                            addEmptyTab(jfxHamburger, jfxTabPane);
                        }
                        connection.close();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        menu = new JFXPopup(loader.getRoot());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/android_log", "client", "0000");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from log");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(userID)) {
                    if (!resultSet.getString(2).equals(oldDate)) {
                        if (!isFirst) index++;
                        dbLogcatControllerList.add(new DBLogcatController());
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/frame_DBLogcat.fxml"));
                        fxmlLoader.setController(dbLogcatControllerList.get(index));
                        Tab tab = fxmlLoader.load();
                        tab.setText(resultSet.getString(2));
                        jfxTabPane.getTabs().add(tab);
                        dbLogcatControllerList.get(index).addLog(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
                        isFirst = false;
                    } else {
                        if (!dbLogcatControllerList.isEmpty())
                            dbLogcatControllerList.get(index).addLog(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
                    }
                }
                oldDate = resultSet.getString(2);
            }
            if (jfxTabPane.getTabs().isEmpty()) {
                addEmptyTab(jfxHamburger, jfxTabPane);
            }
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(decorator);

        primaryStage.setScene(scene);
        primaryStage.show();

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(Login.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                Login.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Login.class.getResource("/css/easyadb.css").toExternalForm());
    }
    public void addEmptyTab(JFXHamburger jfxHamburger, JFXTabPane jfxTabPane) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/frame_DBLogcat.fxml"));
        Tab tab = fxmlLoader.load();
        tab.setText("없음");
        jfxHamburger.setDisable(true);
        jfxTabPane.getTabs().add(tab);
    }
}
