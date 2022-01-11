package com.e_co.weddingprovider.model;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel extends StatusResponse implements Serializable {
    private List<NotificationModel> data;

    public List<NotificationModel> getData() {
        return data;
    }
}
