
package com.e_co.weddingprovider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.OfferRowBinding;
import com.e_co.weddingprovider.model.ServiceModel;
import com.e_co.weddingprovider.uis.activity_home.fragments_app.ServiceDetailsFragment;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ServiceModel.OfferModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;


    public OfferAdapter(Context context, Fragment fragment) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {

        });

        myHolder.binding.btnUpdate.setOnClickListener(v -> {
            if (fragment instanceof ServiceDetailsFragment) {
                ServiceDetailsFragment serviceDetailsFragment = (ServiceDetailsFragment) fragment;
                serviceDetailsFragment.updateOffer(list.get(myHolder.getAdapterPosition()));
            }
        });

        myHolder.binding.flDelete.setOnClickListener(v -> {
            ServiceModel.OfferModel offer = list.get(holder.getAdapterPosition());

            if (fragment instanceof ServiceDetailsFragment) {
                ServiceDetailsFragment serviceDetailsFragment = (ServiceDetailsFragment) fragment;
                serviceDetailsFragment.deleteOffer(offer.getId(),holder.getAdapterPosition());
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
        public OfferRowBinding binding;

        public MyHolder(OfferRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void updateList(List<ServiceModel.OfferModel> list) {
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
