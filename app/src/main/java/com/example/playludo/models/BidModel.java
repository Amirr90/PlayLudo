package com.example.playludo.models;

public class BidModel {
    String name;
    String bidAmount;
    String uid;
    boolean bidStatus;
    long timestamp;
    String bidId;
    String gameId;
    String gameName;
    long bidAcceptTimestamp;
    String bidAcceptBy;
    String bidAccepterName;
    String gameStatus;
    String image;
    String resultUploadedBy;
    String playerTwoUniqueId;

    public String getPlayerTwoUniqueId() {
        return playerTwoUniqueId;
    }

    public void setPlayerTwoUniqueId(String playerTwoUniqueId) {
        this.playerTwoUniqueId = playerTwoUniqueId;
    }

    public String getResultUploadedBy() {
        return resultUploadedBy;
    }

    public void setResultUploadedBy(String resultUploadedBy) {
        this.resultUploadedBy = resultUploadedBy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getBidAcceptTimestamp() {
        return bidAcceptTimestamp;
    }

    public void setBidAcceptTimestamp(long bidAcceptTimestamp) {
        this.bidAcceptTimestamp = bidAcceptTimestamp;
    }

    public String getBidAcceptBy() {
        return bidAcceptBy;
    }

    public void setBidAcceptBy(String bidAcceptBy) {
        this.bidAcceptBy = bidAcceptBy;
    }

    public String getBidAccepterName() {
        return bidAccepterName;
    }

    public void setBidAccepterName(String bidAccepterName) {
        this.bidAccepterName = bidAccepterName;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

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
