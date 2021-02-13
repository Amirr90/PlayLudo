package com.example.playludo.PaymentUtils;

import android.app.Activity;
import android.util.Log;

import com.example.playludo.models.AddCredits;
import com.example.playludo.utils.Utils;
import com.razorpay.Checkout;

import org.json.JSONObject;


public class StartPayment extends AddCredits {
    public static StartPayment instance;

    public static StartPayment getInstance() {
        return instance;
    }

    private static final String TAG = "StartPayment";
    Activity activity;
    PaymentCallback paymentCallback;

    public StartPayment(Activity activity) {
        this.activity = activity;
        instance = this;
    }

    public void initPayment(PaymentCallback paymentCallback) {
        this.paymentCallback = paymentCallback;
        initRazorPay();
    }

    private void initRazorPay() {

        Credentials credentials = new Credentials();
        Checkout.preload(activity);
        Checkout checkout = new Checkout();
        checkout.setKeyID(credentials.getKEY_TEST());

        String image = "https://digidoctor.in/assets/images/logonew.png";

        int amount = 100 * Integer.parseInt(getAmount());
        try {
            JSONObject options = new JSONObject();
            options.put("name", this.getName());
            options.put("description", "" + System.currentTimeMillis());
            options.put("image", image);
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "" + amount);//pass amount in currency subunits
            options.put("prefill.contact", Utils.getMobile());
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void updatePaymentStatus(int status,String paymentId) {
        if (status == 1) {
            paymentCallback.onPaymentSuccess();
        } else {
            paymentCallback.onPaymentFailed();
        }

    }

}
