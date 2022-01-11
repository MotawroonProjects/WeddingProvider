package com.e_co.weddingprovider.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.model.ReservionDataModel;
import com.e_co.weddingprovider.model.ResevisionModel;
import com.e_co.weddingprovider.model.StatusResponse;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.remote.Api;
import com.e_co.weddingprovider.share.Common;
import com.e_co.weddingprovider.tags.Tags;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FragmentCurrentReservisonMvvm extends AndroidViewModel {
    private static final String TAG = "FragmentCurrReservMvvm";

    private Context context;
    private MutableLiveData<List<ResevisionModel>> listMutableLiveData;


    private MutableLiveData<Boolean> isLoadingLivData;

    private CompositeDisposable disposable = new CompositeDisposable();

    public FragmentCurrentReservisonMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public LiveData<List<ResevisionModel>> getReservionList() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }


    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoadingLivData == null) {
            isLoadingLivData = new MutableLiveData<>();
        }
        return isLoadingLivData;
    }


    //_________________________hitting api_________________________________


    public void getReservionData(UserModel userModel) {
        isLoadingLivData.setValue(true);


        Api.getService(Tags.base_url)
                .getCurrentReservation("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<ReservionDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<ReservionDataModel> response) {
                        isLoadingLivData.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                List<ResevisionModel> list = response.body().getData();
                                listMutableLiveData.setValue(list);
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

    public void deleteReservation(Context context, ResevisionModel model, UserModel userModel) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url).deleteReservation("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "", model.getId() + "","refused")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<StatusResponse> statusResponseResponse) {
                        dialog.dismiss();

                        if (statusResponseResponse.isSuccessful()) {
                            if (statusResponseResponse.body() != null) {
                                Log.e(TAG, statusResponseResponse.body().getStatus() + "");
                                if (statusResponseResponse.body().getStatus() == 200) {
                                    getReservionData(userModel);
                                }
                            }

                        } else {

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        dialog.dismiss();

                    }
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
