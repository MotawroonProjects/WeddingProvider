package com.apps.weddingprovider.uis.activity_home.fragments_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
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
    private String selectedDate = "";
    private List<String> datesList = new ArrayList<>();


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
        Calendar currentPageDate = binding.calendarView.getCurrentPageDate();
        String date = simpleDateFormat.format(new Date(currentPageDate.getTimeInMillis()));
        binding.tvDate.setText(date);

        fragmentCalenderMvvm = ViewModelProviders.of(this).get(FragmentCalenderMvvm.class);
        fragmentCalenderMvvm.getIsLoading().observe(activity, isLoading -> {
            binding.swipeRefresh.setRefreshing(isLoading);
        });
        fragmentCalenderMvvm.getDatesLiveData().observe(activity, datesList -> {
            this.datesList.clear();
            this.datesList.addAll(datesList);

        });

        fragmentCalenderMvvm.getReservedDatesLiveData().observe(activity, datesList -> {
            List<EventDay> list = new ArrayList<>();
            for (Calendar calendar : datesList) {
                EventDay eventDay = new EventDay(calendar, R.drawable.circle_primary, ContextCompat.getColor(activity, R.color.colorPrimary));
                list.add(eventDay);
            }
            binding.calendarView.setEvents(list);
        });


        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            selectedDate = simpleDateFormat.format(new Date(clickedDayCalendar.getTimeInMillis()));
            if (isDateValid(selectedDate)){
                Bundle bundle = new Bundle();
                bundle.putString("service_id",service_id);
                bundle.putString("date",selectedDate);
                Navigation.findNavController(binding.calendarView).navigate(R.id.fragmentCalenderReservation,bundle);
            }else {
                Toast.makeText(activity, R.string.no_reserv_on_day, Toast.LENGTH_SHORT).show();
            }

        });
        binding.swipeRefresh.setOnRefreshListener(() -> fragmentCalenderMvvm.getDatesData(getUserModel(), service_id));
        fragmentCalenderMvvm.getDatesData(getUserModel(), service_id);
        binding.calendarView.setOnForwardPageChangeListener(() -> {
            Calendar pageDate = binding.calendarView.getCurrentPageDate();
            String d = simpleDateFormat.format(new Date(pageDate.getTimeInMillis()));
            binding.tvDate.setText(d);

        });

        binding.calendarView.setOnPreviousPageChangeListener(() -> {
            Calendar pageDate = binding.calendarView.getCurrentPageDate();
            String d = simpleDateFormat.format(new Date(pageDate.getTimeInMillis()));
            binding.tvDate.setText(d);
        });


    }

    private boolean isDateValid(String date){
        return datesList.contains(date);
    }


}