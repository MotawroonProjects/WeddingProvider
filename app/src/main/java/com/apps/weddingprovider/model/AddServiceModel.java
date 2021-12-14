package com.apps.weddingprovider.model;

import android.content.Context;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;

import java.util.ArrayList;
import java.util.List;


public class AddServiceModel extends BaseObservable {
    private String name;
    public ObservableField<String> error_name = new ObservableField<>();



    public boolean isDataValid(Context context) {

        if (!name.isEmpty()


        ) {


            error_name.set(null);



            return true;

        } else {

            if (name.isEmpty()){
                error_name.set(context.getString(R.string.field_required));
            }else {
                error_name.set(null);

            }




            return false;

        }

    }

    public AddServiceModel() {
        name = "";

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
