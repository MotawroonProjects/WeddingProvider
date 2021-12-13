package com.apps.weddingprovider.uis.activity_login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityLoginBinding;
import com.apps.weddingprovider.model.LoginModel;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.apps.weddingprovider.uis.activity_verification_code.VerificationCodeActivity;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private String phone_code = "";
    private String phone = "";
    private LoginModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }


    private void initView() {
        model = new LoginModel();
        binding.setModel(model);


        binding.btnLogin.setOnClickListener(v -> {
            if (model.isDataValid(this)) {
                navigateToVerificationCodeActivity();
            }
        });
    }

    private void navigateToVerificationCodeActivity() {
        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("phone_code", model.getPhone_code());
        intent.putExtra("phone", model.getPhone());
        startActivity(intent);
    }
}