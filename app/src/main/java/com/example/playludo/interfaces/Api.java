package com.example.playludo.interfaces;

import com.example.playludo.models.BidModel;
import com.example.playludo.responseModel.BidRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @GET("updateBIdStatus")
    Call<BidRes> updateBidStatus(@Query("uid") String uid, @Query("bidId") String bidId);

}
