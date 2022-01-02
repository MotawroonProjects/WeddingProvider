package com.apps.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.adapter.OfferExtraItemsAdapter;
import com.apps.weddingprovider.adapter.PreviousReservionAdapter;
import com.apps.weddingprovider.databinding.BottomSheetServiceDetailsBinding;
import com.apps.weddingprovider.databinding.FragmentCurrentReservationBinding;
import com.apps.weddingprovider.model.NotModel;
import com.apps.weddingprovider.model.ResevisionModel;
import com.apps.weddingprovider.mvvm.FragmentPreviousReservisonMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;
import com.apps.weddingprovider.uis.activity_home.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FragmentPreviousReservation extends BaseFragment {
    private FragmentCurrentReservationBinding binding;
    private FragmentPreviousReservisonMvvm fragmentCurrentReservisonMvvm;
    private PreviousReservionAdapter previousReservionAdapter;
    private HomeActivity activity;

    public static FragmentPreviousReservation newInstance() {
        FragmentPreviousReservation fragment = new FragmentPreviousReservation();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_reservation, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        fragmentCurrentReservisonMvvm = ViewModelProviders.of(this).get(FragmentPreviousReservisonMvvm.class);
        fragmentCurrentReservisonMvvm.getIsLoading().observe(activity, isLoading -> {
            if (isLoading) {
                binding.cardNoData.setVisibility(View.GONE);


            }
            binding.swipeRefresh.setRefreshing(isLoading);
        });

        fragmentCurrentReservisonMvvm.getReservionList().observe(activity, weddingHallModels -> {
            if (weddingHallModels.size() > 0) {
                previousReservionAdapter.updateList(fragmentCurrentReservisonMvvm.getReservionList().getValue());
                binding.cardNoData.setVisibility(View.GONE);

            } else {
                previousReservionAdapter.updateList(null);

                binding.cardNoData.setVisibility(View.VISIBLE);

            }

        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            fragmentCurrentReservisonMvvm.getReservionData(getUserModel());
        });


        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        getData();
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        previousReservionAdapter = new PreviousReservionAdapter(activity, this);
        binding.recView.setAdapter(previousReservionAdapter);
        fragmentCurrentReservisonMvvm.getReservionData(getUserModel());
        EventBus.getDefault().register(activity);

    }


    private void getData() {
        binding.cardNoData.setVisibility(View.VISIBLE);
    }

    public void createSheetDialog(ResevisionModel model) {
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        BottomSheetServiceDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.bottom_sheet_service_details, null, false);
        dialog.setContentView(binding.getRoot());
        binding.setModel(model);
        double total = model.getPrice() + model.getExtra_item_price();
        binding.setTotal(String.valueOf(total));
        StringBuilder details;
        details = new StringBuilder(model.getService().getText());

        binding.setDetails(details.toString());
        if (model.getReservation_extra_items() != null) {
            binding.recView.setLayoutManager(new GridLayoutManager(activity, 2, LinearLayoutManager.HORIZONTAL, false));
            OfferExtraItemsAdapter adapter = new OfferExtraItemsAdapter(activity);
            binding.recView.setAdapter(adapter);
            adapter.updateList(model.getReservation_extra_items());
        }

        binding.btnDelete.setVisibility(View.GONE);

        binding.imageClose.setOnClickListener(v -> {
            dialog.dismiss();
        });


        dialog.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewNotificationListener(NotModel model){
        fragmentCurrentReservisonMvvm.getReservionData(getUserModel());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

}