package com.e_co.weddingprovider.uis.activity_login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ActivityLoginBinding;
import com.e_co.weddingprovider.model.LoginModel;
import com.e_co.weddingprovider.uis.activity_base.BaseActivity;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;
import com.e_co.weddingprovider.uis.activity_verification_code.VerificationCodeActivity;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private String phone_code = "";
    private String phone = "";
    private LoginModel model;
    private ActivityResultLauncher<Intent> launcher;
    private int req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }


    private void initView() {
        model = new LoginModel();
        binding.setModel(model);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (req == 1 && result.getResultCode() == RESULT_OK) {
                    navigateToHomeActivity();
                }
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (model.isDataValid(this)) {
                navigateToVerificationCodeActivity();
            }
        });

        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith("0")){
                    binding.edtPhone.setText("");
                }
            }
        });
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToVerificationCodeActivity() {
        req = 1;
        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("phone_code", model.getPhone_code());
        intent.putExtra("phone", model.getPhone());
        launcher.launch(intent);
    }
}