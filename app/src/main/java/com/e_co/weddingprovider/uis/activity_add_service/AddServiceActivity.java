package com.e_co.weddingprovider.uis.activity_add_service;

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
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
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

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.adapter.ImageAddServiceAdapter;
import com.e_co.weddingprovider.adapter.SpinnerDepartmentAdapter;
import com.e_co.weddingprovider.databinding.ActivityAddServiceBinding;
import com.e_co.weddingprovider.databinding.AddAdditionalRowBinding;
import com.e_co.weddingprovider.databinding.AddImagesRowBinding;
import com.e_co.weddingprovider.databinding.BottomSheetServiceDetailsBinding;
import com.e_co.weddingprovider.databinding.BottomSheetVideoLinkBinding;
import com.e_co.weddingprovider.model.AddAdditionalItemModel;
import com.e_co.weddingprovider.model.AddServiceModel;
import com.e_co.weddingprovider.model.DepartmentModel;
import com.e_co.weddingprovider.model.GalleryModel;
import com.e_co.weddingprovider.model.ServiceModel;
import com.e_co.weddingprovider.model.SingleServiceDataModel;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.mvvm.ActivityAddServiceMvvm;
import com.e_co.weddingprovider.preferences.Preferences;
import com.e_co.weddingprovider.share.Common;
import com.e_co.weddingprovider.uis.activity_base.BaseActivity;
import com.e_co.weddingprovider.uis.activity_base.FragmentMapTouchListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ServiceModel serviceModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_service);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        serviceModel = (ServiceModel) intent.getSerializableExtra("data");

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
                    Toast.makeText(AddServiceActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
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
        activityAddServiceMvvm.onDeletedSuccess().observe(this, integer -> {

            if (imagesUriList.size() > 0) {
                imagesUriList.remove(integer);
                imageAddServiceAdapter.notifyItemRemoved(integer);
                setResult(RESULT_OK);
                finish();
            }

        });

        activityAddServiceMvvm.getCategoryWeddingHall().observe(this, list -> {
            departmentModelList.clear();
            if (list.size() > 0) {
                departmentModelList.addAll(list);
                spinnerDepartmentAdapter.notifyDataSetChanged();
                if (serviceModel != null) {
                    int pos = getDepartmentPos(serviceModel.getDepartment_id());
                    binding.spDepart.setSelection(pos);
                }

            }
        });
        spinnerDepartmentAdapter = new SpinnerDepartmentAdapter(departmentModelList, this);
        binding.spDepart.setAdapter(spinnerDepartmentAdapter);
        binding.spDepart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addServiceModel.setDepartment_id(departmentModelList.get(i).getId());
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
        binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                binding.progBar.setVisibility(View.GONE);

            }


        });
        binding.recViewimage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAddServiceAdapter = new ImageAddServiceAdapter(imagesUriList, this);
        binding.recViewimage.setAdapter(imageAddServiceAdapter);
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
        binding.cardViewAddImage.setOnClickListener(view -> {
            type = "gallery";
            openSheet();
        });
        binding.llAddVideo.setOnClickListener(view -> {
            createSheetDialog(addServiceModel.getYoutubeLink());
            // checkVideoPermission();
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
                if (serviceModel == null) {
                    activityAddServiceMvvm.addService(this, addServiceModel, userModel);

                } else {
                    activityAddServiceMvvm.updateService(this, addServiceModel, userModel, serviceModel.getId());

                }
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

        if (serviceModel != null) {
            addServiceModel.setName(serviceModel.getName());
            addServiceModel.setMainImage(serviceModel.getMain_image());
            addServiceModel.setPrice(serviceModel.getPrice());
            addServiceModel.setMaxNumber(serviceModel.getMax_limit());
            addServiceModel.setDepartment_id(serviceModel.getDepartment_id());
            addServiceModel.setDescription(serviceModel.getText());
            addServiceModel.setAddress(serviceModel.getAddress());
            addServiceModel.setLat(Double.parseDouble(serviceModel.getLatitude()));
            addServiceModel.setLng(Double.parseDouble(serviceModel.getLongitude()));
            addServiceModel.setYoutubeLink(serviceModel.getVideo_link());
            binding.progBar.setVisibility(View.VISIBLE);
            for (ServiceModel.ServiceMainItem mainItem : serviceModel.getService_main_items()) {
                AddAdditionalItemModel model = new AddAdditionalItemModel();
                model.setName(mainItem.getName());
                model.setAmount(mainItem.getDetails());
                AddAdditionalRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.add_additional_row, null, false);
                rowBinding.setModel(model);
                mainItemList.add(rowBinding);
                binding.llMainItem.addView(rowBinding.getRoot());

                rowBinding.imDelete.setOnClickListener(v -> {
                    mainItemList.remove(rowBinding);
                    binding.llMainItem.removeView(rowBinding.getRoot());
                });


            }

            for (ServiceModel.ServiceExtraItem extraItem : serviceModel.getService_extra_items()) {
                AddAdditionalItemModel model = new AddAdditionalItemModel();
                model.setName(extraItem.getName());
                model.setAmount(extraItem.getPrice());
                AddAdditionalRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.add_additional_row, null, false);
                rowBinding.edtAmount.setHint(getResources().getString(R.string.price));
                rowBinding.setModel(model);
                extraItemList.add(rowBinding);
                binding.llExtraItem.addView(rowBinding.getRoot());
                rowBinding.imDelete.setOnClickListener(v -> {
                    extraItemList.remove(rowBinding);
                    binding.llExtraItem.removeView(rowBinding.getRoot());
                });

            }
            addServiceModel.setMainItemList(mainItemList);
            addServiceModel.setExtraItemList(extraItemList);
            binding.setModel(addServiceModel);
            binding.webView.loadUrl(serviceModel.getVideo_link());

            for (ServiceModel.ServiceImage image : serviceModel.getService_images()) {
                GalleryModel model = new GalleryModel(image.getId(), image.getImage(), "online");
                imagesUriList.add(model);
            }
            imageAddServiceAdapter.notifyDataSetChanged();


            Picasso.get().load(Uri.parse(serviceModel.getMain_image())).into(binding.image1);
            binding.icon1.setVisibility(View.GONE);
            addServiceModel.setGalleryImages(imagesUriList);
            addServiceModel.setVideoUri(serviceModel.getVideo());


        }


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
                            imagesUriList.add(new GalleryModel("", uri.toString(), "local"));
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
                                    imagesUriList.add(new GalleryModel("", uri.toString(), "local"));
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
                                    imagesUriList.add(new GalleryModel("", uri.toString(), "local"));
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

        updateUI();
        checkPermission();
    }

    public void createSheetDialog(String url) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetVideoLinkBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_video_link, null, false);
        dialog.setContentView(binding.getRoot());
        binding.edtLink.setText(url);

        binding.btnSave.setOnClickListener(view -> {
            String link = binding.edtLink.getText().toString();
            if (!link.isEmpty() && extractYTId(link) != null) {
                String vidId = extractYTId(link);
                Common.CloseKeyBoard(this, binding.edtLink);
                String newLink = "http://www.youtube.com/embed/" + vidId;
                addServiceModel.setYoutubeLink(newLink);
                AddServiceActivity.this.binding.progBar.setVisibility(View.VISIBLE);
                AddServiceActivity.this.binding.webView.loadUrl(newLink);
                binding.edtLink.setError(null);
                dialog.dismiss();


            } else {
                binding.edtLink.setError(getString(R.string.field_required));

            }
        });
        binding.imageClose.setOnClickListener(v -> {
            dialog.dismiss();
        });


        dialog.show();

    }

    private String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile("^https?://.*(?:www\\.youtube\\.com/|v/|u/\\w/|embed/|watch\\?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }

    private int getDepartmentPos(String department_id) {
        int pos = 0;

        for (int index = 0; index < departmentModelList.size(); index++) {
            DepartmentModel departmentModel = departmentModelList.get(index);
            if (department_id.equals(departmentModel.getId())) {
                pos = index;
                return pos;
            }
        }

        return pos;
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
            mainItemList.remove(rowBinding);
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
            extraItemList.remove(rowBinding);
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
            if (imagesUriList.get(adapterPosition).getType().equals("local")) {
                imagesUriList.remove(adapterPosition);
                imageAddServiceAdapter.notifyItemRemoved(adapterPosition);
            } else {
                if (imagesUriList.size() > 1) {
                    activityAddServiceMvvm.deleteServiceImage(this, getUserModel(), imagesUriList.get(adapterPosition).getId(), adapterPosition);
                } else {
                    Toast.makeText(this, R.string.only_one_image, Toast.LENGTH_SHORT).show();
                }
            }


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
            } else {
                Toast.makeText(AddServiceActivity.this, R.string.vid_du_less, Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        binding.webView.onPause();
    }


    @Override
    protected void onPause() {
        super.onPause();
        binding.webView.onPause();

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

            if (serviceModel != null) {
                addMarker(Double.parseDouble(serviceModel.getLatitude()), Double.parseDouble(serviceModel.getLongitude()));
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