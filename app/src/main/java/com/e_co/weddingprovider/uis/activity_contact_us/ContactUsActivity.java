package com.e_co.weddingprovider.uis.activity_contact_us;

import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ActivityContactUsBinding;
import com.e_co.weddingprovider.model.ContactUsModel;
import com.e_co.weddingprovider.model.UserModel;
import com.e_co.weddingprovider.mvvm.ContactusActivityMvvm;
import com.e_co.weddingprovider.preferences.Preferences;
import com.e_co.weddingprovider.uis.activity_base.BaseActivity;

public class ContactUsActivity extends BaseActivity {
    private ActivityContactUsBinding binding;
    private ContactUsModel contactUsModel;
    private ContactusActivityMvvm contactusActivityMvvm;
    private UserModel userModel;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);
        initView();

    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        contactusActivityMvvm = ViewModelProviders.of(this).get(ContactusActivityMvvm.class);
        setUpToolbar(binding.toolbar, getString(R.string.contact_us), R.color.white, R.color.black);

        contactUsModel = new ContactUsModel();
        if (userModel != null) {
            contactUsModel.setName(userModel.getData().getName());

        }

        binding.setContactModel(contactUsModel);
        binding.btnSend.setOnClickListener(view -> {
            if (contactUsModel.isDataValid(this)) {
                contactusActivityMvvm.contactus(this, contactUsModel);
            }
        });
        contactusActivityMvvm.send.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.suc), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }


}