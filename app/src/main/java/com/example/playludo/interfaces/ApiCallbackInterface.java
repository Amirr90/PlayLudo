package com.example.playludo.interfaces;

import java.util.List;

public interface ApiCallbackInterface {
    void onSuccess(Object obj);

    void onFailed(String msg);
}
