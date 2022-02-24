package com.e_co.weddingprovider.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.remote.Api;
import com.e_co.weddingprovider.share.Common;
import com.e_co.weddingprovider.tags.Tags;
import com.e_co.weddingprovider.uis.activity_verification_code.VerificationCodeActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ActivityVerificationMvvm extends AndroidViewModel {
    private static final String TAG = "ActivityVerificationMvvm";
    private Context context;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String smsCode;
    private boolean canSend = false;
    private String time;
    private String phone, phone_code;
    public MutableLiveData<String> smscode = new MutableLiveData<>();
    public MutableLiveData<Boolean> canresnd = new MutableLiveData<>();
    public MutableLiveData<String> timereturn = new MutableLiveData<>();
    public MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> found = new MutableLiveData<>();
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private CompositeDisposable disposable = new CompositeDisposable();

    public ActivityVerificationMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        mAuth = FirebaseAuth.getInstance();


    }

    public void sendSmsCode(String lang, String phone_code, String phone, VerificationCodeActivity activity) {
        startTimer();
        this.phone_code = phone_code;
        this.phone = phone;
        mAuth.setLanguageCode(lang);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                smsCode = phoneAuthCredential.getSmsCode();
                smscode.postValue(smsCode);
                checkValidCode(smsCode, activity);
            }

            @Override
            public void onCodeSent(@NonNull String verification_id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verification_id, forceResendingToken);
                verificationId = verification_id;
                ActivityVerificationMvvm.this.forceResendingToken = forceResendingToken;

            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("dkdkdk", e.toString());
                if (e.getMessage() != null) {
                } else {

                }
            }
        };
        PhoneAuthOptions options = new PhoneAuthOptions.Builder(mAuth)
                .setForceResendingToken(forceResendingToken)
                .setActivity(activity)
                .setPhoneNumber(phone_code + phone)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setCallbacks(mCallBack)
                .build();


        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void startTimer() {
        canSend = false;
        canresnd.postValue(canSend);
        Observable.intervalRange(1, 120, 1, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        long diff = 120 - aLong;
                        int min = ((int) diff / 60);
                        int sec = ((int) diff % 60);
                        time = String.format(Locale.ENGLISH, "%02d:%02d", min, sec);
                        timereturn.postValue(time);


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        canSend = true;
                        time = "00:00";
                        timereturn.postValue("00:00");

                        canresnd.postValue(true);
                    }
                });

    }


    public void checkValidCode(String code, VerificationCodeActivity activity) {
        //login(activity);
        ProgressDialog dialog = Common.createProgressDialog(activity, activity.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        login(activity, dialog);
                    }).addOnFailureListener(e -> {
                dialog.dismiss();
                if (e.getMessage() != null) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();

                } else {


                }
            });
        } else {
            Toast.makeText(activity, "Wait sms maybe take a few minutes", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            // Toast.makeText(context, "wait sms", Toast.LENGTH_SHORT).show();
        }

    }

    private void login(Context context, ProgressDialog dialog) {

        Api.getService(Tags.base_url).login(Tags.api_key, phone_code, phone).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new SingleObserver<Response<UserModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Response<UserModel> userModelResponse) {
                dialog.dismiss();


                if (userModelResponse.isSuccessful()) {
                    Log.e("status", userModelResponse.body().getStatus() + "");
                    if (userModelResponse.body().getStatus() == 200) {

                        userModelMutableLiveData.postValue(userModelResponse.body());
                    } else if (userModelResponse.body().getStatus() == 401) {
                        found.postValue("not_found");
                    } else if (userModelResponse.body().getStatus() == 409) {
                        Toast.makeText(context, context.getResources().getString(R.string.user_blocked), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Log.e("error", userModelResponse.code() + "_" + userModelResponse.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("errorLogin", e.toString());
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
