package com.e_co.weddingprovider.uis.activity_home.fragments_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.e_co.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.e_co.weddingprovider.uis.activity_base.BaseFragment;
import com.e_co.weddingprovider.uis.activity_home.HomeActivity;

public class FragmentService extends BaseFragment {
    private FragmentMyServicesBinding binding;
    private HomeActivity activity;
    private FragmentServiceMvvm fragmentServiceMvvm;
    private ServiceAdapter adapter;
    private ActivityResultLauncher<Intent> launcher;
    private int req;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (req == 1 && result.getResultCode() == Activity.RESULT_OK) {
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
        fragmentServiceMvvm = ViewModelProviders.of(activity).get(FragmentServiceMvvm.class);
        adapter = new ServiceAdapter(activity, this);
        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.recView.setAdapter(adapter);

        fragmentServiceMvvm.getIsLoading().observe(activity, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });
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

        fragmentServiceMvvm.getServiceLiveData().observe(activity, serviceModelList -> {
            if (serviceModelList.size() > 0) {
                adapter.updateList(serviceModelList);
                binding.llNoData.setVisibility(View.GONE);

            } else {
                binding.llNoData.setVisibility(View.VISIBLE);

            }
        });

        binding.fab.setOnClickListener(v -> {

            navigateToActivityAddService();
        });
        binding.flAddService.setOnClickListener(v -> {
            navigateToActivityAddService();
        });
        fragmentServiceMvvm.getServiceData(getUserModel());

        binding.swipeRefresh.setOnRefreshListener(() -> fragmentServiceMvvm.getServiceData(getUserModel()));


    }

    private void navigateToActivityAddService() {
        req = 1;
        Intent intent = new Intent(activity, AddServiceActivity.class);
        launcher.launch(intent);

    }

    public void setItemServiceDetails(String serviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("data", serviceId);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.fragmentCalender, bundle);
    }

    public void deleteService(String id, int adapterPosition) {
        fragmentServiceMvvm.deleteServiceData(getUserModel(), id, adapterPosition, activity);
    }
}