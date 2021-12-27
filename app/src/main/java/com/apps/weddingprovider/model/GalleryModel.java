package com.apps.weddingprovider.model;

import java.io.Serializable;

public class GalleryModel implements Serializable {
    private String id;
    private String image;
    private String type;//local or online

    public GalleryModel(String id, String image, String type) {
        this.id = id;
        this.image = image;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
