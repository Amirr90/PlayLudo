package com.example.playludo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.playludo.HomeScreen;
import com.example.playludo.R;
import com.example.playludo.adapter.BidAdapter;
import com.example.playludo.databinding.FragmentBidBinding;
import com.example.playludo.interfaces.AdapterInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.utils.AppUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.playludo.utils.Utils.getFireStoreReference;

public class BidFragment extends Fragment implements AdapterInterface {
    private static final String TAG = "BidFragment";

    public static final String BID_QUERY = "Bids";
    public static final String BID_AMOUNT = "bidAmount";
    public static final String BID_ID = "bidId";
    public static final String GAME_IMAGE = "gameImage";
    String bidValue;
    FragmentBidBinding bidBinding;
    BidAdapter bidAdapter;
    List<BidModel> bidModels;
    NavController navController;

    public BidFragment(String bidValue) {
        this.bidValue = bidValue;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bidBinding = FragmentBidBinding.inflate(getLayoutInflater());
        return bidBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        AppUtils.showRequestDialog(requireActivity());
        bidModels = new ArrayList<>();
        bidAdapter = new BidAdapter(bidModels, this);
        bidBinding.bidRec.setAdapter(bidAdapter);
        bidBinding.bidRec.addItemDecoration(new
                DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL));
        loadBidData();
        AppUtils.hideDialog();
    }

    private void loadBidData() {
        getFireStoreReference().collection(BID_QUERY)
                .whereEqualTo(BID_AMOUNT, bidValue)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (null != e) {
                        Toast.makeText(requireActivity(), "something went wrong, try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null == queryDocumentSnapshots) {
                        Toast.makeText(requireActivity(), "something went wrong, try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<DocumentChange> snapshotList = queryDocumentSnapshots.getDocumentChanges();
                    Log.d(TAG, "loadBidData: " + snapshotList.size());
                    for (DocumentChange snapshot : snapshotList)
                        if (snapshot.getType() == DocumentChange.Type.ADDED) {
                            //Log.d(TAG, "Added: " + snapshot.getDocument().toObject(BidModel.class));
                            BidModel bidModel = snapshot.getDocument().toObject(BidModel.class);
                            bidModel.setBidId(snapshot.getDocument().getId());
                            bidModels.add(bidModel);
                        }

                    AppUtils.hideDialog();
                    bidAdapter.notifyDataSetChanged();
                });

    }

    @Override
    public void onItemClicked(Object obj) {
        BidModel bidModel = (BidModel) obj;
        Log.d(TAG, "onItemClicked: " + bidModel.getBidId());
        DashboardFragmentDirections.ActionDashboardFragmentToBidDetailsFragment action = DashboardFragmentDirections.actionDashboardFragmentToBidDetailsFragment();
        action.setGameId(bidModel.getBidId());
        navController.navigate(action);
    }
}