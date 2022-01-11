package com.e_co.weddingprovider.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.AddAdditionalRowBinding;
import com.e_co.weddingprovider.model.AddOfferModel;
import com.e_co.weddingprovider.model.StatusResponse;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.remote.Api;
import com.e_co.weddingprovider.share.Common;
import com.e_co.weddingprovider.tags.Tags;

import io.reactivex.Observer;
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

    public void addOffer(Context context, AddOfferModel model, UserModel userModel, String service_id) {
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

        Api.getService(Tags.base_url).addOffer("Bearer " + userModel.getData().getToken(), api_part, user_part, service_part, name_part, price_part, text_part, main_image)
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

    public void updateOffer(Context context, AddOfferModel model, UserModel userModel, String offer_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody api_part = Common.getRequestBodyText(Tags.api_key);
        RequestBody user_part = Common.getRequestBodyText(userModel.getData().getId() + "");
        RequestBody offer_id_part = Common.getRequestBodyText(offer_id);

        RequestBody name_part = Common.getRequestBodyText(model.getName());
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody text_part = Common.getRequestBodyText(model.getDescription());
        MultipartBody.Part main_image = null;
        if (!model.getMainImage().startsWith("http")) {
            main_image = Common.getMultiPart(context, Uri.parse(model.getMainImage()), "image");

        }

        Api.getService(Tags.base_url).updateOffer("Bearer " + userModel.getData().getToken(), api_part, user_part, offer_id_part, name_part, price_part, text_part, main_image)
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
