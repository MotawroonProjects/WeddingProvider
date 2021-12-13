package com.apps.weddingprovider.uis.activity_verification_code;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityVerificationCodeBinding;
import com.apps.weddingprovider.mvvm.ActivityVerificationMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.apps.weddingprovider.uis.activity_sign_up.SignUpActivity;

public class VerificationCodeActivity extends BaseActivity {
    private ActivityVerificationCodeBinding binding;
    private String phone_code = "";
    private String phone = "";
    private ActivityVerificationMvvm activityVerificationMvvm;


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
            setUserModel(userModel);
           navigateToHomeActivity();
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



    }

    private void navigateToSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("phone_code", phone_code);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }
    private void navigateToHomeActivity() {
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(intent);
//        finish();
    }
}