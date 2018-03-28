package com.kimjio.easyadb.app.data;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DBLogData extends RecursiveTreeObject<DBLogData> {
    public StringProperty logTime;
    public StringProperty priority;
    public StringProperty tag;
    public StringProperty content;

    public DBLogData(String logTime, String priority, String tag, String content) {
        this.logTime = new SimpleStringProperty(logTime);
        this.priority = new SimpleStringProperty(priority);
        this.tag = new SimpleStringProperty(tag);
        this.content = new SimpleStringProperty(content);
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
