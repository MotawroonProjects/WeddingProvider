package com.e_co.weddingprovider.mvvm;

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

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.AddAdditionalRowBinding;
import com.e_co.weddingprovider.model.AddServiceModel;
import com.e_co.weddingprovider.model.DepartmentDataModel;
import com.e_co.weddingprovider.model.DepartmentModel;
import com.e_co.weddingprovider.model.GalleryModel;
import com.e_co.weddingprovider.model.LocationModel;
import com.e_co.weddingprovider.model.PlaceGeocodeData;
import com.e_co.weddingprovider.model.PlaceMapDetailsData;
import com.e_co.weddingprovider.model.SingleServiceDataModel;
import com.e_co.weddingprovider.model.StatusResponse;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.remote.Api;
import com.e_co.weddingprovider.share.Common;
import com.e_co.weddingprovider.tags.Tags;
import com.e_co.weddingprovider.uis.activity_add_service.AddServiceActivity;
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

public class ActivityAddServiceMvvm extends AndroidViewModel implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MutableLiveData<LocationModel> locationModelMutableLiveData;
    private MutableLiveData<GoogleMap> mMap;
    private CompositeDisposable disposable = new CompositeDisposable();
    private AddServiceActivity activity;
    private String lang = "ar";
    private MutableLiveData<List<DepartmentModel>> departmentLivData;
    public MutableLiveData<SingleServiceDataModel> singleServiceDataModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> imageDeletedLivData;

    public ActivityAddServiceMvvm(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public LiveData<List<DepartmentModel>> getCategoryWeddingHall() {
        if (departmentLivData == null) {
            departmentLivData = new MutableLiveData<>();

        }
        return departmentLivData;
    }

    public LiveData<Integer> onDeletedSuccess() {
        if (imageDeletedLivData == null) {
            imageDeletedLivData = new MutableLiveData<>();

        }
        return imageDeletedLivData;
    }

    //_________________________hitting api_________________________________

    public void getDepartment(Context context) {
        Api.getService(Tags.base_url)
                .getDepartments(Tags.api_key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<DepartmentDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<DepartmentDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().getStatus() == 200) {
                                List<DepartmentModel> list = response.body().getData();
                                if (list.size() > 0) {
                                    departmentLivData.setValue(list);
                                } else {
                                    list.add(0, new DepartmentModel(null, context.getString(R.string.ch_depart), true, ""));

                                }


                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: ", e);
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


    public void setmMap(GoogleMap googleMap) {
        mMap.setValue(googleMap);
    }

    public void Search(String query, String lang) {


        String fields = "id,place_id,name,geometry,formatted_address";

        Api.getService("https://maps.googleapis.com/maps/api/")
                .searchOnMap("textquery", query, fields, lang, context.getResources().getString(R.string.search_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<PlaceMapDetailsData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<PlaceMapDetailsData> placeMapDetailsDataResponse) {

                        if (placeMapDetailsDataResponse.isSuccessful() && placeMapDetailsDataResponse.body() != null) {


                            if (placeMapDetailsDataResponse.body().getCandidates().size() > 0) {
                                String address = placeMapDetailsDataResponse.body().getCandidates().get(0).getFormatted_address();
                                LocationModel locationModel = new LocationModel(placeMapDetailsDataResponse.body().getCandidates().get(0).getGeometry().getLocation().getLat(), placeMapDetailsDataResponse.body().getCandidates().get(0).getGeometry().getLocation().getLng(), address);
                                locationModelMutableLiveData.setValue(locationModel);

                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void getGeoData(final double lat, double lng, String lang) {
        String location = lat + "," + lng;
        Api.getService("https://maps.googleapis.com/maps/api/")
                .getGeoData(location, lang, context.getResources().getString(R.string.search_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<PlaceGeocodeData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<PlaceGeocodeData> placeGeocodeDataResponse) {
                        if (placeGeocodeDataResponse.isSuccessful()) {
                            if (placeGeocodeDataResponse.body().getResults().size() > 0) {
                                String address = placeGeocodeDataResponse.body().getResults().get(0).getFormatted_address().replace("Unnamed Road,", "");
                                double lat = placeGeocodeDataResponse.body().getResults().get(0).getGeometry().getLocation().getLat();
                                double lng = placeGeocodeDataResponse.body().getResults().get(0).getGeometry().getLocation().getLng();

                                LocationModel locationModel = new LocationModel(lat, lng, address);
                                locationModelMutableLiveData.setValue(locationModel);

                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void setContext(Context context) {
        activity = (AddServiceActivity) context;
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
        getGeoData(lat, lng, lang);
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

    public void addService(Context context, AddServiceModel model, UserModel userModel) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody api_part = Common.getRequestBodyText(Tags.api_key);
        RequestBody user_part = Common.getRequestBodyText(userModel.getData().getId() + "");

        RequestBody name_part = Common.getRequestBodyText(model.getName());
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody text_part = Common.getRequestBodyText(model.getDescription());
        RequestBody max_part = Common.getRequestBodyText(model.getMaxNumber());
        RequestBody depart_part = Common.getRequestBodyText(model.getDepartment_id());
        RequestBody lat_part = Common.getRequestBodyText(model.getLat() + "");
        RequestBody lng_part = Common.getRequestBodyText(model.getLng() + "");
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody video_part = Common.getRequestBodyText(model.getYoutubeLink());

        List<MultipartBody.Part> service_main_items = new ArrayList<>(getMainAttribute(model));
        List<MultipartBody.Part> service_extra_items = new ArrayList<>(getExtraAttribute(model));

        MultipartBody.Part main_image = Common.getMultiPart(context, Uri.parse(model.getMainImage()), "main_image");
        //MultipartBody.Part video_part = Common.getMultiPartVideo(context, Uri.parse(model.getVideoUri()), "video");
        List<MultipartBody.Part> partimageList = getMultipartBodyList(model.getGalleryImages(), "images[]", context);

        Api.getService(Tags.base_url).addServices("Bearer " + userModel.getData().getToken(), api_part, user_part, name_part, price_part, text_part, max_part, depart_part, service_main_items, service_extra_items, lat_part, lng_part, address_part, video_part, main_image, partimageList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new Observer<Response<SingleServiceDataModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Response<SingleServiceDataModel> serviceDataModelResponse) {
                dialog.dismiss();

                if (serviceDataModelResponse.isSuccessful()) {
                    Log.e("code", serviceDataModelResponse.body().getStatus() + "__");

                    if (serviceDataModelResponse.body() != null && serviceDataModelResponse.body().getStatus() == 200) {
                        singleServiceDataModelMutableLiveData.setValue(serviceDataModelResponse.body());
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

    public void updateService(Context context, AddServiceModel model, UserModel userModel, String service_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody api_part = Common.getRequestBodyText(Tags.api_key);
        RequestBody user_part = Common.getRequestBodyText(userModel.getData().getId() + "");
        RequestBody service_id_part = Common.getRequestBodyText(service_id);

        RequestBody name_part = Common.getRequestBodyText(model.getName());
        RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody text_part = Common.getRequestBodyText(model.getDescription());
        RequestBody max_part = Common.getRequestBodyText(model.getMaxNumber());
        RequestBody depart_part = Common.getRequestBodyText(model.getDepartment_id());
        RequestBody lat_part = Common.getRequestBodyText(model.getLat() + "");
        RequestBody lng_part = Common.getRequestBodyText(model.getLng() + "");
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody video_part = Common.getRequestBodyText(model.getYoutubeLink());

        List<MultipartBody.Part> service_main_items = new ArrayList<>(getMainAttribute(model));
        List<MultipartBody.Part> service_extra_items = new ArrayList<>(getExtraAttribute(model));

        MultipartBody.Part main_image = null;

        if (!model.getMainImage().startsWith("http")) {
            main_image = Common.getMultiPart(context, Uri.parse(model.getMainImage()), "main_image");

        }

     /*   MultipartBody.Part video_part = null;
        if (!model.getVideoUri().startsWith("http")) {
            video_part = Common.getMultiPartVideo(context, Uri.parse(model.getVideoUri()), "video");

        }*/
        List<MultipartBody.Part> partimageList = getMultipartBodyList(model.getGalleryImages(), "images[]", context);

        Api.getService(Tags.base_url).updateServices("Bearer " + userModel.getData().getToken(), api_part, user_part, service_id_part, name_part, price_part, text_part, max_part, depart_part, service_main_items, service_extra_items, lat_part, lng_part, address_part, video_part, main_image, partimageList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io()).subscribe(new Observer<Response<SingleServiceDataModel>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Response<SingleServiceDataModel> serviceDataModelResponse) {
                dialog.dismiss();

                if (serviceDataModelResponse.isSuccessful()) {
                    Log.e("code", serviceDataModelResponse.body().getStatus() + "__");

                    if (serviceDataModelResponse.body() != null && serviceDataModelResponse.body().getStatus() == 200) {
                        singleServiceDataModelMutableLiveData.setValue(serviceDataModelResponse.body());
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

    public void deleteServiceImage(Context context, UserModel userModel, String service_image_id, int adapter_pos) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url).deleteServiceImage("Bearer " + userModel.getData().getToken(), Tags.api_key, service_image_id)
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
                        dialog.dismiss();
                        Log.e("code", response.body().getStatus() + "_");

                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().getStatus() == 200) {
                                imageDeletedLivData.setValue(adapter_pos);

                            } else {
                                Toast.makeText(context, context.getString(R.string.only_one_image), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        dialog.dismiss();
                        Log.e("deleteImageError", e.getMessage());
                    }
                });
    }


    private List<MultipartBody.Part> getMultipartBodyList(List<GalleryModel> uriList, String name, Context context) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        for (int i = 0; i < uriList.size(); i++) {
            if (uriList.get(i).getType().equals("local")) {
                Uri uri = Uri.parse(uriList.get(i).getImage());
                MultipartBody.Part part = Common.getMultiPart(context, uri, name);
                partList.add(part);
            }

        }
        return partList;
    }

    private List<MultipartBody.Part> getMainAttribute(AddServiceModel model) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int index = 0; index < model.getMainItemList().size(); index++) {
            AddAdditionalRowBinding binding = model.getMainItemList().get(index);
            String title = binding.getModel().getName();
            String amount = binding.getModel().getAmount();


            String partName1 = "service_main_items[" + index + "][name]";
            String partName2 = "service_main_items[" + index + "][details]";

            MultipartBody.Part part1 = Common.getMultiPartText(title, partName1);
            MultipartBody.Part part2 = Common.getMultiPartText(amount, partName2);

            parts.add(part1);
            parts.add(part2);

        }
        return parts;
    }

    private List<MultipartBody.Part> getExtraAttribute(AddServiceModel model) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int index = 0; index < model.getExtraItemList().size(); index++) {
            AddAdditionalRowBinding binding = model.getExtraItemList().get(index);
            String title = binding.getModel().getName();
            String amount = binding.getModel().getAmount();

            String partName1 = "service_extra_items[" + index + "][name]";
            String partName2 = "service_extra_items[" + index + "][price]";

            MultipartBody.Part part1 = Common.getMultiPartText(title, partName1);
            MultipartBody.Part part2 = Common.getMultiPartText(amount, partName2);

            parts.add(part1);
            parts.add(part2);

        }
        return parts;
    }
}
