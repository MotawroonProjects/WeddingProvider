
package com.apps.weddingprovider.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.ServiceRowBinding;
import com.apps.weddingprovider.model.ServiceModel;
import com.apps.weddingprovider.uis.activity_home.fragments_app.FragmentService;
import com.apps.weddingprovider.uis.activity_home.fragments_home_navigaion.FragmentMyService;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ServiceModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;


    public ServiceAdapter(Context context, Fragment fragment) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        ServiceRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.service_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {
            ServiceModel serviceModel = list.get(holder.getAdapterPosition());
            if (fragment instanceof FragmentMyService) {
                FragmentMyService fragmentMyService = (FragmentMyService) fragment;
                fragmentMyService.setItemServiceDetails(serviceModel.getId());

            }else  if (fragment instanceof FragmentService) {
                FragmentService fragmentService = (FragmentService) fragment;
                fragmentService.setItemServiceDetails(serviceModel.getId());

            }
        });

        myHolder.binding.flDelete.setOnClickListener(v -> {
            ServiceModel serviceModel = list.get(holder.getAdapterPosition());
            if (fragment instanceof FragmentMyService) {
                FragmentMyService fragmentMyService = (FragmentMyService) fragment;
                fragmentMyService.deleteService(serviceModel.getId(),holder.getAdapterPosition());

            }else  if (fragment instanceof FragmentService) {
                FragmentService fragmentService = (FragmentService) fragment;
                fragmentService.deleteService(serviceModel.getId(),holder.getAdapterPosition());

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
        public ServiceRowBinding binding;

        public MyHolder(ServiceRowBinding binding) {
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
        if (list!=null&&list.size()>0){
            list.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}
