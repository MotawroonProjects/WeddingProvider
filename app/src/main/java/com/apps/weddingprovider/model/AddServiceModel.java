package com.apps.weddingprovider.model;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.apps.weddingprovider.BR;
import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.AddAdditionalRowBinding;

import java.util.ArrayList;
import java.util.List;


public class AddServiceModel extends BaseObservable {
    private String mainImage;
    private String videoUri;
    private String name;
    private String price;
    private String department_id;
    private String address;
    private double lat;
    private double lng;
    private String maxNumber;
    private String description;
    private List<GalleryModel> galleryImages;
    private List<AddAdditionalRowBinding> mainItemList;
    private List<AddAdditionalRowBinding> extraItemList;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_price = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_maxNumber = new ObservableField<>();
    public ObservableField<String> error_description = new ObservableField<>();


    public boolean isDataValid(Context context) {

        if (!mainImage.isEmpty() &&
                !videoUri.isEmpty() &&
                !name.isEmpty() &&
                !price.isEmpty() &&
                !department_id.isEmpty() &&
                !address.isEmpty() &&
                !maxNumber.isEmpty() &&
                !description.isEmpty() &&
                galleryImages.size() > 0 &&
                mainItemList.size() > 0 &&
                isMainValid(context)
        ) {


            error_name.set(null);
            error_price.set(null);
            error_address.set(null);
            error_maxNumber.set(null);
            error_description.set(null);

            if (extraItemList.size() > 0) {
                if (isExtraValid(context)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;

            }


        } else {
            if (mainImage.isEmpty()) {
                Toast.makeText(context, R.string.ch_main_image, Toast.LENGTH_SHORT).show();
            }

            if (videoUri.isEmpty()) {
                Toast.makeText(context, R.string.vid_req, Toast.LENGTH_SHORT).show();
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

            if (address.isEmpty()) {
                error_address.set(context.getString(R.string.field_required));
            } else {
                error_address.set(null);

            }

            if (maxNumber.isEmpty()) {
                error_maxNumber.set(context.getString(R.string.field_required));
            } else {
                error_maxNumber.set(null);

            }

            if (description.isEmpty()) {
                error_description.set(context.getString(R.string.field_required));
            } else {
                error_description.set(null);

            }

            if (mainItemList.size() > 0) {
                isMainValid(context);
            } else {
                Toast.makeText(context, R.string.sel_main_items, Toast.LENGTH_SHORT).show();

            }

            if (extraItemList.size() > 0) {
                isExtraValid(context);
            }
            if (department_id.isEmpty()) {
                Toast.makeText(context, context.getResources().getString(R.string.ch_depart), Toast.LENGTH_SHORT).show();
            }
            if (galleryImages.size() == 0) {
                Toast.makeText(context, context.getResources().getString(R.string.upload_service_images), Toast.LENGTH_LONG).show();
            }
            return false;

        }

    }

    public AddServiceModel() {
        mainImage = "";
        videoUri = "";
        name = "";
        price = "";
        department_id = "";
        address = "";
        lat = 0;
        lng = 0;
        maxNumber = "";
        description = "";
        galleryImages = new ArrayList<>();
        mainItemList = new ArrayList<>();
        extraItemList = new ArrayList<>();


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

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.name);

    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.name);

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
    public String getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(String maxNumber) {
        this.maxNumber = maxNumber;
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

    public List<GalleryModel> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<GalleryModel> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public List<AddAdditionalRowBinding> getMainItemList() {
        return mainItemList;
    }

    public void setMainItemList(List<AddAdditionalRowBinding> mainItemList) {
        this.mainItemList = mainItemList;
    }

    public List<AddAdditionalRowBinding> getExtraItemList() {
        return extraItemList;
    }

    public void setExtraItemList(List<AddAdditionalRowBinding> extraItemList) {
        this.extraItemList = extraItemList;
    }

    private boolean isMainValid(Context context) {
        boolean isDataValid = true;
        for (AddAdditionalRowBinding binding : mainItemList) {
            if (!binding.getModel().isDataValid(context)) {
                isDataValid = false;
            }
        }
        return isDataValid;
    }

    private boolean isExtraValid(Context context) {
        boolean isDataValid = true;
        for (AddAdditionalRowBinding binding : extraItemList) {
            if (!binding.getModel().isDataValid(context)) {
                isDataValid = false;
            }
        }
        return isDataValid;
    }
}
