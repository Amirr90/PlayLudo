package com.example.playludo.models;

public class AddCredits {
    long timestamp;
    String amount;
    String tId;
    String uid;
    String status;
    String name;
    String payTmNumber;
    String walletAmountBeforeReq;
    

    public String getWalletAmountBeforeReq() {
        return walletAmountBeforeReq;
    }

    public void setWalletAmountBeforeReq(String walletAmountBeforeReq) {
        this.walletAmountBeforeReq = walletAmountBeforeReq;
    }

    public String getPayTmNumber() {
        return payTmNumber;
    }

    public void setPayTmNumber(String payTmNumber) {
        this.payTmNumber = payTmNumber;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    String userMobileNumber;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
