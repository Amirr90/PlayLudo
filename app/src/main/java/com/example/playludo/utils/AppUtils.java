package com.example.playludo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.example.playludo.R;

import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {
    static ProgressDialog progressDialog;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static void showRequestDialog(Activity activity) {
        try {
            if (!((Activity) activity).isFinishing()) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);

                    progressDialog.show();
                } else {
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showRequestDialog(Activity activity, boolean isCancelable) {
        try {
            if (!((Activity) activity).isFinishing()) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);

                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setCancelable(isCancelable);
                    progressDialog.setContentView(R.layout.progress_bar);

                    progressDialog.show();
                } else {
                    progressDialog = ProgressDialog.show(activity, null, null, false, true);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
                    progressDialog.setContentView(R.layout.progress_bar);
                    progressDialog.setCancelable(isCancelable);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            try {
                @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
                View view = activity.getCurrentFocus();
                if (view != null) {
                    IBinder binder = view.getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getDateInDMYFormatFromTimestamp(long currentTimeMillis, String outFormat) {
        try {
            String value = new java.text.SimpleDateFormat(outFormat).
                    format(new java.util.Date(currentTimeMillis));
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Animation fadeIn(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.fade_in);
    }

    public static Animation fadeOut(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.fade_out);
    }

    public static String getCurrencyFormat(double num) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(num);
    }

    public static String getCurrencyFormat(long num) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(num);
    }

    public static String getCurrencyFormat(String num) {
        Double number = Double.parseDouble(num);
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        return NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(number);

    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

}
