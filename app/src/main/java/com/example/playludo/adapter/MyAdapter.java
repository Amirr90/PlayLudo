package com.example.playludo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playludo.HomeScreen;
import com.example.playludo.R;
import com.example.playludo.databinding.MyBidHeaderViewBinding;
import com.example.playludo.fragments.MyBidsListFragment;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PriceVH> {
    List<String> bidModels;
    public static int selectedPosition = 0;

    public MyAdapter(List<String> bidModels) {
        this.bidModels = bidModels;
    }

    @NonNull
    @Override
    public PriceVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MyBidHeaderViewBinding binding = MyBidHeaderViewBinding.inflate(layoutInflater, parent, false);
        return new PriceVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceVH holder, int position) {
        String bidModel = bidModels.get(position);
        holder.binding.setBidModel(bidModel);
        holder.binding.textView5.setOnClickListener((View v) -> {
            selectedPosition = position;
            MyBidsListFragment.getInstance().setMyBidRecData(position);
            notifyDataSetChanged();
        });

        if (selectedPosition == position) {
            setTextColor(holder, HomeScreen.getInstance().getResources().getColor(R.color.white),
                    HomeScreen.getInstance().getResources().getColor(R.color.white));
            holder.binding.getRoot().setBackground(HomeScreen.getInstance().getResources().getDrawable(R.drawable.rectangle_outline_new_ui_color_yellow));
        } else {
            setTextColor(holder, HomeScreen.getInstance().getResources().getColor(R.color.colorPrimary),
                    HomeScreen.getInstance().getResources().getColor(R.color.TextGrayColo));
            holder.binding.getRoot().setBackground(HomeScreen.getInstance().getResources().getDrawable(R.drawable.rectangle_outline_new_ui_color));
        }

    }

    private void setTextColor(PriceVH holder, int color, int color2) {
        holder.binding.textView5.setTextColor(color2);
        holder.binding.textView5.setTextColor(color);
        holder.binding.textView5.setTextColor(color2);
    }

    @Override
    public int getItemCount() {
        return bidModels.size();
    }

    public class PriceVH extends RecyclerView.ViewHolder {
        MyBidHeaderViewBinding binding;

        public PriceVH(@NonNull MyBidHeaderViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}