package com.example.playludo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.playludo.HomeScreen;
import com.example.playludo.PaymentUtils.PaymentCallback;
import com.example.playludo.PaymentUtils.StartPayment;
import com.example.playludo.R;
import com.example.playludo.databinding.FragmentAddCreditsBinding;
import com.example.playludo.databinding.SubmitBidDialogViewBinding;
import com.example.playludo.interfaces.WithdrawInterface;
import com.example.playludo.models.AddCredits;
import com.example.playludo.models.TransactionModel;
import com.example.playludo.utils.AppConstant;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.example.playludo.utils.WithdrawAmount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.example.playludo.utils.Utils.getFireStoreReference;
import static com.example.playludo.utils.Utils.getUid;
import static com.example.playludo.utils.WithdrawAmount.ON_HOLD_FOR_WITHDRAW;


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
    String transactionId;

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
            transactionId = addCreditsBinding.etTransactionId.getText().toString();
            if (TextUtils.isEmpty(amount)) {
                Toast.makeText(requireActivity(), "Enter Amount !!", Toast.LENGTH_SHORT).show();
            } else if (Long.parseLong(amount) > 10000) {
                Toast.makeText(requireActivity(), "Add amount should be less than or equal to ₹10000 !!", Toast.LENGTH_SHORT).show();
            } else if (Long.parseLong(amount) < 10) {
                Toast.makeText(requireActivity(), "Add amount should be greater than or equal to ₹10 !!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(transactionId)) {
                Toast.makeText(requireActivity(), "Enter Transaction Id !!", Toast.LENGTH_SHORT).show();
            } else {
                submitRequestToAddCredits();
            }
        });
        addCreditsBinding.btnWithdrawAmount.setOnClickListener(v -> showWithdrawAmountDialog());
        addCreditsBinding.tvViewAllAddMoneyRequest.setOnClickListener(v -> navController.navigate(R.id.action_addCreditsFragment_to_addMoneyHistoryFragment));


    }

    private void submitRequestToAddCredits() {
        AppUtils.hideSoftKeyboard(requireActivity());
        AppUtils.showRequestDialog(requireActivity());
        getFireStoreReference().collection(AppConstant.ADD_MONEY_REQUEST).document().set(getAddMoneyTransactionMap()).addOnSuccessListener(aVoid -> {
            AppUtils.hideDialog();
            showAlertDialog();
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "try again !!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(requireActivity()).setTitle("Success")
                .setMessage("Your request to add ₹" + amount + " is submitted successfully\nCredits will be added to your wallet shortly !!")
                .setPositiveButton("Dismiss", (dialog, which) -> {
                    dialog.dismiss();
                    HomeScreen.getInstance().onSupportNavigateUp();
                }).show();
    }

    private Map<String, Object> getAddMoneyTransactionMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstant.AMOUNT, amount);
        map.put(AppConstant.TRANSACTION_ID, transactionId);
        map.put(AppConstant.TIMESTAMP, System.currentTimeMillis());
        map.put(AppConstant.UID, getUid());
        map.put(AppConstant.NUMBER, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        map.put(AppConstant.ADD_MONEY_STATUS, AppConstant.PENDING);
        return map;

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
        genderViewBinding.etGameId.setVisibility(View.VISIBLE);
        genderViewBinding.etGameId.setHint("Enter PayTm mobile number");
        genderViewBinding.btnOk.setOnClickListener(v -> {
            String amount = genderViewBinding.etAmount.getText().toString().trim();
            String number = genderViewBinding.etGameId.getText().toString().trim();
            if (!TextUtils.isDigitsOnly(number) || TextUtils.isEmpty(number) || number.length() < 10) {
                Toast.makeText(requireActivity(), "Enter Valid Mobile number", Toast.LENGTH_SHORT).show();
            } else if (!TextUtils.isDigitsOnly(amount) || TextUtils.isEmpty(amount)) {
                Toast.makeText(requireActivity(), "Enter Valid amount number", Toast.LENGTH_SHORT).show();
            } else if (Long.parseLong(amount) > 10000) {
                genderViewBinding.etAmount.setError("maximum withdrawal amount is ₹10,000");
            } else if (Long.parseLong(amount) < 50) {
                genderViewBinding.etAmount.setError("minimum withdrawal amount is ₹50");
            } else {
                optionDialog.dismiss();
                new AlertDialog.Builder(requireActivity()).setTitle("Your money Rs " + amount + " will be sent to " + number + " PayTm mobile number.")
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).setPositiveButton("Yes, Proceed ", (dialog, which) -> {
                    dialog.dismiss();
                    createWithdrawAmountRequest(amount, number);
                }).show();


            }

        });

        genderViewBinding.btnCancel.setOnClickListener(v -> optionDialog.dismiss());

        // the alert dialog
        optionDialog = new AlertDialog.Builder(requireActivity()).create();
        optionDialog.setView(formElementsView);
        optionDialog.show();


    }

    private void createWithdrawAmountRequest(String amount, String number) {
        AppUtils.showRequestDialog(requireActivity(), false);
        String walletAmountBeforeReq = addCreditsBinding.tvAmount.getText().toString();
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
        }).start(amount, number, walletAmountBeforeReq);

    }

    private void loadWalletAmount() {
        AppUtils.showRequestDialog(requireActivity());
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
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

        Utils.getFireStoreReference().collection(AppConstant.WALLET_NUMBER).document("wallet").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        addCreditsBinding.tvWalletNumber.setText("PayTm wallet number : " + documentSnapshot.getString("walletNumber"));
                    }
                }
            }
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

        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).update(map).addOnSuccessListener(aVoid -> {
            AppUtils.hideDialog();
            updateTransactions();
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "Error While Adding money !!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateTransactions() {

        Utils.getFireStoreReference().collection(AppConstant.TRANSACTIONS).document().set(getTransactionModel(amount)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requireActivity(), "Payment Completed Successfully !!", Toast.LENGTH_SHORT).show();
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