package com.apps.weddingprovider.model;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;


public class SignUpModel extends BaseObservable {
    private String address;
    private String name;
    private double lat;
    private double lng;


    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_name = new ObservableField<>();


    public boolean isDataValid(Context context) {
        if (!address.trim().isEmpty()
                &&
                !name.trim().isEmpty() &&lat!=0&&lng!=0
//               department_id != 0


        ) {
            error_address.set(null);
            error_name.set(null);

            return true;
        } else {

            if (address.trim().isEmpty()||lat!=0||lng!=0) {
                error_address.set(context.getString(R.string.field_required));

            } else {
                error_address.set(null);

            }
            if (name.trim().isEmpty()) {
                error_name.set(context.getString(R.string.field_required));

            } else {
                error_name.set(null);

            }


            return false;
        }
    }

    public SignUpModel() {

        setAddress("");

        setName("");

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

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }


}