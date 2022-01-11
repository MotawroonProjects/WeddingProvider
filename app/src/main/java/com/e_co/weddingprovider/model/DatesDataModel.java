package com.e_co.weddingprovider.model;

import java.io.Serializable;
import java.util.List;

public class DatesDataModel extends StatusResponse implements Serializable {
    private List<String> data;

    public List<String> getData() {
        return data;
    }
}
