package com.example.vedranivic.smartbin.setup;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.SplashActivity;

public class PlacementActivity extends AppCompatActivity {

    @BindView(R.id.btNext)
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btNext)
    public void moveToConnectionSetup(){
        startActivity(new Intent(PlacementActivity.this, ConnectionActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
