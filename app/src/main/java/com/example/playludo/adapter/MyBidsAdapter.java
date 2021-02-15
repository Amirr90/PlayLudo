package com.example.playludo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playludo.databinding.MyBidsViewBinding;
import com.example.playludo.models.TransactionModel;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.playludo.fragments.AddCreditsFragment.CREDITS;
import static com.example.playludo.fragments.AddCreditsFragment.INVEST;
import static com.example.playludo.fragments.AddCreditsFragment.TYPE_CREDIT;
import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.fragments.BidDetailsFragment.TRANSACTIONS;
import static com.example.playludo.fragments.BidFragment.BID_AMOUNT;
import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.utils.Bid.BID_STATUS;
import static com.example.playludo.utils.Bid.IS_ACTIVE;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Utils.getUid;

public class MyBidsAdapter extends RecyclerView.Adapter<MyBidsAdapter.BidsVH> {
    List<DocumentSnapshot> snapshotList;
    Activity activity;

    public MyBidsAdapter(List<DocumentSnapshot> snapshotList, Activity activity) {
        this.snapshotList = snapshotList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyBidsAdapter.BidsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MyBidsViewBinding binding = MyBidsViewBinding.inflate(layoutInflater, parent, false);
        return new BidsVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBidsAdapter.BidsVH holder, int position) {
        DocumentSnapshot snapshot = snapshotList.get(position);
        holder.binding.setMyBids(snapshot);
      try {
          if (null != snapshot) {
              holder.binding.tvBidsTimestamp.setText(AppUtils.getTimeAgo(snapshot.getLong(TIMESTAMP)));
              holder.binding.tvGameAmount.setText(AppUtils.getCurrencyFormat(snapshot.getString(BID_AMOUNT)));
              holder.binding.btnCancel.setEnabled(!snapshot.getBoolean(BID_STATUS));
          }
      } catch (Exception e) {
          e.printStackTrace();
      }

        holder.binding.btnCancel.setOnClickListener(v -> {
            if (!snapshot.getBoolean(BID_STATUS)) {
                showCancelBidDialog(snapshot);
            }
        });
    }

    private void showCancelBidDialog(DocumentSnapshot snapshot) {
        new AlertDialog.Builder(activity).setMessage("Do you want to cancel this Bid??")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    cancelBid(snapshot);
                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }

    private void cancelBid(DocumentSnapshot snapshot) {
        AppUtils.showRequestDialog(activity);
        String bidId = snapshot.getId();
        FirebaseFirestore db = Utils.getFireStoreReference();

        // Get a new write batch
        WriteBatch batch = db.batch();


        // update Bid Status
        DocumentReference nycRef = db.collection(BID_QUERY).document(bidId);
        Map<String, Object> map = new HashMap<>();
        map.put(IS_ACTIVE, false);
        batch.update(nycRef, map);

        // Credits user's Wallet
        DocumentReference sfRef = db.collection(USERS_QUERY).document(getUid());
        Map<String, Object> userMap = new HashMap<>();
        map.put(CREDITS, FieldValue.increment(Long.parseLong(snapshot.getString(BID_AMOUNT))));
        map.put(INVEST, FieldValue.increment(-Long.parseLong(snapshot.getString(BID_AMOUNT))));
        batch.update(sfRef, userMap);

        // Create Transaction of User
        DocumentReference transRef = db.collection(TRANSACTIONS).document();
        batch.set(transRef, getTransactionModel(snapshot.getString(BID_AMOUNT)));

        // Commit the batch
        batch.commit().addOnSuccessListener(aVoid -> {
            AppUtils.hideDialog();
            Toast.makeText(activity, "Money refunded to wallet successfully !! ", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(activity, "try again !!", Toast.LENGTH_SHORT).show());
    }

    private TransactionModel getTransactionModel(String bidAmount) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(bidAmount);
        transactionModel.setUid(getUid());
        transactionModel.setTimestamp(System.currentTimeMillis());
        transactionModel.setType(TYPE_CREDIT);
        return transactionModel;
    }

    @Override
    public int getItemCount() {
        return snapshotList.size();
    }

    public class BidsVH extends RecyclerView.ViewHolder {
        MyBidsViewBinding binding;

        public BidsVH(@NonNull MyBidsViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
