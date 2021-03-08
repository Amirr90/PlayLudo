package com.example.playludo.interfaces;

import com.example.playludo.responseModel.BidRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("updateBIdStatus")
    Call<BidRes> updateBidStatus(@Query("uid") String uid, @Query("bidId") String bidId);


    @GET("cancelBid")
    Call<BidRes> cancelBid(@Query("bidId") String uid, @Query("uid") String bidId);

    @GET("requestNewRoomCode")
    Call<BidRes> requestNewRoomCode(@Query("bidId") String uid, @Query("uid") String bidId);

    @GET("updateCode")
    Call<BidRes> updateCode(@Query("bidId") String uid,
                            @Query("roomCode") String roomCode,
                            @Query("uid") String bidId);

}
