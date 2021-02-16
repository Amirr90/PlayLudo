package com.example.playludo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.playludo.HomeScreen;
import com.example.playludo.R;
import com.example.playludo.adapter.BidAdapter;
import com.example.playludo.adapter.PriceAdapter;
import com.example.playludo.databinding.FragmentDashboardBinding;
import com.example.playludo.databinding.SubmitBidDialogViewBinding;
import com.example.playludo.interfaces.AdapterInterface;
import com.example.playludo.interfaces.BidInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.models.User;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Bid;
import com.example.playludo.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.adapter.PriceAdapter.selectedPosition;
import static com.example.playludo.fragments.BidFragment.BID_AMOUNT;
import static com.example.playludo.fragments.BidFragment.BID_ID;
import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.models.AppConstant.GAME_TYPE;
import static com.example.playludo.models.AppConstant.LUDO_KING;
import static com.example.playludo.models.AppConstant.POLL_8_BALL;
import static com.example.playludo.models.AppConstant.SIMPLE_JAKARTHA;
import static com.example.playludo.utils.AppUtils.getFormatedAmount;
import static com.example.playludo.utils.Bid.GAME_NAME;
import static com.example.playludo.utils.Bid.IS_ACTIVE;
import static com.example.playludo.utils.Bid.TIMESTAMP;
import static com.example.playludo.utils.Utils.getFireStoreReference;
import static com.example.playludo.utils.Utils.getUid;


public class DashboardFragment extends Fragment implements BidInterface, AdapterInterface {
    private static final String TAG = "DashboardFragment";


    public static DashboardFragment instance;

    public static DashboardFragment getInstance() {
        return instance;
    }

