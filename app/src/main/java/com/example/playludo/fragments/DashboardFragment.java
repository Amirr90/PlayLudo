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

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.R;
import com.example.playludo.SubmitNewBidFragment;
import com.example.playludo.adapter.TabAdapter;
import com.example.playludo.databinding.FragmentDashboardBinding;
import com.example.playludo.databinding.SubmitBidDialogViewBinding;
import com.example.playludo.interfaces.BidInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.models.User;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Bid;
import com.example.playludo.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.example.playludo.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.utils.Utils.getUid;


public class DashboardFragment extends Fragment implements BidInterface {
    private static final String TAG = "DashboardFragment";


    FragmentDashboardBinding dashboardBinding;
    NavController navController;
    AlertDialog optionDialog;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dashboardBinding = FragmentDashboardBinding.inflate(getLayoutInflater());

        setTab();
        return dashboardBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        updateUserData();

        dashboardBinding.tvFundAmount.setOnClickListener(v -> addBidData());
        dashboardBinding.btnAddMoreCredits.setOnClickListener(v -> navController.navigate(R.id.action_dashboardFragment_to_addCreditsFragment));
        dashboardBinding.btnAddBid.setOnClickListener(v -> {
            //navController.navigate(R.id.action_dashboardFragment_to_submitNewBidFragment);
            openSubmitBidDialog();
        });
    }

    private void openSubmitBidDialog() {

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.submit_bid_dialog_view, null, false);

        final SubmitBidDialogViewBinding genderViewBinding = SubmitBidDialogViewBinding.bind(formElementsView);


        genderViewBinding.btnOk.setOnClickListener(v -> {
            String amount = genderViewBinding.etAmount.getText().toString().trim();
            if (TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount)) {

                if (Long.parseLong(amount) < 50) {
                    genderViewBinding.etAmount.setError("minimum bid amount is 50");
                    return;
                }
                optionDialog.dismiss();
                AppUtils.showRequestDialog(requireActivity(), false);
                Bid bid = new Bid(requireActivity());
                bid.setAmount(amount);
                bid.setUid(getUid());
                bid.start(this);
            }
        });

        genderViewBinding.btnCancel.setOnClickListener(v -> optionDialog.dismiss());

        // the alert dialog
        optionDialog = new AlertDialog.Builder(requireActivity()).create();
        optionDialog.setView(formElementsView);
        optionDialog.show();


    }

    private void updateUserData() {
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    User user = documentSnapshot.toObject(User.class);
                    dashboardBinding.setUser(user);
                    dashboardBinding.tvFundAmount.setText(AppUtils.getCurrencyFormat(user.getCredits()));
                    dashboardBinding.tvInvested.setText(AppUtils.getCurrencyFormat(user.getInvest()));
                    dashboardBinding.tvEarned.setText(AppUtils.getCurrencyFormat(user.getEarn()));
                }
            }
        });
    }

    private void addBidData() {

        Utils.getFireStoreReference().collection(BID_QUERY).add(getBidModel()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(requireActivity(), "Added !!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getLocalizedMessage()));
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

    private void setTab() {
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹50 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹100 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹200 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹250 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹300 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹500 Bet"));
        dashboardBinding.tabLayout.addTab(dashboardBinding.tabLayout.newTab().setText("₹1000 Bet"));
        dashboardBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_START);


        final TabAdapter adapter = new TabAdapter(requireActivity(), requireActivity().getSupportFragmentManager(),
                dashboardBinding.tabLayout.getTabCount());

        dashboardBinding.viewPager.setAdapter(adapter);
        dashboardBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dashboardBinding.tabLayout));
        dashboardBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
}