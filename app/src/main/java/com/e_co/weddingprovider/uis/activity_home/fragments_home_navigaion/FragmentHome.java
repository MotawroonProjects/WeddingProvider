package com.e_co.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.Navigation;

import com.e_co.weddingprovider.R;

import com.e_co.weddingprovider.adapter.MyPagerAdapter;
import com.e_co.weddingprovider.databinding.FragmentHomeBinding;

import com.e_co.weddingprovider.uis.activity_base.BaseFragment;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class FragmentHome extends BaseFragment {
    private static final String TAG = FragmentHome.class.getName();
    private HomeActivity activity;
    private FragmentHomeBinding binding;
    private List<String> titles;
    private List<Fragment> fragments;
    private MyPagerAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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
        titles.add(getString(R.string.current));
        titles.add(getString(R.string.prev));

        fragments.add(FragmentCurrentReservation.newInstance());
        fragments.add(FragmentPreviousReservation.newInstance());

        adapter = new MyPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments, titles);
        binding.pager.setAdapter(adapter);
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setOffscreenPageLimit(fragments.size());
        binding.flCalender.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.fragmentService);

        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
    }


}