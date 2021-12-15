package com.apps.weddingprovider.uis.activity_home.fragments_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.FragmentCalenderBinding;
import com.apps.weddingprovider.mvvm.FragmentCalenderMvvm;
import com.apps.weddingprovider.uis.activity_base.BaseFragment;
import com.apps.weddingprovider.uis.activity_home.HomeActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentCalender extends BaseFragment {
    private FragmentCalenderBinding binding;
    private HomeActivity activity;
    private FragmentCalenderMvvm fragmentCalenderMvvm;
    private String service_id = "";
    private List<Calendar> reservedDatesList;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle!=null){
            service_id = bundle.getString("data");

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calender, container, false);
        return binding.getRoot();

    }

    private void initView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        fragmentCalenderMvvm = ViewModelProviders.of(this).get(FragmentCalenderMvvm.class);
        fragmentCalenderMvvm.getIsLoading().observe(activity, isLoading -> {
            if (isLoading){
                binding.progBar.setVisibility(View.VISIBLE);
                binding.llData.setVisibility(View.GONE);
            }else {
                binding.progBar.setVisibility(View.GONE);
                binding.llData.setVisibility(View.VISIBLE);
            }
        });
        fragmentCalenderMvvm.getReservedDatesLiveData().observe(activity, datesList->{

        });


        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            String date = simpleDateFormat.format(new Date(clickedDayCalendar.getTimeInMillis()));
            binding.tvDate.setText(date);

        });
        fragmentCalenderMvvm.getDatesData(getUserModel(), "");
    }


}