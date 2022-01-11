package com.e_co.weddingprovider.uis.activity_home.fragments_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.adapter.OfferExtraItemsAdapter;
import com.e_co.weddingprovider.adapter.ReservionAdapter;
import com.e_co.weddingprovider.databinding.BottomSheetServiceDetailsBinding;
import com.e_co.weddingprovider.databinding.FragmentCalenderReservationBinding;
import com.e_co.weddingprovider.databinding.FragmentCurrentReservationBinding;
import com.e_co.weddingprovider.model.ResevisionModel;
import com.e_co.weddingprovider.mvvm.FragmentCalenderReservisonMvvm;
import com.e_co.weddingprovider.uis.activity_base.BaseFragment;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class FragmentCalenderReservation extends BaseFragment {
    private FragmentCalenderReservationBinding binding;
    private HomeActivity activity;
    private ReservionAdapter reservionAdapter;
    private FragmentCalenderReservisonMvvm fragmentCalenderReservisonMvvm;
    private String service_id = "";
    private String selectedDate = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            service_id = bundle.getString("service_id");
            selectedDate = bundle.getString("date");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calender_reservation, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        fragmentCalenderReservisonMvvm = ViewModelProviders.of(this).get(FragmentCalenderReservisonMvvm.class);
        fragmentCalenderReservisonMvvm.getIsLoading().observe(activity, isLoading -> {
            if (isLoading) {
                binding.cardNoData.setVisibility(View.GONE);


            }
            binding.swipeRefresh.setRefreshing(isLoading);
        });


        fragmentCalenderReservisonMvvm.getReservation().observe(activity, list -> {
            if (list.size() > 0) {
                reservionAdapter.updateList(fragmentCalenderReservisonMvvm.getReservation().getValue());
                binding.cardNoData.setVisibility(View.GONE);

            } else {
                reservionAdapter.updateList(null);

                binding.cardNoData.setVisibility(View.VISIBLE);

            }

        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            fragmentCalenderReservisonMvvm.getReservionData(getUserModel(), service_id, selectedDate);
        });

        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        reservionAdapter = new ReservionAdapter(activity, this);
        binding.recView.setAdapter(reservionAdapter);
        fragmentCalenderReservisonMvvm.getReservionData(getUserModel(), service_id, selectedDate);
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


        binding.imageClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        binding.btnDelete.setOnClickListener(v -> {
            delete(model);
            dialog.dismiss();
        });
        dialog.show();
    }


    public void delete(ResevisionModel model) {
        fragmentCalenderReservisonMvvm.deleteReservation(activity, model, getUserModel(), service_id, selectedDate);
    }
}