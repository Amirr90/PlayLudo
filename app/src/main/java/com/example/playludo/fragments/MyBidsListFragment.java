package com.example.playludo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.HomeScreen;
import com.example.playludo.R;
import com.example.playludo.adapter.MyAdapter;
import com.example.playludo.adapter.MyBidsAdapter;
import com.example.playludo.adapter.PriceAdapter;
import com.example.playludo.databinding.BidPriceViewBinding;
import com.example.playludo.databinding.FragmentMyBidsListBinding;
import com.example.playludo.interfaces.AdapterInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.utils.Bid.IS_ACTIVE;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Utils.getUid;


public class MyBidsListFragment extends Fragment implements AdapterInterface {
    private static final String TAG = "MyBidsListFragment";

    MyBidsAdapter bidsAdapter;
    FragmentMyBidsListBinding myBidsListBinding;
    List<DocumentSnapshot> snapshots;
    public static MyBidsListFragment instance;
    NavController navController;
    int selectedPosition = 0;

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

        navController = Navigation.findNavController(view);

        List<String> list = new ArrayList<>();
        list.add("Created");
        list.add("Accepted");
        myBidsListBinding.priceTab.setAdapter(new MyAdapter(list));


        snapshots = new ArrayList<>();
        bidsAdapter = new MyBidsAdapter(snapshots, requireActivity(), this);
        myBidsListBinding.recMyBids.setAdapter(bidsAdapter);
        myBidsListBinding.recMyBids.addItemDecoration(new
                DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL));
        loadBidsListData(selectedPosition);
    }

    public void loadBidsListData(int pos) {
        AppUtils.showRequestDialog(requireActivity());
        if (pos == 0) {
            Utils.getFireStoreReference().collection(BID_QUERY)
                    .whereEqualTo("uid", getUid())
                    //.whereEqualTo(IS_ACTIVE, true)
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
        } else if (pos == 1) {
            Utils.getFireStoreReference().collection(BID_QUERY)
                    .whereEqualTo("bidAcceptBy", getUid())
                    //.whereEqualTo(IS_ACTIVE, true)
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

    @Override
    public void onItemClicked(Object obj) {
        String bidId = (String) obj;
        MyBidsListFragmentDirections.ActionMyBidsListFragmentToBidDetailsFragment action = MyBidsListFragmentDirections.actionMyBidsListFragmentToBidDetailsFragment();
        action.setGameId(bidId);
        navController.navigate(action);
    }


    public void setMyBidRecData(int position) {
        selectedPosition = position;
        loadBidsListData(selectedPosition);
    }
}