package com.e_co.weddingprovider.uis.activity_home.fragments_home_navigaion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;


import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.adapter.ServiceAdapter;
import com.e_co.weddingprovider.databinding.FragmentMyServicesBinding;
import com.e_co.weddingprovider.mvvm.FragmentServiceMvvm;
import com.e_co.weddingprovider.mvvm.HomeActivityMvvm;
import com.e_co.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.e_co.weddingprovider.uis.activity_base.BaseFragment;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;

public class FragmentMyService extends BaseFragment {
    private FragmentMyServicesBinding binding;
    private HomeActivity activity;
    private FragmentServiceMvvm fragmentServiceMvvm;
    private ServiceAdapter adapter;
    private HomeActivityMvvm generalMvvm;
    private ActivityResultLauncher<Intent> launcher;
    private int req;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
        launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req==1&&result.getResultCode()== Activity.RESULT_OK){
                fragmentServiceMvvm.getServiceData(getUserModel());

            }
        });

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
        generalMvvm = ViewModelProviders.of(activity).get(HomeActivityMvvm.class);

        fragmentServiceMvvm = ViewModelProviders.of(activity).get(FragmentServiceMvvm.class);
        fragmentServiceMvvm.onDeleteSuccess().observe(activity, integer -> {
            if (fragmentServiceMvvm.getServiceLiveData().getValue().size() > 0) {
                fragmentServiceMvvm.getServiceLiveData().getValue().remove(integer);

                if (adapter != null) {
                    adapter.removeItem(integer);
                }
                if (fragmentServiceMvvm.getServiceLiveData().getValue().size() == 0) {
                    binding.llNoData.setVisibility(View.VISIBLE);
                } else {
                    binding.llNoData.setVisibility(View.GONE);

                }
            }
        });
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
        binding.flAddService.setOnClickListener(v -> {
            navigateToActivityAddService();
        });
        binding.fab.setOnClickListener(v -> {
            navigateToActivityAddService();


        });

        fragmentServiceMvvm.getServiceData(getUserModel());

        binding.swipeRefresh.setOnRefreshListener(() -> fragmentServiceMvvm.getServiceData(getUserModel()));



    }

    private void navigateToActivityAddService() {
        req =1;
        Intent intent = new Intent(activity, AddServiceActivity.class);
        launcher.launch(intent);

    }

    public void setItemServiceDetails(String serviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("data", serviceId);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.fragmentServiceDetails, bundle);
    }

    public void deleteService(String id, int adapterPosition) {
        fragmentServiceMvvm.deleteServiceData(getUserModel(), id, adapterPosition, activity);
    }
}