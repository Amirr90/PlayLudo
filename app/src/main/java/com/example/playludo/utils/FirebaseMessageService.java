package com.example.playludo.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());

        Log.d(TAG, "onMessageReceived: ");

        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getData() + "...." + remoteMessage.getFrom());

    }

}
