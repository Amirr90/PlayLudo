package com.example.playludo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playludo.databinding.BidListViewBinding;
import com.example.playludo.interfaces.AdapterInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.fragments.BidFragment.BID_AMOUNT;
import static com.example.playludo.utils.Bid.BID_STATUS;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidVH> {
    List<BidModel> bidModels;
    AdapterInterface adapterInterface;

    public BidAdapter(List<BidModel> bidModels, AdapterInterface adapterInterface) {
        this.bidModels = bidModels;
        this.adapterInterface = adapterInterface;
    }


    @NonNull
    @Override
    public BidVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BidListViewBinding binding = BidListViewBinding.inflate(layoutInflater, parent, false);
        binding.setAdapterInterface(adapterInterface);
        return new BidVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BidVH holder, int position) {
        BidModel bidModel = bidModels.get(position);
        holder.binding.setBidModel(bidModel);
        holder.binding.tvBidsTimestamp.setText(AppUtils.getTimeAgo(bidModel.getTimestamp()));
        holder.binding.tvGameAmount.setText(AppUtils.getCurrencyFormat(bidModel.getBidAmount()));
        holder.binding.btnCancel.setEnabled(!bidModel.isBidStatus());


    }

    @Override
    public int getItemCount() {
        if (bidModels == null)
            bidModels = new ArrayList<>();

        return bidModels.size();
    }

    public class BidVH extends RecyclerView.ViewHolder {
        BidListViewBinding binding;

        public BidVH(@NonNull BidListViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
