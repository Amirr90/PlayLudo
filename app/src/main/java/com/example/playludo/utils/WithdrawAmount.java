package com.example.playludo.utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.playludo.fragments.AddCreditsFragment;
import com.example.playludo.interfaces.WithdrawInterface;
import com.example.playludo.models.AddCredits;
import com.example.playludo.models.TransactionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import static com.example.playludo.fragments.AddCreditsFragment.CREDITS;
import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;

import static com.example.playludo.utils.Utils.getUid;

public class WithdrawAmount {
    public static final String WITHDRAW_REQUEST_QUERY = "WithdrawRequest";
    public static final String PENDING = "pending";
    public static final String ON_HOLD_FOR_WITHDRAW = "onHoldForWithdraw";
    public static final String TYPE_WITHDRAW = "withdraw";
    Activity activity;
    WithdrawInterface withdrawInterface;


    String tId = String.valueOf(System.currentTimeMillis());

    public WithdrawAmount(Activity activity, WithdrawInterface withdrawInterface) {
        this.activity = activity;
        this.withdrawInterface = withdrawInterface;
    }

    public void start(String amount) {
        checkAmount(amount);
    }

    private void checkAmount(String amount) {
        Utils.getFireStoreReference().collection(AddCreditsFragment.USERS_QUERY).document(getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (null == documentSnapshot) {
                    withdrawInterface.onFailed("unable to fetch user balance !!");
                    return;
                }
                submitRequest(Long.valueOf(amount));
            }
        }).addOnFailureListener(e -> {
            withdrawInterface.onFailed(e.getLocalizedMessage());
            Toast.makeText(activity, "Failed to get user details !! ", Toast.LENGTH_SHORT).show();
        });
    }

    private void submitRequest(Long credits) {
        FirebaseFirestore db = Utils.getFireStoreReference();
        // Get a new write batch
        WriteBatch batch = db.batch();

        // create Withdraw request
        DocumentReference nycRef = db.collection(WITHDRAW_REQUEST_QUERY).document();
        batch.set(nycRef, getWithdrawModel(credits));

        // Update user Balance
        DocumentReference sfRef = db.collection(USERS_QUERY).document(getUid());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(CREDITS, FieldValue.increment(-credits));
        userMap.put(ON_HOLD_FOR_WITHDRAW, credits);
        batch.update(sfRef, userMap);

        // Create Transaction of User
        DocumentReference transRef = db.collection(AppConstant.TRANSACTIONS).document();
        batch.set(transRef, getTransactionModel(String.valueOf(credits)));

        // Commit the batch
        batch.commit().addOnSuccessListener(aVoid -> withdrawInterface.onSuccess("request submitted successfully !!")).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                withdrawInterface.onFailed("some thing went wrong !!, try again");
            }
        });
    }

    private AddCredits getWithdrawModel(Long credits) {
        AddCredits addCredits = new AddCredits();
        addCredits.setTimestamp(System.currentTimeMillis());
        addCredits.setUid(getUid());
        addCredits.setStatus(PENDING);
        addCredits.settId(tId);
        addCredits.setAmount(String.valueOf(credits));
        return addCredits;
    }


    private TransactionModel getTransactionModel(String bidAmount) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(bidAmount);
        transactionModel.setUid(getUid());
        transactionModel.setTimestamp(System.currentTimeMillis());
        transactionModel.setType(TYPE_WITHDRAW);
        return transactionModel;
    }
}
