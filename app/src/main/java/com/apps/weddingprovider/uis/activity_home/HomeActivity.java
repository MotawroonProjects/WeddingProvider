package com.apps.weddingprovider.uis.activity_home;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ActivityHomeBinding;
import com.apps.weddingprovider.interfaces.Listeners;
import com.apps.weddingprovider.language.Language;
import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.mvvm.HomeActivityMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseActivity;
import com.apps.weddingprovider.uis.activity_login.LoginActivity;
import com.apps.weddingprovider.uis.activity_notification.NotificationActivity;

import io.paperdb.Paper;

public class HomeActivity extends BaseActivity implements Listeners.VerificationListener {
    private ActivityHomeBinding binding;
    private NavController navController;
    private HomeActivityMvvm homeActivityMvvm;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }


    private void initView() {

        homeActivityMvvm = ViewModelProviders.of(this).get(HomeActivityMvvm.class);
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        NavigationUI.setupWithNavController(binding.toolBar, navController, appBarConfiguration);
        NavigationUI.setupActionBarWithNavController(this, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (binding.toolBar.getNavigationIcon() != null) {
                binding.toolBar.getNavigationIcon().setColorFilter(ContextCompat.getColor(HomeActivity.this, R.color.black), PorterDuff.Mode.SRC_ATOP);

            }
        });

        homeActivityMvvm.firebase.observe(this, token -> {
            if (getUserModel() != null) {
                UserModel userModel = getUserModel();
                userModel.getData().setFirebase_token(token);
                setUserModel(userModel);
            }
        });

        binding.imgNotification.setOnClickListener(v -> {
            if (getUserModel() != null) {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });
        if (getUserModel() != null) {
            homeActivityMvvm.updateFirebase(this, getUserModel());
        }
    }

    public void displayFragmentService() {
        navController.navigate(R.id.myService);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) | super.onSupportNavigateUp();
    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }


    @Override
    public void onBackPressed() {
        int currentFragmentId = navController.getCurrentDestination().getId();
        if (currentFragmentId == R.id.home) {
            finish();

        } else {
            navController.popBackStack();
        }

    }

    @Override
    public void onVerificationSuccess() {

    }


    public void updateFirebase() {
        if (getUserModel() != null) {
            homeActivityMvvm.updateFirebase(this, getUserModel());
        }
    }


}
