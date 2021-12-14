package com.apps.weddingprovider.model;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private double lat;
    private double lng;
    private boolean search;

    public LocationModel(double lat, double lng, boolean search) {
        this.lat = lat;
        this.lng = lng;
        this.search = search;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }
}
