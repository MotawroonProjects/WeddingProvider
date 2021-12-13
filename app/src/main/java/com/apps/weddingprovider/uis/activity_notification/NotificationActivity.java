package com.apps.weddingprovider.uis.activity_notification;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityNotificationBinding;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;

public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        initView();


    }


    private void initView() {
        setUpToolbar(binding.toolbar, getString(R.string.notifications), R.color.white, R.color.black);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        getData();
    }

    private void getData() {
        binding.cardNoData.setVisibility(View.VISIBLE);
    }
}