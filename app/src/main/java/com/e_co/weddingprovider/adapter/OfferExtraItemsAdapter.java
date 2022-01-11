
package com.e_co.weddingprovider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ExtraItemRowBinding;
import com.e_co.weddingprovider.databinding.ServiceRowBinding;
import com.e_co.weddingprovider.model.ResevisionModel;

import java.util.List;

public class OfferExtraItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ResevisionModel.ResevisionExtraItems> list;
    private Context context;
    private LayoutInflater inflater;


    public OfferExtraItemsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        ExtraItemRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.extra_item_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));


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
        public ExtraItemRowBinding binding;

        public MyHolder(ExtraItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void updateList(List<ResevisionModel.ResevisionExtraItems> list) {
        if (list == null) {
            if (this.list != null) {
                this.list.clear();

            }

        } else {
            this.list = list;

        }
        notifyDataSetChanged();
    }

}
