package com.example.playludo.utils;

import com.example.playludo.models.BidModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Utils {


    public static FirebaseFirestore getFireStoreReference() {
        return FirebaseFirestore.getInstance();
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getBidPrice(int position) {
        List<BidModel> bidModels = getBidPriceList();
        if (bidModels.size() > position)
            return bidModels.get(position).getBidAmount();
        else return "";
    }

    public static List<BidModel> getBidPriceList() {
        List<BidModel> bidModels = new ArrayList<>();
        bidModels.add(new BidModel("50"));
        bidModels.add(new BidModel("100"));
        bidModels.add(new BidModel("200"));
        bidModels.add(new BidModel("250"));
        bidModels.add(new BidModel("300"));
        bidModels.add(new BidModel("500"));
        bidModels.add(new BidModel("1000"));
        return bidModels;
    }

    public static String getMobile() {
        if (null == FirebaseAuth.getInstance().getCurrentUser())
            return "";
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }
}
