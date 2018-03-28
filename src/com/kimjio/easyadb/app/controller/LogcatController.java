package com.kimjio.easyadb.app.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.kimjio.easyadb.app.data.LogData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.function.Function;

public class LogcatController implements Initializable {

    @FXML
    private JFXTreeTableView<LogData> treeTableView;
    @FXML
    private JFXTreeTableColumn<LogData, String> logDateColumn;
    @FXML
    private JFXTreeTableColumn<LogData, String> logTimeColumn;
    @FXML
    private JFXTreeTableColumn<LogData, String> priorityColumn;
    @FXML
    private JFXTreeTableColumn<LogData, String> tagColumn;
    @FXML
    private JFXTreeTableColumn<LogData, String> contentColumn;
    @FXML
    private JFXTextField searchField;

    private ObservableList<LogData> logData = FXCollections.observableArrayList();

    private ListIterator<LogData> logListIterator = logData.listIterator();

    public LogcatController() {
    }

    public void initialize(URL location, ResourceBundle resources) {
        setupLogTableView();
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<LogData, T> column, Function<LogData, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<LogData, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupLogTableView() {
        setupCellValueFactory(logDateColumn, LogData::logDateProperty);
        setupCellValueFactory(logTimeColumn, LogData::logTimeProperty);
        setupCellValueFactory(priorityColumn, LogData::priorityProperty);
        setupCellValueFactory(tagColumn, LogData::tagProperty);
        setupCellValueFactory(contentColumn, LogData::contentProperty);
        contentColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        treeTableView.setRoot(new RecursiveTreeItem<>(logData, RecursiveTreeObject::getChildren));

        treeTableView.setShowRoot(false);
        searchField.textProperty().addListener(setupSearchField(treeTableView));
    }

    private ChangeListener<String> setupSearchField(final JFXTreeTableView<LogData> tableView) {
        return (o, oldVal, newVal) ->
                tableView.setPredicate(logProp -> {
                    final LogData logData = logProp.getValue();
                    return logData.logDate.get().contains(newVal)
                            || logData.logTime.get().contains(newVal)
                            || logData.priority.get().contains(newVal)
                            || logData.tag.get().contains(newVal)
                            || logData.content.get().contains(newVal);
                });
    }

    public void addLog(String logDate, String logTime, String priority, String tag, String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logListIterator.add(new LogData(logDate, logTime, priority, tag, content));
            }
        });
    }
}
