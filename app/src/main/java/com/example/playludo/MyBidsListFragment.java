package com.example.playludo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.adapter.MyBidsAdapter;
import com.example.playludo.databinding.FragmentMyBidsListBinding;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.utils.Bid.IS_ACTIVE;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Utils.getUid;


public class MyBidsListFragment extends Fragment {
    private static final String TAG = "MyBidsListFragment";

    MyBidsAdapter bidsAdapter;
    FragmentMyBidsListBinding myBidsListBinding;
    List<DocumentSnapshot> snapshots;
    public static MyBidsListFragment instance;

    public static MyBidsListFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myBidsListBinding = FragmentMyBidsListBinding.inflate(getLayoutInflater());
        instance = this;
        return myBidsListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        snapshots = new ArrayList<>();
        bidsAdapter = new MyBidsAdapter(snapshots, requireActivity());
        myBidsListBinding.recMyBids.setAdapter(bidsAdapter);
        myBidsListBinding.recMyBids.addItemDecoration(new
                DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL));
        loadBidsListData();
    }

    public void loadBidsListData() {
        AppUtils.showRequestDialog(requireActivity());
        Utils.getFireStoreReference().collection(BID_QUERY)
                .whereEqualTo("uid", getUid())
                .whereEqualTo(IS_ACTIVE, true)
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AppUtils.hideDialog();
                    if (null != queryDocumentSnapshots) {
                        snapshots.clear();
                        snapshots.addAll(queryDocumentSnapshots.getDocuments());
                        bidsAdapter.notifyDataSetChanged();
                    } else if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(requireActivity(), "No Active bids found !!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(requireActivity(), "No data found !!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Log.d(TAG, "loadBidsListData: " + e.getLocalizedMessage());
            Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show();
        });
    }
}