
package com.e_co.weddingprovider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ServiceAppointmentRowBinding;
import com.e_co.weddingprovider.databinding.ServiceRowBinding;
import com.e_co.weddingprovider.model.ServiceModel;
import com.e_co.weddingprovider.uis.activity_home.fragments_app.FragmentService;
import com.e_co.weddingprovider.uis.activity_home.fragments_home_navigaion.FragmentMyService;

import java.util.List;

public class ServiceAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ServiceModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;


    public ServiceAppointmentAdapter(Context context, Fragment fragment) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        ServiceAppointmentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.service_appointment_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.btnAppointment.setOnClickListener(v -> {
            ServiceModel serviceModel = list.get(holder.getAdapterPosition());
            if (fragment instanceof FragmentService) {
                FragmentService fragmentService = (FragmentService) fragment;
                fragmentService.setItemServiceDetails(serviceModel.getId());

            }
        });


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ServiceAppointmentRowBinding binding;

        public MyHolder(ServiceAppointmentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void updateList(List<ServiceModel> list) {
        if (list == null) {
            if (this.list != null) {
                this.list.clear();

            }

        } else {
            this.list = list;

        }
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        if (list != null && list.size() > 0) {
            list.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}
