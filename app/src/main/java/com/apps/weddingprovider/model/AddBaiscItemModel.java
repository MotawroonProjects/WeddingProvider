package com.apps.weddingprovider.model;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;


public class AddBaiscItemModel extends BaseObservable {
    private String name;
    private String detials;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_detials = new ObservableField<>();


    public boolean isDataValid(Context context) {

        if (!name.isEmpty() &&
                !detials.isEmpty()


        ) {


            error_name.set(null);
            error_detials.set(null);

            return true;

        } else {

            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_required));
            } else {
                error_name.set(null);

            }


            if (detials.isEmpty()) {
                error_detials.set(context.getString(R.string.field_required));
            } else {
                error_detials.set(null);

            }


            return false;

        }

    }

    public AddBaiscItemModel() {
        name = "";
        detials = "";
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }


    public String getDetials() {
        return detials;
    }

    public void setDetials(String detials) {
        this.detials = detials;
    }




}
