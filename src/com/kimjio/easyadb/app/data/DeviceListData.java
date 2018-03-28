package com.kimjio.easyadb.app.data;

public class DeviceListData {
    private String deviceID;
    private String deviceModel;
    private String deviceStatus;
    private String ImageUrl;
    private boolean isEmpty;
    private boolean isOffline;

    public DeviceListData (String deviceID, String deviceModel, String deviceStatus, String imageUrl, boolean isEmpty, boolean isOffline) {
        this.deviceID = deviceID;
        this.deviceModel = deviceModel;
        this.deviceStatus = deviceStatus;
        this.ImageUrl = imageUrl;
        this.isEmpty = isEmpty;
        this.isOffline = isOffline;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isOffline() {
        return isOffline;
    }
}
