package com.apps.weddingprovider.uis.activity_add_offer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityAddOfferBinding;
import com.apps.weddingprovider.databinding.ActivityAddServiceBinding;
import com.apps.weddingprovider.model.AddOfferModel;
import com.apps.weddingprovider.model.AddServiceModel;
import com.apps.weddingprovider.model.GalleryModel;
import com.apps.weddingprovider.model.ServiceModel;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.mvvm.ActivityAddOfferMvvm;
import com.apps.weddingprovider.preferences.Preferences;
import com.apps.weddingprovider.share.Common;
import com.apps.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AddOfferActivity extends BaseActivity {
    private ActivityAddOfferBinding binding;
    private ActivityAddOfferMvvm activityAddOfferMvvm;
    private AddOfferModel addOfferModel;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private ActivityResultLauncher<Intent> launcher;
    private Uri uri;
    private int selectedReq;
    private ServiceModel serviceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_offer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        serviceModel = (ServiceModel) intent.getSerializableExtra("data");

    }

    private void initView() {
        activityAddOfferMvvm = ViewModelProviders.of(this).get(ActivityAddOfferMvvm.class);
        setUpToolbar(binding.toolbar, getString(R.string.offer), R.color.white, R.color.black);

        activityAddOfferMvvm.onDataSuccess().observe(this, aBoolean -> {
            setResult(RESULT_OK);
            finish();
        });

        addOfferModel = new AddOfferModel();
        binding.setModel(addOfferModel);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                if (selectedReq == READ_REQ) {

                    uri = result.getData().getData();
                    File file = new File(Common.getImagePath(this, uri));
                    binding.icon1.setVisibility(View.GONE);

                    Picasso.get().load(file).fit().into(binding.image1);
                    addOfferModel.setMainImage(uri.toString());
                    binding.setModel(addOfferModel);
                }


            } else if (selectedReq == CAMERA_REQ) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                uri = getUriFromBitmap(bitmap);
                if (uri != null) {
                    String path = Common.getImagePath(this, uri);

                    if (path != null) {
                        binding.icon1.setVisibility(View.GONE);
                        Picasso.get().load(new File(path)).fit().into(binding.image1);
                        addOfferModel.setMainImage(uri.toString());
                        binding.setModel(addOfferModel);
                    }
                }

            }
        });

        binding.flUploadImage.setOnClickListener(view -> {
            openSheet();
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
            if (addOfferModel.isDataValid(this)) {

                activityAddOfferMvvm.addOffer(this, addOfferModel, getUserModel(), serviceModel.getId());
            }
        });
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

}