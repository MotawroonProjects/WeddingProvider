package com.apps.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.FragmentMyServicesBinding;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyService extends BaseFragment {
    private FragmentMyServicesBinding binding;
    private List<String> titles;
    private List<Fragment> fragments;

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
        titles = new ArrayList<>();
        fragments = new ArrayList<>();


    }
}