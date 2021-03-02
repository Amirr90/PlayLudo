package com.example.playludo.utils;

import com.example.playludo.interfaces.Api;
import com.example.playludo.interfaces.ApiCallbackInterface;
import com.example.playludo.models.BidModel;
import com.example.playludo.responseModel.BidRes;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiUtils {
    public static void updateBidStatus(BidModel model,
                                       final ApiCallbackInterface apiCallbackInterface) {

        try {
            final Api api = URLUtils.getAPIService();
            Call<BidRes> dashBoardResCall = api.updateBidStatus(model);

            dashBoardResCall.enqueue(new Callback<BidRes>() {
                @Override
                public void onResponse(@NotNull Call<BidRes> call, @NotNull Response<BidRes> response) {
                    if (response.code() == 200 && null != response.body()) {
                        if (response.body().getResponseCode() == 1) {
                            apiCallbackInterface.onSuccess(response.body().getResponseValue());
                        } else {
                            apiCallbackInterface.onFailed(response.body().getResponseMessage());
                        }
                    } else apiCallbackInterface.onFailed(response.message());
                }

                @Override
                public void onFailure(@NotNull Call<BidRes> call, @NotNull Throwable t) {
                    apiCallbackInterface.onFailed(t.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
