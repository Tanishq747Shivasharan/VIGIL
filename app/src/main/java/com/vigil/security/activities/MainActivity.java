package com.vigil.security.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vigil.security.R;
import com.vigil.security.fragments.*;

import com.vigil.security.utils.PermissionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionManager.hasPermissions(this)) {
            PermissionManager.requestPermissions(this);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        loadFragment(new WifiFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_wifi)
                selectedFragment = new WifiFragment();
            else if (item.getItemId() == R.id.nav_lan)
                selectedFragment = new LanFragment();
            else if (item.getItemId() == R.id.nav_password)
                selectedFragment = new PasswordFragment();
            else if (item.getItemId() == R.id.nav_sms)
                selectedFragment = new SmsFragment();
            else if (item.getItemId() == R.id.nav_history)
                selectedFragment = new HistoryFragment();

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionManager.PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                finish();
            }
        }
    }
}