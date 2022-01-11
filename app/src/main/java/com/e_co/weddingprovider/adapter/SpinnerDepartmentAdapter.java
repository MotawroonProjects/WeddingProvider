package com.e_co.weddingprovider.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;


import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.SpinnerDepartmentRowBinding;
import com.e_co.weddingprovider.model.DepartmentModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SpinnerDepartmentAdapter extends BaseAdapter {
    private List<DepartmentModel> dataList;
    private Context context;
    private String lang;
    public SpinnerDepartmentAdapter(List<DepartmentModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerDepartmentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_department_row,viewGroup,false);
        binding.setLang(lang);
        binding.setModel(dataList.get(i));
        return binding.getRoot();
    }
}
