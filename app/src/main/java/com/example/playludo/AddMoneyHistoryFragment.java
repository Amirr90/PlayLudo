package com.example.playludo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.playludo.databinding.AddCreditsHistoryViewBinding;
import com.example.playludo.databinding.FragmentAddMoneyHistoryBinding;
import com.example.playludo.utils.AppConstant;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.fragments.BidFragment.BID_AMOUNT;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Bid.UID;
import static com.example.playludo.utils.Utils.getUid;


public class AddMoneyHistoryFragment extends Fragment {
    private static final String TAG = "AddMoneyHistoryFragment";


    FragmentAddMoneyHistoryBinding historyBinding;
    NavController navController;
    AddMoneyHistoryAdapter adapter;
    List<DocumentSnapshot> snapshots;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        historyBinding = FragmentAddMoneyHistoryBinding.inflate(getLayoutInflater());
        return historyBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


        snapshots = new ArrayList<>();
        adapter = new AddMoneyHistoryAdapter(snapshots);
        historyBinding.recViewHistory.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        Utils.getFireStoreReference().collection(AppConstant.ADD_MONEY_REQUEST)
                .whereEqualTo(UID, getUid())
                .orderBy(AppConstant.TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        snapshots.addAll(queryDocumentSnapshots.getDocuments());
                        adapter.notifyDataSetChanged();
                    } else Log.d(TAG, "onEvent: " + e.getLocalizedMessage());
                });
    }

    private static class AddMoneyHistoryAdapter extends RecyclerView.Adapter<AddMoneyHistoryAdapter.AddMoneyVH> {
        List<DocumentSnapshot> snapshots;

        public AddMoneyHistoryAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public AddMoneyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            AddCreditsHistoryViewBinding binding = AddCreditsHistoryViewBinding.inflate(layoutInflater, parent, false);
            return new AddMoneyVH(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull AddMoneyVH holder, int position) {
            DocumentSnapshot snapshot = snapshots.get(position);
            holder.binding.setAddMoney(snapshot);
            holder.binding.textView.setText(AppUtils.getTimeAgo(snapshot.getLong(TIMESTAMP)));
            holder.binding.textView27.setText(AppUtils.getCurrencyFormat(snapshot.getString(AppConstant.AMOUNT)));
        }

        @Override
        public int getItemCount() {
            return null == snapshots ? 0 : snapshots.size();
        }

        public static class AddMoneyVH extends RecyclerView.ViewHolder {
            AddCreditsHistoryViewBinding binding;

            public AddMoneyVH(@NonNull AddCreditsHistoryViewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}