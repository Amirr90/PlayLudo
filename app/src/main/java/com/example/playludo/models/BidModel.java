package com.example.playludo.models;

public class BidModel {
    String name;
    String bidAmount;
    String uid;
    boolean bidStatus;
    long timestamp;
    String bidId;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public BidModel(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public BidModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public boolean isBidStatus() {
        return bidStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", bidAmount='" + bidAmount + '\'' +
                ", bidStatus=" + bidStatus +
                ", timestamp=" + timestamp +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public void setBidStatus(boolean bidStatus) {
        this.bidStatus = bidStatus;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
