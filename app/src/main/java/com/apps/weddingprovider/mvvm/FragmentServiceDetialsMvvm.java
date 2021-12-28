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
import com.apps.weddingprovider.model.SingleServiceDataModel;
import com.apps.weddingprovider.model.StatusResponse;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.remote.Api;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.tags.Tags;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FragmentServiceDetialsMvvm extends AndroidViewModel {
    private String TAG = "FragmentServiceDetialsMvvm";
    private Context context;

    private Timer timer;
    private TimerTask timerTask;
    private MutableLiveData<SingleServiceDataModel> singleWeddingHallDataModelMutableLiveData;
    private MutableLiveData<Boolean> isLoadingLivData;
    private MutableLiveData<Integer> onDeleteSuccess;

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoadingLivData == null) {
            isLoadingLivData = new MutableLiveData<>();
        }
        return isLoadingLivData;
    }

    public MutableLiveData<SingleServiceDataModel> getSingleService() {
        if (singleWeddingHallDataModelMutableLiveData == null) {
            singleWeddingHallDataModelMutableLiveData = new MutableLiveData<>();
        }
        return singleWeddingHallDataModelMutableLiveData;
    }

    private CompositeDisposable disposable = new CompositeDisposable();

    public FragmentServiceDetialsMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public MutableLiveData<Integer> onDeleteSuccess() {
        if (onDeleteSuccess == null) {
            onDeleteSuccess = new MutableLiveData<>();
        }
        return onDeleteSuccess;
    }


    public void getSingleServiceData(String id) {
        isLoadingLivData.postValue(true);
        Api.getService(Tags.base_url)
                .getSingleService(Tags.api_key, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<SingleServiceDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<SingleServiceDataModel> response) {
                        isLoadingLivData.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                singleWeddingHallDataModelMutableLiveData.postValue(response.body());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isLoadingLivData.postValue(false);
                        Log.e(TAG, "onError: ", e);
                    }
                });

    }


    public void deleteOfferData(UserModel userModel, String offer_id, int adapterPos, Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteOffer("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "", offer_id)
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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
