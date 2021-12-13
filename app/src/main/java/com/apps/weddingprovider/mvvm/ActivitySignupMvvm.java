package com.apps.weddingprovider.mvvm;

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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.model.LocationModel;
import com.apps.weddingprovider.model.PlaceGeocodeData;
import com.apps.weddingprovider.model.PlaceMapDetailsData;
import com.apps.weddingprovider.model.SignUpModel;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.remote.Api;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.tags.Tags;
import com.apps.weddingprovider.uis.activity_sign_up.SignUpActivity;
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
import com.google.android.gms.maps.OnMapReadyCallback;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ActivitySignupMvvm extends AndroidViewModel  implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    public MutableLiveData<UserModel> userModelMutableLiveData = new MutableLiveData<>();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MutableLiveData<LocationModel> locationModelMutableLiveData;
    private MutableLiveData<String> address;
    private MutableLiveData<GoogleMap> mMap;
    private CompositeDisposable disposable = new CompositeDisposable();
    private SignUpActivity activity;

    public ActivitySignupMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void signupWithOutImage(Context context, SignUpModel model, String phone_code, String phone) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url).signUp(Tags.api_key, model.getAddress() + " " + model.getName(), phone_code.replace("+", ""), phone,model.getLat()+"",model.getLng()+"",model.getAddress(), "android").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new SingleObserver<Response<UserModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Response<UserModel> userModelResponse) {
                dialog.dismiss();
                if (userModelResponse.isSuccessful()) {
                    if (userModelResponse.body().getStatus() == 200) {

                        userModelMutableLiveData.postValue(userModelResponse.body());
                    } else if (userModelResponse.body().getStatus() == 405) {
                        Toast.makeText(context, context.getResources().getString(R.string.user_found), Toast.LENGTH_LONG).show();
                    }
                } else {

                }

            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                dialog.dismiss();

            }
        });
    }

    public void signupWithImage(Context context, SignUpModel model, String phone_code, String phone, Uri uri) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody api_part = Common.getRequestBodyText(Tags.api_key);
        RequestBody name_part = Common.getRequestBodyText( model.getName());
        RequestBody soft_part = Common.getRequestBodyText("android");
        RequestBody phone_part = Common.getRequestBodyText(phone);
        RequestBody lat_part = Common.getRequestBodyText( model.getLat()+"");
        RequestBody lng_part = Common.getRequestBodyText(model.getLng()+"");
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody phone_code_part = Common.getRequestBodyText(phone_code.replace("+", ""));


        MultipartBody.Part image = Common.getMultiPart(context, uri, "logo");


        Api.getService(Tags.base_url).signUpwithImage(api_part, name_part, phone_code_part, phone_part,lat_part,lng_part,address_part, soft_part, image).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new Observer<Response<UserModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Response<UserModel> userModelResponse) {
                dialog.dismiss();
                if (userModelResponse.isSuccessful()) {
                    if (userModelResponse.body().getStatus() == 200) {

                        userModelMutableLiveData.postValue(userModelResponse.body());
                    } else if (userModelResponse.body().getStatus() == 405) {
                        Toast.makeText(context, context.getResources().getString(R.string.user_found), Toast.LENGTH_LONG).show();
                    }
                } else {

                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                dialog.dismiss();
            }

            @Override
            public void onComplete() {
                dialog.dismiss();
            }
        });
    }



    public LiveData<LocationModel> getLocationData() {
        if (locationModelMutableLiveData == null) {
            locationModelMutableLiveData = new MutableLiveData<>();
        }

        return locationModelMutableLiveData;
    }



    public LiveData<GoogleMap> getGoogleMap() {
        if (mMap == null) {
            mMap = new MutableLiveData<>();
        }

        return mMap;
    }

    public MutableLiveData<String> getAddress() {
        if(address==null){
            address=new MutableLiveData<>();
        }
        return address;
    }

    public void setmMap(GoogleMap googleMap) {
        mMap.setValue(googleMap);
    }
    public void Search(String query,String lang) {

       // binding.progBar.setVisibility(View.VISIBLE);

        String fields = "id,place_id,name,geometry,formatted_address";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, lang, context.getResources().getString(R.string.search_key)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new SingleObserver<Response<PlaceMapDetailsData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Response<PlaceMapDetailsData> placeMapDetailsDataResponse) {
                if (placeMapDetailsDataResponse.isSuccessful() && placeMapDetailsDataResponse.body() != null) {


                    if (placeMapDetailsDataResponse.body().getCandidates().size() > 0) {

                         address.postValue(placeMapDetailsDataResponse.body().getCandidates().get(0).getFormatted_address().replace("Unnamed Road,", ""));
                        LocationModel locationModel = new LocationModel(placeMapDetailsDataResponse.body().getCandidates().get(0).getGeometry().getLocation().getLat(), placeMapDetailsDataResponse.body().getCandidates().get(0).getGeometry().getLocation().getLng());
                        locationModelMutableLiveData.setValue(locationModel);

                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });}
    public void getGeoData(final double lat, double lng,String lang) {
        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang,context.getResources().getString(R.string.search_key)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new SingleObserver<Response<PlaceGeocodeData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Response<PlaceGeocodeData> placeGeocodeDataResponse) {
                if(placeGeocodeDataResponse.isSuccessful()){
                if (placeGeocodeDataResponse.body().getResults().size() > 0) {
                   // binding.btnSelect.setVisibility(View.VISIBLE);
                    address.postValue(placeGeocodeDataResponse.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", ""));

                }}
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
}
    public void setContext(Context context){
        activity = (SignUpActivity) context;
    }
    public void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());
        result.setResultCallback(locationSettingsResult -> {
            Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(activity, 100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("MissingPermission")
    public void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(activity)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LocationModel locationModel = new LocationModel(lat, lng);
        locationModelMutableLiveData.setValue(locationModel);
        if (googleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }


    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        if (googleApiClient != null) {
            if (locationCallback != null) {
                LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
                googleApiClient.disconnect();
                googleApiClient = null;
            }
        }
    }
}
