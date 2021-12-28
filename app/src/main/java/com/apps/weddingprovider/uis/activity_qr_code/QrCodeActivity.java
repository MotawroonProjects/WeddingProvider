package com.apps.weddingprovider.uis.activity_qr_code;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityQrCodeBinding;
import com.apps.weddingprovider.mvvm.ActivityAddOfferMvvm;
import com.apps.weddingprovider.mvvm.ActivityQrCodeMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrCodeActivity extends BaseActivity {
    private ActivityQrCodeBinding binding;
    private ActivityQrCodeMvvm activityQrCodeMvvm;
    private CodeScanner mCodeScanner;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int CAMERA_REQ = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
        initView();
    }

    private void initView() {
        activityQrCodeMvvm = ViewModelProviders.of(this).get(ActivityQrCodeMvvm.class);
        activityQrCodeMvvm.onDataSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                finish();
            }
        });
        checkCameraPermission();

    }

    private void initScanner() {

        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setScanMode(ScanMode.SINGLE);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            binding.scannerView.setVisibility(View.GONE);
            scanOrder(result.getText());
        }));
        binding.scannerView.setVisibility(View.VISIBLE);
        mCodeScanner.startPreview();
    }

    private void scanOrder(String qr_code) {
        activityQrCodeMvvm.confirmReservation(this, getUserModel(), qr_code);
    }


    public void checkCameraPermission() {


        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            initScanner();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();

            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();

        }
    }
}