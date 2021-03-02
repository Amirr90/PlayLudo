package com.example.playludo.responseModel;

import com.example.playludo.models.BidModel;

public class BidRes {

    int responseCode;
    String responseMessage;
    BidModel responseValue;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public BidModel getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(BidModel responseValue) {
        this.responseValue = responseValue;
    }
}
