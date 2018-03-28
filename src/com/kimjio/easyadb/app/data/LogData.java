package com.kimjio.easyadb.app.data;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogData extends RecursiveTreeObject<LogData> {
    public StringProperty logDate;
    public StringProperty logTime;
    public StringProperty priority;
    public StringProperty tag;
    public StringProperty content;

    public LogData(String logDate, String logTime, String priority, String tag, String content) {
        this.logDate = new SimpleStringProperty(logDate);
        this.logTime = new SimpleStringProperty(logTime);
        this.priority = new SimpleStringProperty(priority);
        this.tag = new SimpleStringProperty(tag);
        this.content = new SimpleStringProperty(content);
    }

    public StringProperty logDateProperty() {
        return logDate;
    }

    public StringProperty logTimeProperty() {
        return logTime;
    }

    public StringProperty priorityProperty() {
        return priority;
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public StringProperty contentProperty() {
        return content;
    }
}
