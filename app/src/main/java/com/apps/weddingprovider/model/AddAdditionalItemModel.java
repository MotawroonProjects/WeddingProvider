package com.apps.weddingprovider.model;

import android.content.Context;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;


public class AddAdditionalItemModel extends BaseObservable {
    private String name;
    private String amount;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_amount = new ObservableField<>();

    public boolean isMainDataValid(Context context) {

        if (!name.isEmpty()


        ) {


            error_name.set(null);

            return true;

        } else {

            error_name.set(context.getString(R.string.field_required));

            return false;

        }

    }
    public boolean isExtraDataValid(Context context) {

        if (!name.isEmpty() &&
                !amount.isEmpty()


        ) {


            error_name.set(null);
            error_amount.set(null);

            return true;

        } else {

            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_required));
            } else {
                error_name.set(null);

            }


            if (amount.isEmpty()) {
                error_amount.set(context.getString(R.string.field_required));
            } else {
                error_amount.set(null);

            }


            return false;

        }

    }

    public AddAdditionalItemModel() {
        name = "";
        amount = "";
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
