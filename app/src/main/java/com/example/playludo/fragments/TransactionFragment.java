package com.example.playludo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playludo.databinding.FragmentTransactionBinding;
import com.example.playludo.databinding.TransactionViewBinding;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.fragments.BidDetailsFragment.TRANSACTIONS;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Bid.UID;
import static com.example.playludo.utils.Utils.getUid;
import static com.example.playludo.utils.WithdrawAmount.TYPE_WITHDRAW;


public class TransactionFragment extends Fragment {
    private static final String TAG = "TransactionFragment";

    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    FragmentTransactionBinding transactionBinding;
    TransactionAdapter adapter;
    List<DocumentSnapshot> snapshotList;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        transactionBinding = FragmentTransactionBinding.inflate(getLayoutInflater());
        return transactionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        snapshotList = new ArrayList<>();
        adapter = new TransactionAdapter(snapshotList);
        transactionBinding.recTransaction.setAdapter(adapter);
        transactionBinding.recTransaction.addItemDecoration(new
                DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL));

        loadData();
    }

    private void loadData() {
        AppUtils.showRequestDialog(requireActivity());
        Utils.getFireStoreReference().collection(TRANSACTIONS).whereEqualTo(UID, getUid())
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AppUtils.hideDialog();
                    if (null == queryDocumentSnapshots)
                        return;

                    snapshotList.clear();
                    snapshotList.addAll(queryDocumentSnapshots.getDocuments());
                    adapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Log.d(TAG, "loadData: " + e.getLocalizedMessage());
            Toast.makeText(requireActivity(), "try again", Toast.LENGTH_SHORT).show();
        });
    }


    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionVH> {
        List<DocumentSnapshot> snapshots;

        public TransactionAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }


        @NonNull
        @Override
        public TransactionAdapter.TransactionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            TransactionViewBinding binding = TransactionViewBinding.inflate(inflater, parent, false);
            return new TransactionVH(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionAdapter.TransactionVH holder, int position) {
            DocumentSnapshot snapshot = snapshots.get(position);
            holder.binding.setTransaction(snapshot);
            String prefix = null;
            if (snapshot.getString(TYPE).equals("credit"))
                prefix = "+";
            else prefix = "-";
            holder.binding.tvTAmount.setText(prefix + AppUtils.getCurrencyFormat(snapshot.getString(AMOUNT)));
        }

        @Override
        public int getItemCount() {
            return snapshots.size();
        }

        public class TransactionVH extends RecyclerView.ViewHolder {
            TransactionViewBinding binding;

            public TransactionVH(@NonNull TransactionViewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
