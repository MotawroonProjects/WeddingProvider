package com.e_co.weddingprovider.model;

import java.io.Serializable;

public class UserSettingsModel implements Serializable {
    private boolean isLanguageSelected= false;

    public boolean isLanguageSelected() {
        return isLanguageSelected;
    }

    public void setLanguageSelected(boolean languageSelected) {
        isLanguageSelected = languageSelected;
    }


}
