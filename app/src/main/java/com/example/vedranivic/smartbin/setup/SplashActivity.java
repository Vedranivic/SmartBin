package com.example.vedranivic.smartbin.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/*----------------------------------------
Splash Activity that is show only on first
setup (connection) of the smartBin
----------------------------------------*/
public class SplashActivity extends AppCompatActivity {

    // Timer for how long the splash lasts (2 seconds)
    private final int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer (2 s) is over
            startActivity(new Intent(SplashActivity.this, PlacementActivity.class));
            finish();
        }, SPLASH_TIME_OUT);
    }

}
