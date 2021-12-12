package com.apps.weddingprovider.model;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String phone_code;
    private String phone;
    public ObservableField<String> error_phone = new ObservableField<>();

    public LoginModel() {
        phone_code ="+20";
        phone ="";
    }

    public boolean isDataValid(Context context){
        if (!phone.isEmpty()){
            error_phone.set(null);


            return true;
        }else {

            if (phone.isEmpty()){
                error_phone.set(context.getString(R.string.field_required));

            }else {
                error_phone.set(null);

            }


            return false;
        }
    }
    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);
    }
    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }


}