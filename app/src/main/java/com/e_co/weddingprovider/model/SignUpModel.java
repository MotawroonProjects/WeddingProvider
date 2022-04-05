package com.e_co.weddingprovider.model;

import android.content.Context;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.e_co.weddingprovider.BR;
import com.e_co.weddingprovider.R;


public class SignUpModel extends BaseObservable {
    private String address;
    private String name;
    private double lat;
    private double lng;
    private String facebook;
    private String instagram;
    private String twitter;


    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_facebook = new ObservableField<>();
    public ObservableField<String> error_instagram = new ObservableField<>();
    public ObservableField<String> error_twitter = new ObservableField<>();


    public boolean isDataValid(Context context) {
        if (!address.trim().isEmpty() &&
                !name.trim().isEmpty()
                && lat != 0 && lng != 0
                ||((!facebook.isEmpty()&&Patterns.WEB_URL.matcher(facebook).matches())||(!instagram.isEmpty()&&Patterns.WEB_URL.matcher(instagram).matches())||(!twitter.isEmpty()&&Patterns.WEB_URL.matcher(twitter).matches()))

//               department_id != 0


        ) {




            error_address.set(null);
            error_name.set(null);

            return true;
        } else {

            if (address.trim().isEmpty() || lat != 0 || lng != 0) {
                error_address.set(context.getString(R.string.field_required));

            } else {
                error_address.set(null);

            }
            if (name.trim().isEmpty()) {
                error_name.set(context.getString(R.string.field_required));

            } else {
                error_name.set(null);

            }
            if (!facebook.isEmpty()) {
                if (!Patterns.WEB_URL.matcher(facebook.trim()).matches()) {
                    error_facebook.set(context.getString(R.string.inv_url));
                } else {
                    error_facebook.set(null);

                }
            }

            if (!instagram.isEmpty()) {
                if (!Patterns.WEB_URL.matcher(instagram.trim()).matches()) {
                    error_instagram.set(context.getString(R.string.inv_url));
                } else {
                    error_instagram.set(null);

                }
            }

            if (!twitter.isEmpty()) {
                if (!Patterns.WEB_URL.matcher(twitter.trim()).matches()) {
                    error_twitter.set(context.getString(R.string.inv_url));
                } else {
                    error_twitter.set(null);

                }
            }


            return false;
        }
    }

    public SignUpModel() {

        setAddress("");
        setName("");
        setFacebook("");
        setInstagram("");
        setTwitter("");

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

    @Bindable
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
        notifyPropertyChanged(BR.facebook);
    }


    @Bindable
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
        notifyPropertyChanged(BR.instagram);
    }

    @Bindable
    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
        notifyPropertyChanged(BR.twitter);
    }
}