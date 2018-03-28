package com.kimjio.easyadb.app.controller;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.kimjio.easyadb.app.data.DBLogData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;

import java.net.URL;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.function.Function;

public class DBLogcatController implements Initializable {

    @FXML
    private JFXTreeTableView<DBLogData> treeTableView;
    @FXML
    private JFXTreeTableColumn<DBLogData, String> logTimeColumn;
    @FXML
    private JFXTreeTableColumn<DBLogData, String> priorityColumn;
    @FXML
    private JFXTreeTableColumn<DBLogData, String> tagColumn;
    @FXML
    private JFXTreeTableColumn<DBLogData, String> contentColumn;
    @FXML
    private JFXTextField searchField;

    private ObservableList<DBLogData> logData = FXCollections.observableArrayList();

    private ListIterator<DBLogData> logListIterator = logData.listIterator();

    public void initialize(URL location, ResourceBundle resources) {
        setupLogTableView();
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<DBLogData, T> column, Function<DBLogData, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<DBLogData, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupLogTableView() {
        setupCellValueFactory(logTimeColumn, DBLogData::logTimeProperty);
        setupCellValueFactory(priorityColumn, DBLogData::priorityProperty);
        setupCellValueFactory(tagColumn, DBLogData::tagProperty);
        setupCellValueFactory(contentColumn, DBLogData::contentProperty);
        contentColumn.setStyle( "-fx-alignment: CENTER-LEFT;");

        treeTableView.setRoot(new RecursiveTreeItem<>(logData, RecursiveTreeObject::getChildren));

        treeTableView.setShowRoot(false);
        searchField.textProperty().addListener(setupSearchField(treeTableView));
    }

    private ChangeListener<String> setupSearchField(final JFXTreeTableView<DBLogData> tableView) {
        return (o, oldVal, newVal) ->
                tableView.setPredicate(logProp -> {
                    final DBLogData logData = logProp.getValue();
                    return logData.logTime.get().contains(newVal)
                            || logData.priority.get().contains(newVal)
                            || logData.tag.get().contains(newVal)
                            || logData.content.get().contains(newVal);
                });
    }

    public void addLog(String logTime, String priority, String tag, String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logListIterator.add(new DBLogData(logTime, priority, tag, content));
            }
        });
    }
}
