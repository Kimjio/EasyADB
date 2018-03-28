package com.kimjio.easyadb.app;

import com.jfoenix.controls.JFXDecorator;
import com.kimjio.easyadb.app.controller.dialog.HOSDialog;
import com.kimjio.easyadb.tool.FontTools;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class 한글 extends Application {

    private Stage 초기단계;

    public static void main(String[] 변수들) {
        launch(변수들);
    }

    @Override
    public void start(Stage 초기단계) throws IOException {
        Parent 루트 = FXMLLoader.load(getClass().getResource("/layout/KR.fxml"));
        this.초기단계 = 초기단계;
        this.초기단계.setTitle("한글");
        this.초기단계.getIcons().add(new Image("/drawable/ic_info_black.png"));
        FontTools.loadFont(getClass(), 0, FontTools.notoFonts);
        JFXDecorator 장식 = new JFXDecorator(this.초기단계, 루트, false, false, false);
        장식.setCustomMaximize(true);
        장식.setOnCloseButtonAction(this.초기단계::close);

        Scene 씬 = new Scene(장식);
        Label 이름 = (Label) 루트.lookup("#app_name");
        Label 한글 = (Label) 루트.lookup("#kor");
        Label 영어 = (Label) 루트.lookup("#eng");
        Label 만든이 = (Label) 루트.lookup("#by");

        루트.setVisible(false);
        final ObservableList<String> 스타일시트 = 씬.getStylesheets();
        스타일시트.addAll(한글.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                한글.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                한글.class.getResource("/css/easyadb.css").toExternalForm());
        this.초기단계.setScene(씬);
        this.초기단계.show();

        HOSDialog 히오스대화상자 = new HOSDialog(1, true);
        히오스대화상자.setHeaderText(null);

        Optional<ButtonType> 결과 = 히오스대화상자.showAndWait();

        if (결과.orElse(null) == ButtonType.OK) {
            HOSDialog 히오스두번째대화상자 = new HOSDialog(10, true);
            히오스두번째대화상자.setHeaderText(null);

            Optional<ButtonType> 두번째결과 = 히오스두번째대화상자.showAndWait();

            if (두번째결과.orElse(null) == ButtonType.OK) {
                HOSDialog 히오스세번째대화상자 = new HOSDialog(20, true);
                히오스세번째대화상자.setHeaderText(null);

                Optional<ButtonType> 세번째결과 = 히오스세번째대화상자.showAndWait();

                if (세번째결과.orElse(null) == ButtonType.OK) {
                    HOSDialog 히오스네번째대화상자 = new HOSDialog(30, true);
                    히오스네번째대화상자.setHeaderText(null);

                    Optional<ButtonType> 네번째결과 = 히오스네번째대화상자.showAndWait();

                    if (네번째결과.orElse(null) == ButtonType.OK) {
                        HOSDialog 히오스다섯번째대화상자 = new HOSDialog(40, true);
                        히오스다섯번째대화상자.setHeaderText(null);

                        Optional<ButtonType> 다섯번째결과 = 히오스다섯번째대화상자.showAndWait();

                        if (다섯번째결과.orElse(null) == ButtonType.OK) {
                            HOSDialog 히오스여섯번째대화상자 = new HOSDialog(40, true);
                            히오스여섯번째대화상자.setContentText("히오스 대화상자\nPress OK");
                            히오스여섯번째대화상자.setHeaderText(null);

                            Optional<ButtonType> 여섯번째결과 = 히오스여섯번째대화상자.showAndWait();

                            if (여섯번째결과.orElse(null) == ButtonType.OK) {
                                new TheSuperSuperSuperHOS().start(new Stage());
                                창닫아();
                            } else {
                                루트.setVisible(true);
                            }
                        } else {
                            루트.setVisible(true);
                            이름.setText("Heroes");
                            한글.setText("스");
                            영어.setText("HOs");
                            만든이.setText("Battle.net");
                        }
                    } else {
                        루트.setVisible(true);
                        이름.setText("Heroes");
                        한글.setText("오");
                        영어.setText("HoS");
                        만든이.setText("Battle.net");
                    }
                } else {
                    루트.setVisible(true);
                    이름.setText("Heroes");
                    한글.setText("히");
                    영어.setText("hOS");
                    만든이.setText("Battle.net");
                }
            } else {
                루트.setVisible(true);
                이름.setText("히오스");
                한글.setText("~히오스~");
                영어.setText("~HOS~");
                만든이.setText("Blizzard");
            }
        } else {
            루트.setVisible(true);
            이름.setText("히어로즈 오브 더 스톰");
            영어.setText("히어로즈 오브 더 스☆톰♚♚가입시$$전원 카드팩☜☜뒷면100%증정※ ♜월드오브 워크래프트♜펫 무료증정￥");
            한글.setText("특정조건 §§디아블로3§§☆공허의유산☆초상화♜오버워치♜겐지스킨￥획득기회@@@");
            만든이.setText("즉시이동http://kr.battle.net/heroes/ko/");
        }
    }

    public void 창닫아() {
        this.초기단계.close();
    }
}
