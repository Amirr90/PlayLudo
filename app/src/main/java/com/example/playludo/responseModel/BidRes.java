package com.example.playludo.responseModel;

import com.example.playludo.models.BidModel;

import java.util.List;

public class BidRes {

    int responseCode;
    String responseMessage;
    List<BidModel> responseValue;

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public List<BidModel> getResponseValue() {
        return responseValue;
    }
}
