package com.example.playludo.utils;

import com.example.playludo.interfaces.Api;

public class URLUtils {

    public static final String BASE_URL = "https://us-central1-playludo-35d49.cloudfunctions.net/";

    public static Api getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(Api.class);
    }
}
