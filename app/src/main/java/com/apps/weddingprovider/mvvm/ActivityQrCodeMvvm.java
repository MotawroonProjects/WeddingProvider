package com.apps.weddingprovider.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.model.AddOfferModel;
import com.apps.weddingprovider.model.StatusResponse;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.remote.Api;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.tags.Tags;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ActivityQrCodeMvvm extends AndroidViewModel {
    private Context context;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<Boolean> onDataSuccess;

    public ActivityQrCodeMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    public LiveData<Boolean> onDataSuccess() {
        if (onDataSuccess == null) {
            onDataSuccess = new MutableLiveData<>();

        }
        return onDataSuccess;
    }


    //_________________________hitting api_________________________________


    public void confirmReservation(Context context,  UserModel userModel, String qr_code) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url).confirmReservation("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "", qr_code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<StatusResponse> response) {
                        if (response.isSuccessful()){
                            if (response.body()!=null&&response.body().getStatus()==200){
                                onDataSuccess.setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("confirmeError",e.getMessage());
                    }
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }

}
