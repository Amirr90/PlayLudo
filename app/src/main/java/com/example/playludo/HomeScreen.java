package com.example.playludo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.playludo.PaymentUtils.StartPayment;
import com.example.playludo.databinding.ActivityHomeScreenBinding;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;


public class HomeScreen extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG = "HomeScreen";

    ActivityHomeScreenBinding homeScreenBinding;
    NavController navController;
    MenuItem cart = null;

    public static HomeScreen instance;

    public static HomeScreen getInstance() {
        return instance;
    }

    int cartCounter = 100;
    TextView cartTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
        instance = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        cart = menu.findItem(R.id.notification);
        View actionView = cart.getActionView();
        /*cartTv = actionView.findViewById(R.id.tvCartCount);
        cartTv.setText(String.valueOf(cartCounter));*/
        actionView.setOnClickListener(v -> onOptionsItemSelected(cart));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    public void navigate(int id) {
        navController.navigate(id);
    }

    public void navigate(int id, Bundle bundle) {
        navController.navigate(id, bundle);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Log.d(TAG, "onPaymentSuccess: Status " + s);
        Log.d(TAG, "onPaymentSuccess: " + paymentData.getPaymentId());
        StartPayment.getInstance().updatePaymentStatus(1, paymentData.getPaymentId());

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d(TAG, "onPaymentError: " + paymentData.getData());
        StartPayment.getInstance().updatePaymentStatus(0, paymentData.getData().toString());
    }
}