package com.apps.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;


import com.apps.weddingprovider.R;
import com.apps.weddingprovider.adapter.MyPagerAdapter;
import com.apps.weddingprovider.adapter.ServiceAdapter;
import com.apps.weddingprovider.databinding.FragmentMyServicesBinding;
import com.apps.weddingprovider.model.ServiceModel;
import com.apps.weddingprovider.mvvm.FragmentServiceMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;
import com.apps.weddingprovider.uis.activity_home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyService extends BaseFragment {
    private FragmentMyServicesBinding binding;
    private HomeActivity activity;
    private FragmentServiceMvvm fragmentServiceMvvm;
    private ServiceAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_services, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        fragmentServiceMvvm = ViewModelProviders.of(activity).get(FragmentServiceMvvm.class);
        adapter = new ServiceAdapter(activity, this);
        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.recView.setAdapter(adapter);

        fragmentServiceMvvm.getIsLoading().observe(activity, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });

        fragmentServiceMvvm.getServiceLiveData().observe(activity, serviceModelList -> {
            if (serviceModelList.size() > 0) {
                adapter.updateList(serviceModelList);
                adapter.notifyDataSetChanged();
                binding.llNoData.setVisibility(View.GONE);
            } else {
                binding.llNoData.setVisibility(View.VISIBLE);

            }
        });


        fragmentServiceMvvm.getServiceData(getUserModel());

        binding.swipeRefresh.setOnRefreshListener(() -> fragmentServiceMvvm.getServiceData(getUserModel()));
    }

    public void setItemServiceDetails(String serviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("data",serviceId);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.fragmentServiceDetails,bundle);
    }
}