package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXDecorator;
import com.kimjio.easyadb.tool.FontTools;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TheSuperSuperSuperHOS extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/THESUPERSUPERSUPERHOS.fxml"));
        primaryStage.setTitle(getClass().getSimpleName());
        primaryStage.getIcons().add(new Image("/drawable/ic_heroes.png"));
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        JFXDecorator jfxDecorator = new JFXDecorator(primaryStage, root, false, false, false);
        jfxDecorator.setCustomMaximize(true);
        jfxDecorator.setOnCloseButtonAction(primaryStage::close);

        ImageView hosView = (ImageView) root.lookup("#super_hos");

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), hosView);
        rotateTransition.setByAngle(360 * 40);
        rotateTransition.setCycleCount(-1);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();

        Scene scene = new Scene(jfxDecorator);

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(TheSuperSuperSuperHOS.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                TheSuperSuperSuperHOS.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                TheSuperSuperSuperHOS.class.getResource("/css/easyadb.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
