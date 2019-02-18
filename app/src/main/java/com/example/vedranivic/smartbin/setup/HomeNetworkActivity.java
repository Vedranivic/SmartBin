package com.example.vedranivic.smartbin.setup;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vedranivic.smartbin.MainActivity;
import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.model.APIResponse;
import com.example.vedranivic.smartbin.network.SmartBinNetworkService;

import java.util.List;

public class HomeNetworkActivity extends AppCompatActivity {

    public final static String TAG = HomeNetworkActivity.class.getSimpleName();

    @BindView(R.id.etSSID)
    TextView etSSID;
    @BindView(R.id.etPASS)
    TextView etPASS;
    @BindView(R.id.btNext)
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSmartBinSetup();
        setContentView(R.layout.activity_home_network);
        ButterKnife.bind(this);

    }

    private void checkSmartBinSetup() {
        SmartBinNetworkService.initialize();
        SmartBinNetworkService.getUserID().enqueue(new Callback<APIResponse>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if(!response.body().getUserID().equals("")){
                    getSharedPreferences("SMARTBIN", MODE_PRIVATE).edit()
                            .putString("USER_ID", response.body().getUserID())
                            .commit();
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
                    wifiManager.saveConfiguration();
                    startActivity(new Intent(HomeNetworkActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                toastMessage("Connection failed. Please check connectivity and try again");
            }
        });
    }

    @OnClick(R.id.btNext)
    public void enableSmartBinHomeNetworkConnection() {
        if(validateInput()){
            String ssid = etSSID.getText().toString();
            String password = etPASS.getText().toString();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSSID.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etPASS.getWindowToken(),0);

            SmartBinNetworkService.initialize();
            SmartBinNetworkService.sendSSID(ssid).enqueue(new Callback<APIResponse>() {
                @Override
                public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                    Log.d(TAG,call.request().toString());
                    SmartBinNetworkService.sendPassword(password).enqueue(new Callback<APIResponse>() {
                        @SuppressLint("ApplySharedPref")
                        @Override
                        public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                            if(response.body().getReturnValue()==1){
                                toastMessage("smartBin successfully connected to home network: "+ssid);
                                String userID = response.body().getName() + response.body().getId();
                                getSharedPreferences("SMARTBIN", MODE_PRIVATE).edit()
                                        .putString("USER_ID", userID)
                                        .commit();
                                getSharedPreferences("SMARTBIN", MODE_PRIVATE).edit()
                                        .putBoolean("INFO_NOT_SET_UP", true)
                                        .commit();
                                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
                                wifiManager.saveConfiguration();
                                startActivity(new Intent(HomeNetworkActivity.this, MainActivity.class)
                                );
                                finish();
                            }
                            else{
                                toastMessage("Please check your SSID and password and try again");
                            }
                        }
                        @Override
                        public void onFailure(Call<APIResponse> call, Throwable t) {
                            toastMessage("Enabling failed. Please check your SSID and password and try again");
                        }
                    });
                }
                @Override
                public void onFailure(Call<APIResponse> call, Throwable t) {
                    toastMessage("Enabling failed. Please check connectivity and try again");
                }
            });


        }

    }

    private Boolean validateInput() {
        String ssid = etSSID.getText().toString();
        String password = etPASS.getText().toString();
        if(ssid.equals("") || password.equals("")){
            toastMessage("Please enter all fields first");
            return false;
        }
        if(password.length()<8){
            toastMessage("Please enter valid password (8 characters minimum)");
            return false;
        }
        return true;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }

    public boolean IsNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmartBinNetworkService.release();
    }
}
