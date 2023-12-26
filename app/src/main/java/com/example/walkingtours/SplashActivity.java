package com.example.walkingtours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    private static final int LOCATION_REQUEST = 111;
    private static final int BACKGROUND_LOCATION_REQUEST = 222;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable color
                = new ColorDrawable(Color.parseColor("#20B2AA"));
        actionBar.setBackgroundDrawable(color);

        setContentView(R.layout.activity_splash);
//        ProgressBar spinner = new android.widget.ProgressBar(
//                context,
//                null,
//                android.R.attr.progressBarStyle);


        boolean permission = checkPermission();

        if(permission) {
            FenceVolley fenceVolley = new FenceVolley(this);
            Thread t1 = new Thread(fenceVolley);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            new Handler().postDelayed(() -> {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i =
                        new Intent(SplashActivity.this, MapsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
                // close this activity
                finish();
            }, SPLASH_TIME_OUT);
        }
//        else{
//
//
//            AlertDialog.Builder builder  = new AlertDialog.Builder(this);
//
//            builder.setPositiveButton("ok",(dialog, which) -> {
//
//                finish();
//            });
//            builder.setTitle("App cannot be used without these Location Permissions" );
//
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permNum = permissions.length + 1;
        int permCount = 0;

        if (requestCode == LOCATION_REQUEST) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                permCount++;

            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBackgroundLocPerm();
                permCount++;
            }

            if (permissions.length == 2) {
                if (permissions[1].equals(android.Manifest.permission.POST_NOTIFICATIONS) &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permCount++;
                }
            }

            if (permCount == permNum) {

//                setupLocationListener();
//                makeFences();

                FenceVolley fenceVolley = new FenceVolley(this);
                Thread t1 = new Thread(fenceVolley);
                t1.start();
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                new Handler().postDelayed(() -> {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i =
                            new Intent(SplashActivity.this, MapsActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
                    // close this activity
                    finish();
                }, SPLASH_TIME_OUT);

            }

        } else if (requestCode == BACKGROUND_LOCATION_REQUEST) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                setupLocationListener();
//                makeFences();
                FenceVolley fenceVolley = new FenceVolley(this);
                Thread t1 = new Thread(fenceVolley);
                t1.start();
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                new Handler().postDelayed(() -> {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i =
                            new Intent(SplashActivity.this, MapsActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out); // new act, old act
                    // close this activity
                    finish();
                }, SPLASH_TIME_OUT);

            }
        }

    }

    private void getBackgroundLocPerm() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            return;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "NEED BASIC PERMS FIRST!", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    BACKGROUND_LOCATION_REQUEST);
        } else {
            Toast.makeText(this, "ALREADY HAS BACKGROUND LOC PERMS", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        ArrayList<String> perms = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            perms.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                perms.add(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!perms.isEmpty()) {
            String[] array = perms.toArray(new String[0]);
            ActivityCompat.requestPermissions(this,
                    array, LOCATION_REQUEST);
            return false;
        }

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}