    FragmentDashboardBinding dashboardBinding;
    NavController navController;
    AlertDialog optionDialog;
    BidAdapter bidAdapter;
    List<BidModel> bidModels = new ArrayList<>();
    String gameType = null;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dashboardBinding = FragmentDashboardBinding.inflate(getLayoutInflater());
        instance = this;
        return dashboardBinding.getRoot();
    }

    private void setDashboardRecData() {
        dashboardBinding.priceTab.setAdapter(new PriceAdapter(Utils.getBidPriceList()));

        bidAdapter = new BidAdapter(bidModels, this);
        dashboardBinding.bidRec.setAdapter(bidAdapter);
        dashboardBinding.bidRec.addItemDecoration(new
                DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL));
        setBidRecData(selectedPosition);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        if (null == getArguments())
            return;
        gameType = getArguments().getString(GAME_TYPE);
        updateUserData();

        setDashboardRecData();
        dashboardBinding.tvFundAmount.setOnClickListener(v -> addBidData());
        dashboardBinding.btnAddMoreCredits.setOnClickListener(v -> navController.navigate(R.id.action_dashboardFragment_to_addCreditsFragment));
        dashboardBinding.btnAddBid.setOnClickListener(v -> {
            openSubmitBidDialog();
        });
    }

    private void openSubmitBidDialog() {

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.submit_bid_dialog_view, null, false);

        final SubmitBidDialogViewBinding genderViewBinding = SubmitBidDialogViewBinding.bind(formElementsView);

        genderViewBinding.textView46.setHint(gameType);
        genderViewBinding.etGameId.setHint(getIdHintText(gameType));
        genderViewBinding.etGameId.setVisibility(View.VISIBLE);

        genderViewBinding.btnOk.setOnClickListener(v -> {
            String amount = genderViewBinding.etAmount.getText().toString().trim();
            String gameId = genderViewBinding.etGameId.getText().toString();
            if (TextUtils.isEmpty(gameId)) {
                genderViewBinding.etGameId.setError("game Id required !!");
            } else if (TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount)) {

                if (Long.parseLong(amount) < 50) {
                    genderViewBinding.etAmount.setError("minimum bid amount is 50");
                    return;
                }
                optionDialog.dismiss();
                AppUtils.showRequestDialog(requireActivity(), false);
                Bid bid = new Bid(requireActivity());
                bid.setAmount(amount);
                bid.setUid(getUid());
                bid.start(this, gameId, gameType);
            }
        });

        genderViewBinding.btnCancel.setOnClickListener(v -> optionDialog.dismiss());

        // the alert dialog
        optionDialog = new AlertDialog.Builder(requireActivity()).create();
        optionDialog.setView(formElementsView);
        optionDialog.show();


    }

    private String getIdHintText(String gameType) {
        if (gameType.equalsIgnoreCase(LUDO_KING))
            return "Enter Game ID";
        else if (gameType.equalsIgnoreCase(SIMPLE_JAKARTHA))
            return "Enter 8 Ball Pool game id here";
        else return "";

    }


    private void updateUserData() {
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null && null != documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (null != user) {
                        dashboardBinding.setUser(user);
                        dashboardBinding.tvFundAmount.setText(AppUtils.getCurrencyFormat(user.getCredits()));
                        dashboardBinding.tvInvested.setText(AppUtils.getCurrencyFormat(user.getInvest()));
                        dashboardBinding.tvEarned.setText(AppUtils.getCurrencyFormat(user.getEarn()));
                    } else
                        Toast.makeText(requireActivity(), "User not found !!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(requireActivity(), "something went wrong !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBidData() {

        Utils.getFireStoreReference().collection(BID_QUERY).add(getBidModel()).addOnSuccessListener(documentReference -> Toast.makeText(requireActivity(), "Added !!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getLocalizedMessage()));
    }

    private BidModel getBidModel() {
        BidModel bidModel = new BidModel();
        bidModel.setBidAmount("100");
        bidModel.setBidStatus(false);
        bidModel.setName("Amirr khan");
        bidModel.setUid(getUid());
        bidModel.setTimestamp(System.currentTimeMillis());
        return bidModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onBidPlaceSuccessFully(Object obj) {
        AppUtils.hideDialog();
        Toast.makeText(requireActivity(), "Bid Placed Successfully !!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBidPlaceFailed(Object obj) {
        AppUtils.hideDialog();
        Toast.makeText(requireActivity(), (String) obj, Toast.LENGTH_SHORT).show();
    }

    public void setBidRecData(int position) {
        AppUtils.showRequestDialog(requireActivity());
        String bidAmount = Utils.getBidPrice(position);
        getFireStoreReference().collection(BID_QUERY)
                .whereEqualTo(BID_AMOUNT, bidAmount)
                .whereEqualTo(GAME_NAME, gameType)
                .whereEqualTo(IS_ACTIVE, true)
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .limit(30)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    AppUtils.hideDialog();
                    if (null != e) {
                        Log.d(TAG, "setBidRecData: " + e.getLocalizedMessage());
                        Toast.makeText(requireActivity(), "something went wrong, try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null == queryDocumentSnapshots) {
                        Toast.makeText(requireActivity(), "something went wrong, try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<DocumentChange> snapshotList = queryDocumentSnapshots.getDocumentChanges();
                    Log.d(TAG, "loadBidData: " + snapshotList.size());
                    bidModels.clear();

                    int active = 0;
                    int paired = 0;
                    for (DocumentChange snapshot : snapshotList)
                        if (snapshot.getType() == DocumentChange.Type.ADDED) {
                            BidModel bidModel = snapshot.getDocument().toObject(BidModel.class);
                            bidModel.setBidId(snapshot.getDocument().getId());
                            bidModels.add(bidModel);
                            if (!bidModel.isBidStatus())
                                active++;
                            else paired++;
                        }
                    bidAdapter.notifyDataSetChanged();
                    updateActiveCounter(active, paired);
                });
    }

    @Override
    public void onItemClicked(Object obj) {
        BidModel bidModel = (BidModel) obj;
        Bundle bundle = new Bundle();
        bundle.putString(BID_ID, bidModel.getBidId());
        HomeScreen.getInstance().navigate(R.id.action_dashboardFragment_to_bidDetailsFragment, bundle);
    }

    public void updateActiveCounter(int active, int paired) {
        dashboardBinding.tcActiveCounter.setText(getFormatedAmount(active));
        dashboardBinding.tvPairedCounter.setText(getFormatedAmount(paired));
    }
}