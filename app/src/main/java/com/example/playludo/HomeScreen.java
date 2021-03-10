package com.example.playludo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.playludo.PaymentUtils.StartPayment;
import com.example.playludo.databinding.ActivityHomeScreenBinding;
import com.example.playludo.fragments.AppDashboardFragment;
import com.example.playludo.fragments.AppDashboardFragmentDirections;
import com.example.playludo.fragments.DashboardFragmentDirections;
import com.example.playludo.utils.AppConstant;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.utils.Utils.getUid;


public class HomeScreen extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG = "HomeScreen";

    ActivityHomeScreenBinding homeScreenBinding;
    NavController navController;
    MenuItem cart = null;

    public static HomeScreen instance;

    public static HomeScreen getInstance() {
        return instance;
    }

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
        generateFcmToken();
        getData();
    }

    public void getData() {
        String bidId = getIntent().getStringExtra(AppConstant.BID_ID);
        String type = getIntent().getStringExtra(AppConstant.NOTIFICATION_TYPE);

        Log.d(TAG, "bidId: " + bidId);
        Log.d(TAG, "type: " + type);

        if (null != bidId && !bidId.isEmpty()) {
            AppDashboardFragmentDirections.ActionAppDashboardFragmentToBidDetailsFragment action = AppDashboardFragmentDirections.actionAppDashboardFragmentToBidDetailsFragment();
            action.setGameId(getIntent().getStringExtra(AppConstant.BID_ID));
            navController.navigate(action);
        } else if (null != type && !type.isEmpty()) {
            navController.navigate(R.id.action_appDashboardFragment_to_addMoneyHistoryFragment2);
        }

        /*if (HomeScreen.getInstance().getIntent().hasExtra(AppConstant.BID_ID) && getIntent().getStringExtra(AppConstant.BID_ID) != null &&) {
            AppDashboardFragmentDirections.ActionAppDashboardFragmentToBidDetailsFragment action = AppDashboardFragmentDirections.actionAppDashboardFragmentToBidDetailsFragment();
            action.setGameId(getIntent().getStringExtra(AppConstant.BID_ID));
            navController.navigate(action);
        } else if (getIntent().hasExtra(AppConstant.NOTIFICATION_TYPE) && getIntent().getStringExtra(AppConstant.NOTIFICATION_TYPE) != null) {
            if (getIntent().getStringExtra(AppConstant.NOTIFICATION_TYPE).equals(AppConstant.ADD_MONEY)) {
                navController.navigate(R.id.action_appDashboardFragment_to_addMoneyHistoryFragment2);
            }

        }*/


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
        if (item.getItemId() == R.id.logout) {
            showLogoutDialog();
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(HomeScreen.this).setMessage("Want to Logout??").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppUtils.showRequestDialog(HomeScreen.this);
                AuthUI.getInstance()
                        .signOut(HomeScreen.this)
                        .addOnCompleteListener(task -> {
                            AppUtils.hideDialog();
                            startActivity(new Intent(HomeScreen.this, SplashScreen.class));
                            finish();
                        });
            }
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
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

    public static void generateFcmToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken2", newToken);
            Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).update("token", newToken);
        });

    }
}