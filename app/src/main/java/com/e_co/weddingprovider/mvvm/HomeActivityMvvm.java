package com.e_co.weddingprovider.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.e_co.weddingprovider.model.NotificationCount;
import com.e_co.weddingprovider.model.StatusResponse;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.remote.Api;
import com.e_co.weddingprovider.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeActivityMvvm extends AndroidViewModel {
    private MutableLiveData<Boolean> onServiceAdded = new MutableLiveData<>();
    public MutableLiveData<String> firebase = new MutableLiveData<>();
    private MutableLiveData<String> count;

    private CompositeDisposable disposable = new CompositeDisposable();

    public HomeActivityMvvm(@NonNull Application application) {
        super(application);


    }


    public MutableLiveData<String> getCount() {
        if (count == null) {
            count = new MutableLiveData<>();
        }

        return count;
    }

    public MutableLiveData<Boolean> getOnServiceAdded() {
        if (onServiceAdded == null) {
            onServiceAdded = new MutableLiveData<>();
        }

        return onServiceAdded;
    }

    public void updateFirebase(Context context, UserModel userModel) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener((Activity) context, task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                Api.getService(Tags.base_url).updateFirebasetoken("Bearer " + userModel.getData().getToken(), Tags.api_key, token, userModel.getData().getId() + "", "android").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<StatusResponse> statusResponseResponse) {
                        if (statusResponseResponse.isSuccessful()) {
                            if (statusResponseResponse.body().getStatus() == 200) {
                                firebase.postValue(token);
                                Log.e("token", "updated successfully");
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
            }
        });


    }

    public void getNotificationCount(UserModel userModel) {
        if (userModel == null) {
            return;
        }
        Api.getService(Tags.base_url)
                .getNotificationCount("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<NotificationCount>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    count.setValue(response.body().getData());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("TAG", "onError: ", e);
                    }
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
