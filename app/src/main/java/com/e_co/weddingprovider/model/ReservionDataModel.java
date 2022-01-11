package com.e_co.weddingprovider.model;

import java.io.Serializable;
import java.util.List;

public class ReservionDataModel extends StatusResponse implements Serializable {
    private List<ResevisionModel> data;

    public List<ResevisionModel> getData() {
        return data;
    }

    public void setData(List<ResevisionModel> data) {
        this.data = data;
    }
}
