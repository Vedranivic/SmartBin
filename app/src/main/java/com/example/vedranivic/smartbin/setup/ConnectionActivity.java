package com.example.vedranivic.smartbin.setup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vedranivic.smartbin.R;

import java.util.List;

/*----------------------------------------
Second step in setup - connecting to the
soft access point WiFi of the smartBin
device ("smartBin")
----------------------------------------*/
public class ConnectionActivity extends AppCompatActivity {

    @BindView(R.id.btNext)
    Button btNext;

    private Boolean connectedToAP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btNext)
    public void moveToHomeNetworkSetup(){
        if(!connectedToAP) {
            // Open WiFi settings
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),100);
        }
        else{
            // if connected to smartBin WiFi (softAP) move to next step
            startActivity(new Intent(ConnectionActivity.this, HomeNetworkActivity.class));
            finish();
        }
    }

    // Check if connected to smartBin Wifi
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100) {
            if(resultCode==RESULT_CANCELED) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                if (wifiManager.getConnectionInfo().getSSID().equals("\"smartBin\"")) {
                    connectedToAP = true;
                    btNext.setText(getResources().getString(R.string.next));
                }
            }
        }
    }

}
