package com.apps.weddingprovider.mvvm;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.AddAdditionalRowBinding;
import com.apps.weddingprovider.model.AddOfferModel;
import com.apps.weddingprovider.model.AddServiceModel;
import com.apps.weddingprovider.model.DepartmentDataModel;
import com.apps.weddingprovider.model.DepartmentModel;
import com.apps.weddingprovider.model.GalleryModel;
import com.apps.weddingprovider.model.LocationModel;
import com.apps.weddingprovider.model.PlaceGeocodeData;
import com.apps.weddingprovider.model.PlaceMapDetailsData;
import com.apps.weddingprovider.model.SingleServiceDataModel;
import com.apps.weddingprovider.model.StatusResponse;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.remote.Api;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.tags.Tags;
import com.apps.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ActivityAddOfferMvvm extends AndroidViewModel {
    private Context context;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String lang = "ar";
    private MutableLiveData<Boolean> onDataSuccess;

    public ActivityAddOfferMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public LiveData<Boolean> onDataSuccess() {
        if (onDataSuccess == null) {
            onDataSuccess = new MutableLiveData<>();

        }
        return onDataSuccess;
    }


    //_________________________hitting api_________________________________


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }

    public void addOffer(Context context, AddOfferModel model, UserModel userModel,String service_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody api_part = Common.getRequestBodyText(Tags.api_key);
        RequestBody user_part = Common.getRequestBodyText(userModel.getData().getId() + "");
        RequestBody service_part = Common.getRequestBodyText(service_id);

        RequestBody name_part = Common.getRequestBodyText(model.getName());
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody text_part = Common.getRequestBodyText(model.getDescription());

        MultipartBody.Part main_image = Common.getMultiPart(context, Uri.parse(model.getMainImage()), "image");

        Api.getService(Tags.base_url).addOffer("Bearer " + userModel.getData().getToken(), api_part, user_part,service_part, name_part, price_part, text_part, main_image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<StatusResponse>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Response<StatusResponse> response) {
                dialog.dismiss();

                if (response.isSuccessful()) {
                    Log.e("code", response.body().getStatus() + "__");

                    if (response.body().getStatus() == 200) {
                        onDataSuccess.setValue(true);
                    }
                } else {

                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.e("onError", throwable.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onComplete() {
                dialog.dismiss();
            }
        });
    }





}
