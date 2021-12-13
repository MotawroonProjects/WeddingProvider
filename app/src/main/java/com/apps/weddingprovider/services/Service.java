package com.apps.weddingprovider.services;




import com.apps.weddingprovider.model.PlaceGeocodeData;
import com.apps.weddingprovider.model.PlaceMapDetailsData;
import com.apps.weddingprovider.model.UserModel;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("geocode/json")
    Single<Response<PlaceGeocodeData>> getGeoData(@Query(value = "latlng") String latlng,
                                                  @Query(value = "language") String language,
                                                  @Query(value = "key") String key);

    @GET("place/findplacefromtext/json")
    Single<Response<PlaceMapDetailsData>> searchOnMap(@Query(value = "inputtype") String inputtype,
                                                      @Query(value = "input") String input,
                                                      @Query(value = "fields") String fields,
                                                      @Query(value = "language") String language,
                                                      @Query(value = "key") String key);



    @FormUrlEncoded
    @POST("api/login")
    Single<Response<UserModel>> login(@Field("api_key") String api_key,
                                      @Field("phone_code") String phone_code,
                                      @Field("phone") String phone);

    @FormUrlEncoded
    @POST("api/provider-register")
    Single<Response<UserModel>> signUp(@Field("api_key") String api_key,
                                       @Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude,
                                       @Field("address") String address,
                                       @Field("software_type") String software_type


    );


    @Multipart
    @POST("api/provider-register")
    Observable<Response<UserModel>> signUpwithImage(@Part("api_key") RequestBody api_key,
                                                    @Part("name") RequestBody name,
                                                    @Part("phone_code") RequestBody phone_code,
                                                    @Part("phone") RequestBody phone,
                                                    @Part("latitude") RequestBody latitude,
                                                    @Part("longitude") RequestBody longitude,
                                                    @Part("address") RequestBody address,
                                                    @Part("software_type") RequestBody software_type,
                                                    @Part MultipartBody.Part logo


    );

    @FormUrlEncoded
    @POST("api/client-update-profile")
    Single<Response<UserModel>> editProfile(@Header("AUTHORIZATION") String token,
                                            @Field("api_key") String api_key,
                                            @Field("name") String name,
                                            @Field("user_id") String user_id


    );


    @Multipart
    @POST("api/client-update-profile")
    Observable<Response<UserModel>> editProfilewithImage(@Header("AUTHORIZATION") String token,
                                                         @Part("api_key") RequestBody api_key,
                                                         @Part("name") RequestBody name,
                                                         @Part("user_id") RequestBody user_id,
                                                         @Part MultipartBody.Part logo


    );



}