package com.crowdconquer.crowdconquer.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.crowdconquer.crowdconquer.R;
import com.rey.material.widget.FloatingActionButton;


public class LoginActivity extends Activity {

    FloatingActionButton locationButton;
    FloatingActionButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        startListeners();
    }

    void initViews() {
        setContentView(R.layout.activity_login);
        locationButton = (FloatingActionButton) findViewById(R.id.locationButton);
        loginButton = (FloatingActionButton) findViewById(R.id.loginButton);
    }

    void startListeners() {
        setOnClickListenerLogin();
    }

    void setOnClickListenerLogin(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppReadyToLogin()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    boolean isAppReadyToLogin(){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS and try again");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            return false;
        } else {
            return true;
        }

    }
}