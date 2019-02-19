package com.example.vedranivic.smartbin.setup;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.vedranivic.smartbin.R;

/*----------------------------------------
First step in setup - instructions for
proper placement of the smartBin device
----------------------------------------*/
public class PlacementActivity extends AppCompatActivity {

    @BindView(R.id.btNext)
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);
        ButterKnife.bind(this);

    }

    // move to next Step
    @OnClick(R.id.btNext)
    public void moveToConnectionSetup(){
        startActivity(new Intent(PlacementActivity.this, ConnectionActivity.class));
        finish();
    }
}
