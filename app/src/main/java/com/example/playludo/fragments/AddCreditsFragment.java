package com.example.playludo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.PaymentUtils.PaymentCallback;
import com.example.playludo.PaymentUtils.StartPayment;
import com.example.playludo.R;
import com.example.playludo.databinding.FragmentAddCreditsBinding;
import com.example.playludo.databinding.SubmitBidDialogViewBinding;
import com.example.playludo.interfaces.WithdrawInterface;
import com.example.playludo.models.AddCredits;
import com.example.playludo.models.TransactionModel;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Bid;
import com.example.playludo.utils.Utils;
import com.example.playludo.utils.WithdrawAmount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.example.playludo.fragments.BidDetailsFragment.TRANSACTIONS;
import static com.example.playludo.utils.Utils.getUid;
import static com.example.playludo.utils.WithdrawAmount.ON_HOLD_FOR_WITHDRAW;
import static com.example.playludo.utils.WithdrawAmount.WITHDRAW_REQUEST_QUERY;


public class AddCreditsFragment extends Fragment {
    private static final String TAG = "AddCreditsFragment";
    public static final String ADD_MONEY_QUERY = "AddMoney";
    public static final String ADD_MONEY_STATUS_STARTED = "started";
    public static final String ADD_MONEY_STATUS_SUCCESS = "success";
    public static final String ADD_MONEY_STATUS_FAILED = "failed";
    public static final String STATUS = "status";
    public static final String MONEY_RECEIVED_QUERY = "MoneyReceived";
    public static final String USERS_QUERY = "Users";
    public static final String CREDITS = "credits";
    public static final String INVEST = "invest";
    public static final String EARN = "earn";
    public static final String ON_HOLD = "onHold";
    public static final String TYPE_CREDIT = "credit";


    FragmentAddCreditsBinding addCreditsBinding;
    NavController navController;
    String amount = null;
    StartPayment startPayment;
    String tId;

