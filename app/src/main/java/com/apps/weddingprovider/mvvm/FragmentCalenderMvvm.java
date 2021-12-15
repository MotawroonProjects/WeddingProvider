package com.apps.weddingprovider.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.model.DatesDataModel;
import com.apps.weddingprovider.model.ReservionDataModel;
import com.apps.weddingprovider.model.ResevisionModel;
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

public class FragmentCalenderMvvm extends AndroidViewModel {
    private static final String TAG = "FragmentCalMvvm";

    private Context context;
    private MutableLiveData<Boolean> isLoadingLivData;
    private MutableLiveData<List<Calendar>> reservedDatesLiveData;

    private CompositeDisposable disposable = new CompositeDisposable();

    public FragmentCalenderMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoadingLivData == null) {
            isLoadingLivData = new MutableLiveData<>();
        }
        return isLoadingLivData;
    }

    public MutableLiveData<List<Calendar>> getReservedDatesLiveData() {
        if (reservedDatesLiveData == null) {
            reservedDatesLiveData = new MutableLiveData<>();
        }
        return reservedDatesLiveData;
    }


    //_________________________hitting api_________________________________


    public void getDatesData(UserModel userModel, String service_id) {
        isLoadingLivData.setValue(true);


        Api.getService(Tags.base_url)
                .getReservationDates("Bearer " + userModel.getData().getToken(), Tags.api_key, userModel.getData().getId() + "", service_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<DatesDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<DatesDataModel> response) {
                        isLoadingLivData.postValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                List<String> list = response.body().getData();
                                updateDates(list);
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

    //_____________________________________________________________________
    private void updateDates(List<String> list) {
        List<Calendar> calendars = new ArrayList<>();
        for (String date :list){
           Calendar calendar =  getCalenderFromDate(date);
           calendars.add(calendar);

        }
        reservedDatesLiveData.setValue(calendars);
    }

    private Calendar getCalenderFromDate(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date d = simpleDateFormat.parse(date);
            calendar.setTime(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
