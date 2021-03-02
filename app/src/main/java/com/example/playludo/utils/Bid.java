package com.example.playludo.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.playludo.interfaces.ApiCallbackInterface;
import com.example.playludo.interfaces.BidInterface;
import com.example.playludo.models.AddCredits;
import com.example.playludo.models.BidModel;
import com.example.playludo.models.TransactionModel;
import com.example.playludo.models.User;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;


public class Bid extends AddCredits {
    private static final String TAG = "Bid";
    private static final String BIDS = "Bids";
    private static final String BID_AMOUNT = "bidAmount";
    public static final String UID = "uid";
    public static final String TIMESTAMP = "timestamp";
    public static final String BID_STATUS = "bidStatus";
    public static final String GAME_ID = "gameId";
    public static final String IS_ACCEPTED = "isAccepted";
    public static final String BID_STATUS_PENDING = "pending";
    public static final String NAME = "name";
    public static final String IS_ACTIVE = "isActive";
    public static final String GAME_NAME = "gameName";
    Activity activity;
    BidInterface bidInterface;
    String gameId, gameName, name;

    public Bid(Activity activity, String name) {
        this.activity = activity;
        this.name = name;
    }

    public Bid(Activity activity) {
        this.activity = activity;
    }

    public void start(BidInterface bidInterface, String gameId, String gameName) {
        this.bidInterface = bidInterface;
        this.gameId = gameId;
        this.gameName = gameName;
        checkUserWallet();
    }

    private void checkUserWallet() {
        Utils.getFireStoreReference().collection(USERS_QUERY).document(Utils.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user.getCredits() < Long.parseLong(getAmount())) {
                bidInterface.onBidPlaceFailed("Insufficient Balance");
                return;
            }
            //have sufficient balance
            initSubmitBid(getAmount());

        }).addOnFailureListener(documentSnapshot -> Toast.makeText(activity, "Unable To fetch Wallet Amount", Toast.LENGTH_SHORT).show());
    }

    private void initSubmitBid(String amount) {

        FirebaseFirestore db = Utils.getFireStoreReference();

        // Get a new write batch
        WriteBatch batch = db.batch();

        //1. deduct amount from user 'Wallet'
        DocumentReference nycRef = db.collection(USERS_QUERY).document(getUid());
        Map<String, Object> map = new HashMap<>();
        map.put("credits", FieldValue.increment(-Long.parseLong(amount)));
        map.put("invest", FieldValue.increment(Long.parseLong(amount)));
        batch.update(nycRef, map);

        //2. Create new BID request
        Map<String, Object> bidMap = new HashMap<>();
        bidMap.put(BID_AMOUNT, amount);
        bidMap.put(UID, getUid());
        bidMap.put(NAME, name);
        bidMap.put(TIMESTAMP, System.currentTimeMillis());
        bidMap.put(BID_STATUS, false);
        bidMap.put(GAME_ID, gameId);
        bidMap.put(IS_ACTIVE, true);
        bidMap.put(GAME_NAME, gameName);
        bidMap.put("bidId", "" + System.currentTimeMillis());
        DocumentReference sfRef = db.collection(BIDS).document();
        batch.set(sfRef, bidMap);

        // Create Transaction of User
        DocumentReference transRef = db.collection(AppConstant.TRANSACTIONS).document();
        batch.set(transRef, getTransactionModel(String.valueOf(amount)));

        // Commit the batch
        batch.commit().addOnSuccessListener(task -> {
            bidInterface.onBidPlaceSuccessFully(task);
            Log.d(TAG, "onComplete: " + task);

        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
            bidInterface.onBidPlaceFailed(e.getLocalizedMessage());
        });
    }

    private TransactionModel getTransactionModel(String bidAmount) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(bidAmount);
        transactionModel.setUid(getUid());
        transactionModel.setTimestamp(System.currentTimeMillis());
        transactionModel.setType(AppConstant.TYPE_DEBIT);
        return transactionModel;
    }

    public void loss(BidModel bidModel, ApiCallbackInterface apiCallbackInterface) {
        ApiUtils.updateBidStatus(bidModel, new ApiCallbackInterface() {
            @Override
            public void onSuccess(Object obj) {
                apiCallbackInterface.onSuccess(obj);
            }

            @Override
            public void onFailed(String msg) {
                apiCallbackInterface.onFailed(msg);
            }
        });
    }
}
