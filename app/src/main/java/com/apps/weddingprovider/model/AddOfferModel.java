package com.apps.weddingprovider.model;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.AddAdditionalRowBinding;

import java.util.ArrayList;
import java.util.List;


public class AddOfferModel extends BaseObservable {
    private String mainImage;
    private String name;
    private String price;
    private String description;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_price = new ObservableField<>();
    public ObservableField<String> error_description = new ObservableField<>();


    public boolean isDataValid(Context context) {

        if (!mainImage.isEmpty() &&
                !name.isEmpty() &&
                !price.isEmpty() &&
                !description.isEmpty()
        ) {


            error_name.set(null);
            error_price.set(null);
            error_description.set(null);
            return true;

        } else {
            if (mainImage.isEmpty()) {
                Toast.makeText(context, R.string.ch_main_image, Toast.LENGTH_SHORT).show();
            }


            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_required));
            } else {
                error_name.set(null);

            }

            if (price.isEmpty()) {
                error_price.set(context.getString(R.string.field_required));
            } else {
                error_price.set(null);

            }


            if (description.isEmpty()) {
                error_description.set(context.getString(R.string.field_required));
            } else {
                error_description.set(null);

            }


            return false;

        }

    }

    public AddOfferModel() {
        mainImage = "";
        name = "";
        price = "";
        description = "";


    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }



    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.name);

    }



    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.name);

    }




}
