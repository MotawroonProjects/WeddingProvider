package com.apps.weddingprovider.uis.activity_add_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.adapter.ImageAddServiceAdapter;
import com.apps.weddingprovider.adapter.SpinnerDepartmentAdapter;
import com.apps.weddingprovider.databinding.ActivityAddServiceBinding;
import com.apps.weddingprovider.databinding.AddAdditionalRowBinding;
import com.apps.weddingprovider.databinding.AddImagesRowBinding;
import com.apps.weddingprovider.model.AddAdditionalItemModel;
import com.apps.weddingprovider.model.AddServiceModel;
import com.apps.weddingprovider.model.DepartmentModel;
import com.apps.weddingprovider.model.GalleryModel;
import com.apps.weddingprovider.model.SingleServiceDataModel;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.mvvm.ActivityAddServiceMvvm;
import com.apps.weddingprovider.preferences.Preferences;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.apps.weddingprovider.uis.activity_base.FragmentMapTouchListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddServiceActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityAddServiceBinding binding;

    private UserModel userModel;
    private AddServiceModel addServiceModel;
    private Preferences preferences;
    private ActivityResultLauncher<Intent> launcher;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2, VIDEO_REQ = 3;
    private String type;
    private int selectedReq = 0;
    private Uri uri = null, videouri;
    private ImageAddServiceAdapter imageAddServiceAdapter;
    private List<GalleryModel> imagesUriList;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long currentPosition = 0;
    private boolean playWhenReady = true;
    private List<AddAdditionalRowBinding> mainItemList;
    private List<AddAdditionalRowBinding> extraItemList;
    private ActivityAddServiceMvvm activityAddServiceMvvm;
    private GoogleMap mMap;
    private float zoom = 15.0f;
    private ActivityResultLauncher<String> permissionLauncher;
    private SpinnerDepartmentAdapter spinnerDepartmentAdapter;
    private List<DepartmentModel> departmentModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_service);
        initView();

    }

    private void initView() {
        addServiceModel = new AddServiceModel();
        departmentModelList = new ArrayList<>();
        activityAddServiceMvvm = ViewModelProviders.of(this).get(ActivityAddServiceMvvm.class);
        activityAddServiceMvvm.setContext(this);
        activityAddServiceMvvm.setLang(getLang());
        activityAddServiceMvvm.singleServiceDataModelMutableLiveData.observe(this, new Observer<SingleServiceDataModel>() {
            @Override
            public void onChanged(SingleServiceDataModel singleServiceDataModel) {
                if (singleServiceDataModel != null) {
                    Intent intent = getIntent();
                    intent.putExtra("service_id", singleServiceDataModel.getData().getId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        activityAddServiceMvvm.getLocationData().observe(this, locationModel -> {

            addMarker(locationModel.getLat(), locationModel.getLng());
            addServiceModel.setLat(locationModel.getLat());
            addServiceModel.setLng(locationModel.getLng());
            addServiceModel.setAddress(locationModel.getAddress());
            binding.setModel(addServiceModel);

        });


        activityAddServiceMvvm.getCategoryWeddingHall().observe(this, weddingHallModels -> {
            departmentModelList.clear();
            if (weddingHallModels.size() > 0) {
                departmentModelList.addAll(weddingHallModels);
                spinnerDepartmentAdapter.notifyDataSetChanged();
            }
        });
        spinnerDepartmentAdapter = new SpinnerDepartmentAdapter(departmentModelList, this);
        binding.spDepart.setAdapter(spinnerDepartmentAdapter);
        binding.spDepart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    addServiceModel.setDepartment_id("");

                } else {
                    addServiceModel.setDepartment_id(departmentModelList.get(i).getId());


                }
                //  Log.e("cc",city_id);

                binding.setModel(addServiceModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mainItemList = new ArrayList<>();
        extraItemList = new ArrayList<>();
        addServiceModel = new AddServiceModel();
        imagesUriList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        setUpToolbar(binding.toolbar, getString(R.string.add_service), R.color.white, R.color.black);
        binding.setModel(addServiceModel);
        binding.recViewimage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAddServiceAdapter = new ImageAddServiceAdapter(imagesUriList, this);
        binding.recViewimage.setAdapter(imageAddServiceAdapter);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                if (selectedReq == READ_REQ) {

                    uri = result.getData().getData();
                    File file = new File(Common.getImagePath(this, uri));
                    if (type.equals("mainImage")) {
                        binding.icon1.setVisibility(View.GONE);

                        Picasso.get().load(file).fit().into(binding.image1);
                        addServiceModel.setMainImage(uri.toString());
                        binding.setModel(addServiceModel);
                    } else {
                        if (imagesUriList.size() < 5) {
                            imagesUriList.add(new GalleryModel(uri.toString(), "local"));
                            imageAddServiceAdapter.notifyItemInserted(imagesUriList.size() - 1);
                            addServiceModel.setGalleryImages(imagesUriList);
                            binding.setModel(addServiceModel);
                        } else {
                            Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                        }
                    }


                } else if (selectedReq == CAMERA_REQ) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    uri = getUriFromBitmap(bitmap);
                    if (uri != null) {
                        String path = Common.getImagePath(this, uri);

                        if (path != null) {
                            if (type.equals("mainImage")) {
                                binding.icon1.setVisibility(View.GONE);
                                Picasso.get().load(new File(path)).fit().into(binding.image1);
                                addServiceModel.setMainImage(uri.toString());
                                binding.setModel(addServiceModel);
                            } else {
                                if (imagesUriList.size() < 5) {
                                    imagesUriList.add(new GalleryModel(uri.toString(), "local"));
                                    imageAddServiceAdapter.notifyItemInserted(imagesUriList.size() - 1);
                                    addServiceModel.setGalleryImages(imagesUriList);
                                    binding.setModel(addServiceModel);
                                } else {
                                    Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (type.equals("mainImage")) {
                                binding.icon1.setVisibility(View.GONE);
                                Picasso.get().load(uri).fit().into(binding.image1);
                                addServiceModel.setMainImage(uri.toString());
                                binding.setModel(addServiceModel);
                            } else {
                                if (imagesUriList.size() < 5) {
                                    imagesUriList.add(new GalleryModel(uri.toString(), "local"));
                                    imageAddServiceAdapter.notifyItemInserted(imagesUriList.size() - 1);
                                    addServiceModel.setGalleryImages(imagesUriList);
                                    binding.setModel(addServiceModel);
                                } else {
                                    Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                } else if (selectedReq == VIDEO_REQ) {
                    Uri uri = result.getData().getData();
                    new VideoTask().execute(uri);
                }
            }
        });
        binding.llAddMainItem.setOnClickListener(view -> {
            addMainItem();
        });

        binding.llAddExtraItem.setOnClickListener(view -> {
            addExtraItem();
        });

        binding.flUploadImage.setOnClickListener(view -> {
            type = "mainImage";
            openSheet();
        });
        binding.flAddImage.setOnClickListener(view -> {
            type = "gallery";
            openSheet();
        });
        binding.llAddVideo.setOnClickListener(view -> {
            checkVideoPermission();
        });
        binding.flGallery.setOnClickListener(view -> {
            closeSheet();
            checkReadPermission();
        });

        binding.flCamera.setOnClickListener(view -> {
            closeSheet();
            checkCameraPermission();
        });

        binding.btnSave.setOnClickListener(view -> {
            if (addServiceModel.isDataValid(this)) {

            }
        });
        binding.btnCancel.setOnClickListener(view -> closeSheet());
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                activityAddServiceMvvm.initGoogleApi();

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

            }
        });
        activityAddServiceMvvm.getDepartment(this);

        updateUI();
        checkPermission();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, BaseActivity.fineLocPerm) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(BaseActivity.fineLocPerm);
        } else {

            activityAddServiceMvvm.initGoogleApi();
        }
    }


    private void updateUI() {
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

        FragmentMapTouchListener fragmentMapTouchListener = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        fragmentMapTouchListener.setListener(() -> binding.nestedscroll.requestDisallowInterceptTouchEvent(true));


    }


    private void addMainItem() {
        AddAdditionalItemModel model = new AddAdditionalItemModel();
        AddAdditionalRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.add_additional_row, null, false);
        rowBinding.setModel(model);
        mainItemList.add(rowBinding);
        binding.llMainItem.addView(rowBinding.getRoot());
        rowBinding.imDelete.setOnClickListener(v -> {
            binding.llMainItem.removeView(rowBinding.getRoot());
        });
        addServiceModel.setMainItemList(mainItemList);
        binding.setModel(addServiceModel);

    }

    private void addExtraItem() {
        AddAdditionalItemModel model = new AddAdditionalItemModel();
        AddAdditionalRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.add_additional_row, null, false);
        rowBinding.edtAmount.setHint(getResources().getString(R.string.price));
        rowBinding.setModel(model);
        extraItemList.add(rowBinding);
        binding.llExtraItem.addView(rowBinding.getRoot());
        rowBinding.imDelete.setOnClickListener(v -> {
            binding.llExtraItem.removeView(rowBinding.getRoot());
        });
        addServiceModel.setExtraItemList(extraItemList);
        binding.setModel(addServiceModel);
    }

    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }


    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }

    public void checkVideoPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, VIDEO_REQ);
        } else {
            displayVideoIntent(VIDEO_REQ);
        }
    }

    private void displayVideoIntent(int video_req) {
        selectedReq = video_req;

        Intent intent = new Intent();

        if (video_req == VIDEO_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("video/*");
            launcher.launch(intent);

        }
    }

    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {
        selectedReq = req;
        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");

            launcher.launch(intent);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                launcher.launch(intent);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }

    public void deleteImage(int adapterPosition) {
        if (imagesUriList.size() > 0) {
            imagesUriList.remove(adapterPosition);
            imageAddServiceAdapter.notifyItemRemoved(adapterPosition);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VideoTask extends AsyncTask<Uri, Void, Long> {
        MediaMetadataRetriever retriever;
        private Uri uri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retriever = new MediaMetadataRetriever();
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            uri = uris[0];
            retriever.setDataSource(AddServiceActivity.this, uris[0]);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(time) / 1000;
            retriever.release();
            return duration;
        }

        @Override
        protected void onPostExecute(Long duration) {
            super.onPostExecute(duration);
            Log.e("duration", duration + "__");
            if (duration <= 90) {
                videouri = uri;
                Log.e("uri", uri + "");
                addServiceModel.setVideoUri(uri.toString());
                initPlayer(videouri);
            } else {
                Toast.makeText(AddServiceActivity.this, R.string.vid_du_less, Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void initPlayer(Uri uri) {


        if (uri != null) {

            DataSource.Factory factory = new DefaultDataSourceFactory(this, "Wedding");


            if (player == null) {
                player = new SimpleExoPlayer.Builder(this).build();
                binding.player.setPlayer(player);
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);
                player.prepare(mediaSource);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            } else {

                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(uri);

                player.seekTo(currentWindow, currentPosition);
                player.setPlayWhenReady(playWhenReady);
                player.prepare(mediaSource);
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            release();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            release();
        }
    }


    private void release() {
        if (player != null) {
            currentWindow = player.getCurrentWindowIndex();
            currentPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer(videouri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            if (activityAddServiceMvvm.getGoogleMap().getValue() == null) {
                activityAddServiceMvvm.setmMap(mMap);

            }


        }
    }

    private void addMarker(double lat, double lng) {
        if (activityAddServiceMvvm.getGoogleMap().getValue() != null) {
            mMap = activityAddServiceMvvm.getGoogleMap().getValue();
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            activityAddServiceMvvm.startLocationUpdate();

        }
    }

}