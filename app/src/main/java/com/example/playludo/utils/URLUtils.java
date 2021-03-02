package com.example.playludo.utils;

import com.example.playludo.interfaces.Api;

public class URLUtils {

    public static final String BASE_URL = "http://52.172.134.222:207/api/v1.0/Patient/";

    public static Api getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(Api.class);
    }
}
