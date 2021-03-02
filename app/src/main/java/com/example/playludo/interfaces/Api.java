package com.example.playludo.interfaces;

import com.example.playludo.models.BidModel;
import com.example.playludo.responseModel.BidRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("updateBidStatus")
    Call<BidRes> updateBidStatus(
            @Body BidModel dashboard);

}
