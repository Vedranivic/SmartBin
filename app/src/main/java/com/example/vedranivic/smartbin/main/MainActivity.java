package com.example.vedranivic.smartbin.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.setup.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    private Fragment mybinFragment = new MyBinFragment();
    private Fragment settingsFragment = new SettingsFragment();
    private Fragment statisticsFragment = new StatisticsFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment activeFragment = mybinFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_mybin);

        firstTimeSetup();

    }

    private void firstTimeSetup() {
        if(getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID","").equals("")){
            startActivity(new Intent(MainActivity.this,SplashActivity.class));
            finish();
        }
        else{
            if(getSharedPreferences("SMARTBIN", MODE_PRIVATE).getBoolean("INFO_NOT_SET_UP", false)){
                bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
            }
        }
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if(!menuItem.isChecked()) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_mybin:
                    activeFragment = mybinFragment;
                    break;

                case R.id.navigation_settings:
                    activeFragment =  settingsFragment;
                    break;

                case R.id.navigation_statistics:
                    activeFragment = statisticsFragment;
                    break;
            }
        }
        return loadFragment(activeFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentManager = null;
        mybinFragment = null;
        settingsFragment = null;
        statisticsFragment = null;
        activeFragment = null;
    }
}
