package com.apps.weddingprovider.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.weddingprovider.R;
import com.apps.weddingprovider.databinding.AddBasicItemRowBinding;
import com.apps.weddingprovider.databinding.AddImagesRowBinding;
import com.apps.weddingprovider.model.AddBaiscItemModel;
import com.apps.weddingprovider.uis.activity_add_service.AddServiceActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BaiscItemAddServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AddBaiscItemModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AddServiceActivity activity;

    public BaiscItemAddServiceAdapter(List<AddBaiscItemModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AddServiceActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddBasicItemRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.add_basic_item_row,parent,false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));
myHolder.binding.imDelete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        activity.delete(holder.getLayoutPosition());
    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        private AddBasicItemRowBinding binding;
        public MyHolder(@NonNull AddBasicItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