    AlertDialog optionDialog;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addCreditsBinding = FragmentAddCreditsBinding.inflate(getLayoutInflater());
        return addCreditsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);


        loadWalletAmount();
        addCreditsBinding.btnOk.setOnClickListener(v -> {
            amount = addCreditsBinding.etAmount.getText().toString();

            if (TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount)) {
                if (Long.parseLong(amount) < 10) {
                    Toast.makeText(requireActivity(), "Add amount should be greater than ₹10", Toast.LENGTH_SHORT).show();
                    return;
                }
                tId = String.valueOf(System.currentTimeMillis());
                initTransaction(tId);
            }
        });
        addCreditsBinding.btnWithdrawAmount.setOnClickListener(v -> showWithdrawAmountDialog());


    }

    private void showWithdrawAmountDialog() {
        new AlertDialog.Builder(requireActivity()).setMessage("Do you want to withdraw amount ??")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    showEnterAmountDialog();

                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }

    private void showEnterAmountDialog() {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.submit_bid_dialog_view, null, false);

        final SubmitBidDialogViewBinding genderViewBinding = SubmitBidDialogViewBinding.bind(formElementsView);


        genderViewBinding.textView47.setText("Enter Amount to withdraw (minimum amount is ₹200)");
        genderViewBinding.btnOk.setOnClickListener(v -> {
            String amount = genderViewBinding.etAmount.getText().toString().trim();
            if (TextUtils.isDigitsOnly(amount) && !TextUtils.isEmpty(amount)) {

                if (Long.parseLong(amount) < 50) {
                    genderViewBinding.etAmount.setError("minimum withdrawal amount is ₹200");
                    return;
                }
                optionDialog.dismiss();

                createWithdrawAmountRequest();
            }
        });

        genderViewBinding.btnCancel.setOnClickListener(v -> optionDialog.dismiss());

        // the alert dialog
        optionDialog = new AlertDialog.Builder(requireActivity()).create();
        optionDialog.setView(formElementsView);
        optionDialog.show();


    }

    private void createWithdrawAmountRequest() {
        AppUtils.showRequestDialog(requireActivity(), false);
        new WithdrawAmount(requireActivity(), new WithdrawInterface() {
            @Override
            public void onSuccess(Object o) {
                AppUtils.hideDialog();
                Toast.makeText(requireActivity(), (String) o, Toast.LENGTH_SHORT).show();
                loadWalletAmount();
            }

            @Override
            public void onFailed(Object o) {
                AppUtils.hideDialog();
                Toast.makeText(requireActivity(), (String) o, Toast.LENGTH_SHORT).show();
            }
        }).start();

    }

    private void loadWalletAmount() {
        AppUtils.showRequestDialog(requireActivity());
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).get().addOnSuccessListener(documentSnapshot -> {
            AppUtils.hideDialog();
            if (null != documentSnapshot) {
                {
                    try {
                        addCreditsBinding.tvAmount.setText("Available Balance: " + AppUtils.getCurrencyFormat(documentSnapshot.getLong(CREDITS)));
                        if (null != documentSnapshot.get(ON_HOLD_FOR_WITHDRAW)) {
                            addCreditsBinding.tvWithdrawAmount.setText(AppUtils.getCurrencyFormat(documentSnapshot.getLong(ON_HOLD_FOR_WITHDRAW)));
                            addCreditsBinding.withDrawLay.setVisibility(View.VISIBLE);
                        } else addCreditsBinding.withDrawLay.setVisibility(View.GONE);
                        addCreditsBinding.btnWithdrawAmount.setEnabled(documentSnapshot.getLong(CREDITS) >= 200);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "loadWalletAmount: " + e.getLocalizedMessage());
                    }
                }
            } else
                Toast.makeText(requireActivity(), "Unable to get wallet amount !!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "User not found !!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initTransaction(String tId) {

        if (AppUtils.isNetworkConnected(requireActivity())) {
            AppUtils.showRequestDialog(requireActivity());
            Utils.getFireStoreReference().collection(ADD_MONEY_QUERY).document(tId).set(getAddMoneyModel(tId))
                    .addOnSuccessListener(aVoid -> initRazorPay(tId))
                    .addOnFailureListener(e -> {
                        AppUtils.hideDialog();
                        Toast.makeText(requireActivity(), "try again", Toast.LENGTH_SHORT).show();
                    });
        } else Toast.makeText(requireActivity(), "No internet ", Toast.LENGTH_SHORT).show();
    }


    private void initRazorPay(String tId) {
        startPayment = new StartPayment(requireActivity());
        startPayment.setAmount(amount);
        startPayment.setName("Aamirr Khan");
        startPayment.initPayment(new PaymentCallback() {
            @Override
            public void onPaymentSuccess() {
                updatePaymentStatus(ADD_MONEY_STATUS_SUCCESS, tId);
            }

            @Override
            public void onPaymentFailed() {
                Toast.makeText(requireActivity(), "Failed To get Payment", Toast.LENGTH_SHORT).show();
                updatePaymentStatus(ADD_MONEY_STATUS_FAILED, tId);
            }
        });
    }

    private void updatePaymentStatus(String addMoneyStatusSuccess, String tId) {
        Utils.getFireStoreReference().collection(ADD_MONEY_QUERY).document(tId).update(getMap(addMoneyStatusSuccess))
                .addOnSuccessListener(aVoid -> {
                    if (addMoneyStatusSuccess.equalsIgnoreCase(ADD_MONEY_STATUS_SUCCESS))
                        updateToWallet(tId);
                }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "something went wrong !!, if your money is not credited pls contact us!!", Toast.LENGTH_SHORT).show());
    }

    private void updateToWallet(String tId) {

        Utils.getFireStoreReference().collection(MONEY_RECEIVED_QUERY).document(tId).set(getAddMoneyMapForAdmin()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateToUserWallet();
            }
        });
    }

    private void updateToUserWallet() {
        Map<String, Object> map = new HashMap<>();
        map.put("credits", FieldValue.increment(Long.parseLong(amount)));
        map.put("invest", FieldValue.increment(Long.parseLong(amount)));

        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).update(map).addOnSuccessListener(aVoid -> {
            AppUtils.hideDialog();
            updateTransactions();
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "Error While Adding money!!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateTransactions() {

        Utils.getFireStoreReference().collection(TRANSACTIONS).document().set(getTransactionModel(amount)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requireActivity(), "Payment Completed Successfully !!!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_addCreditsFragment_to_addedSuccesssfullyFragment);
            }
        }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "something went wrong !!", Toast.LENGTH_SHORT).show());
    }

    private TransactionModel getTransactionModel(String bidAmount) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(bidAmount);
        transactionModel.setUid(getUid());
        transactionModel.setTimestamp(System.currentTimeMillis());
        transactionModel.setType(TYPE_CREDIT);
        return transactionModel;
    }

    private Object getAddMoneyMapForAdmin() {
        Map<String, Object> map = new HashMap<>();
        map.put("amount", amount);
        map.put("tId", tId);
        map.put("uid", getUid());
        map.put("paymentCompletedAt", System.currentTimeMillis());
        map.put("paymentStartedAt", tId);
        map.put("status", ADD_MONEY_STATUS_SUCCESS);
        return map;
    }

    private Map<String, Object> getMap(String status) {
        Map<String, Object> map = new HashMap<>();
        map.put(STATUS, status);
        map.put("paymentId", System.currentTimeMillis());
        return map;
    }

    private AddCredits getAddMoneyModel(String tId) {
        AddCredits addCredits = new AddCredits();
        addCredits.setAmount(amount);
        addCredits.settId(tId);
        addCredits.setStatus(ADD_MONEY_STATUS_STARTED);
        addCredits.setUid(getUid());
        addCredits.setTimestamp(System.currentTimeMillis());
        return addCredits;
    }


}