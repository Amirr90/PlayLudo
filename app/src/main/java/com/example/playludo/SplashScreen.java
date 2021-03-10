package com.example.playludo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.playludo.utils.AppConstant;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    String bidId, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                checkLoginStatus();
            }
        }.start();

        getData();
    }

    private void getData() {
        if (null != getIntent().getExtras()) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("bidId")) {
                    Log.d(TAG, "getData: BidId " + getIntent().getExtras().getString(key));
                    bidId = getIntent().getExtras().getString(key);
                }
                if (key.equals("type")) {
                    Log.d(TAG, "getData: type " + getIntent().getExtras().getString(key));
                    type = getIntent().getExtras().getString(key);
                }
            }

        } else Log.d(TAG, "getData: null");
    }

    private void checkLoginStatus() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            login();
        } else {
            startActivity(new Intent(SplashScreen.this, HomeScreen.class)
                    .putExtra(AppConstant.BID_ID, bidId)
                    .putExtra(AppConstant.NOTIFICATION_TYPE, type)
            );
            finish();
        }
    }

    private void login() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_launcher_foreground)
                        .setTheme(R.style.ThemePlayLudoNoActionBar)
                        .build(),
                10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            //loginTypeScreenBinding.progressBar.setVisibility(View.GONE);
            if (resultCode == RESULT_OK) {
                Toast.makeText(SplashScreen.this, "sign in successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashScreen.this, HomeScreen.class));
                finish();
            } else {
                Toast.makeText(SplashScreen.this, "sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

}