package com.e_co.weddingprovider.uis.activity_verification_code;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ActivityVerificationCodeBinding;
import com.e_co.weddingprovider.mvvm.ActivityVerificationMvvm;
import com.e_co.weddingprovider.uis.activity_base.BaseActivity;
import com.e_co.weddingprovider.uis.activity_sign_up.SignUpActivity;

public class VerificationCodeActivity extends BaseActivity {
    private ActivityVerificationCodeBinding binding;
    private String phone_code = "";
    private String phone = "";
    private ActivityVerificationMvvm activityVerificationMvvm;
    private ActivityResultLauncher<Intent> launcher;
    private int req;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_code);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        phone_code = intent.getStringExtra("phone_code");
        phone = intent.getStringExtra("phone");
    }

    private void initView() {

        activityVerificationMvvm = ViewModelProviders.of(this).get(ActivityVerificationMvvm.class);
        activityVerificationMvvm.sendSmsCode(getLang(), phone_code, phone, this);
        activityVerificationMvvm.smscode.observe(this, smsCode -> {
            binding.edtCode.setText(smsCode);
        });
        activityVerificationMvvm.canresnd.observe(this, canResend -> {
            if (canResend) {
                binding.tvResend.setVisibility(View.VISIBLE);
                binding.tvResend.setEnabled(true);
            } else {
                binding.tvResend.setVisibility(View.GONE);
                binding.tvResend.setEnabled(false);
            }
        });
        activityVerificationMvvm.timereturn.observe(this, time -> {
            binding.tvCounter.setText(time);
        });
        activityVerificationMvvm.userModelMutableLiveData.observe(this, userModel -> {
            if (userModel.getData().getUser_type().equals("provider")) {
                setUserModel(userModel);
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.plz_login_provider_phone, Toast.LENGTH_SHORT).show();
            }

        });
        activityVerificationMvvm.found.observe(this, s -> {
            if (s != null) {
                navigateToSignUpActivity();
            }
        });
        binding.tvResend.setOnClickListener(view -> activityVerificationMvvm.sendSmsCode(getLang(), phone_code, phone, this));
        binding.setPhone(phone_code + phone);
        binding.btnConfirm.setOnClickListener(view -> {
            String smscode = binding.edtCode.getText().toString();
            if (!smscode.isEmpty()) {
                activityVerificationMvvm.checkValidCode(smscode, this);
            } else {
                binding.edtCode.setError(getResources().getString(R.string.field_required));
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (req == 1 && result.getResultCode() == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }

    private void navigateToSignUpActivity() {
        req = 1;
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("phone_code", phone_code);
        intent.putExtra("phone", phone);
        launcher.launch(intent);
    }


}