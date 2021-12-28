package com.apps.weddingprovider.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.model.DatesDataModel;
import com.apps.weddingprovider.model.ServiceDataModel;
import com.apps.weddingprovider.model.ServiceModel;
import com.apps.weddingprovider.model.StatusResponse;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.remote.Api;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.tags.Tags;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FragmentServiceMvvm extends AndroidViewModel {
    private static final String TAG = "FragmentServMvvm";

    private Context context;
    private MutableLiveData<Boolean> isLoadingLivData;
    private MutableLiveData<List<ServiceModel>> serviceLiveData;
    private MutableLiveData<Integer> onDeleteSuccess;
    private CompositeDisposable disposable = new CompositeDisposable();

    public FragmentServiceMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoadingLivData == null) {
            isLoadingLivData = new MutableLiveData<>();
        }
        return isLoadingLivData;
    }

    public MutableLiveData<List<ServiceModel>> getServiceLiveData() {
        if (serviceLiveData == null) {
            serviceLiveData = new MutableLiveData<>();
        }
        return serviceLiveData;
    }

    public MutableLiveData<Integer> onDeleteSuccess() {
        if (onDeleteSuccess == null) {
            onDeleteSuccess = new MutableLiveData<>();
        }
        return onDeleteSuccess;
    }


    //_________________________hitting api_________________________________


    public void getServiceData(UserModel userModel) {

        isLoadingLivData.setValue(true);
        Api.getService(Tags.base_url)
                .getService("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<ServiceDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<ServiceDataModel> response) {
                        isLoadingLivData.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                List<ServiceModel> list = response.body().getData();
                                serviceLiveData.setValue(list);
                            }
                        }
                    }

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoadingLivData.setValue(false);
                        Log.e(TAG, "onError: ", e);
                    }
                });

    }

    public void deleteServiceData(UserModel userModel, String service_id, int adapterPos,Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteService("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "", service_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                onDeleteSuccess.setValue(adapterPos);
                            }
                        }
                    }

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onError(@NonNull Throwable e) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: ", e);
                    }
                });

    }

    //_____________________________________________________________________

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
