package com.apps.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.FragmentProfileBinding;
import com.apps.weddingprovider.mvvm.FragmentProfileMvvm;
import com.apps.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;
import com.apps.weddingprovider.uis.activity_contact_us.ContactUsActivity;
import com.apps.weddingprovider.uis.activity_home.HomeActivity;
import com.apps.weddingprovider.uis.activity_login.LoginActivity;
import com.apps.weddingprovider.uis.activity_notification.NotificationActivity;
import com.apps.weddingprovider.uis.activity_sign_up.SignUpActivity;

import java.util.List;


public class FragmentProfile extends BaseFragment {
    private static final String TAG = FragmentProfile.class.getName();
    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private boolean login;
    private FragmentProfileMvvm fragmentProfileMvvm;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> addservicelauncher;

    private int req = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req == 1 && result.getResultCode() == Activity.RESULT_OK) {
                binding.setModel(getUserModel());
            }
        });
        addservicelauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                String serviceid = intent.getStringExtra("service_id");
                setItemServiceDetails(serviceid);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        Log.e(TAG, "onViewCreated: ");

    }

    private void initView() {
        fragmentProfileMvvm = ViewModelProviders.of(this).get(FragmentProfileMvvm.class);
        if (getUserModel() != null) {
            binding.setModel(getUserModel());
        }
        binding.setLang(getLang());

        fragmentProfileMvvm.logout.observe(activity, aBoolean -> {
            if (aBoolean) {
                logout();
            }
        });
        binding.llContactUs.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ContactUsActivity.class);
            startActivity(intent);

            // Navigation.findNavController(v).navigate(R.id.activity_contact_us);
        });


        binding.llRate.setOnClickListener(v -> rateApp());

        binding.tvName.setOnClickListener(v -> {
            if (getUserModel() == null) {
                navigateToLoginActivity();
            }
        });
        binding.llEditProfile.setOnClickListener(view -> {
                    if (getUserModel() != null) {
                        navigateToSignUpActivity();
                    }
                }
        );
        binding.llAddService.setOnClickListener(view -> {
                    if (getUserModel() != null) {
                        navigateToAddServiceActivity();
                    }
                }
        );
        binding.llLogout.setOnClickListener(view -> {
            if (getUserModel() == null) {
                logout();
            } else {
                fragmentProfileMvvm.logout(activity, getUserModel());
            }

        });
    }

    private void navigateToAddServiceActivity() {
        Intent intent = new Intent(activity, AddServiceActivity.class);
        addservicelauncher.launch(intent);

    }

    private void navigateToLoginActivity() {
        req = 1;
        Intent intent = new Intent(activity, LoginActivity.class);
        launcher.launch(intent);

    }

    private void logout() {
        clearUserModel(activity);
        binding.setModel(null);
        binding.image.setImageResource(R.drawable.circle_avatar);
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        activity.finish();
    }

    private void rateApp() {
        String appId = activity.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        final List<ResolveInfo> otherApps = activity.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rateIntent.setComponent(componentName);
                startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            startActivity(webIntent);
        }

    }


    private void navigateToSignUpActivity() {
        Intent intent = new Intent(activity, SignUpActivity.class);

        launcher.launch(intent);
    }

    public void setItemServiceDetails(String serviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("data", serviceId);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.fragmentServiceDetails, bundle);
    }
}