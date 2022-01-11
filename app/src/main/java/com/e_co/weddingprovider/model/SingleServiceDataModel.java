package com.e_co.weddingprovider.model;

import java.io.Serializable;

public class SingleServiceDataModel extends StatusResponse implements Serializable {
    private ServiceModel data;
    public ServiceModel getData() {
        return data;
    }

}
