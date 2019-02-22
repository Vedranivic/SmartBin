package com.example.vedranivic.smartbin.base;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Constants {
    public static final String SMARTBINAPI_BASE_URL = "http://192.168.4.1/";
    public static final String SHARED_PREFS_NAME = "SMARTBIN";
    public static final String USER_ID_KEY = "USER_ID";
    public static final int ONE_DAY = 1000*60*60*24;
    public static final int ONE_HOUR = 1000*60*60;
    public static final int HALF_HOUR = 1000*60*30;
    public static final int MINUTES_15 = 1000*60*15;
    public static final int MINUTES_2 = 1000*60*2;
    public static final String NO_INTERNET_MSG = "No Internet connection";
    public final static String SCHEDULE_FOR_COLLECTION_ACTION = "Collect";
    public final static String DISMISS_ACTION = "Dismiss";
    public final static String CHANNEL_REMINDER = "REMINDER";


    public static boolean IsNetworkConnected(Activity context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            Toast.makeText(context, NO_INTERNET_MSG, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
