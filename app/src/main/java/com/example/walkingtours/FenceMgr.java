package com.example.walkingtours;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

class FenceMgr {

    private static final String TAG = "FenceMgr";
    private final MapsActivity mapsActivity;
    private final GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    private FenceData fenceData;


    FenceMgr(final MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        geofencingClient = LocationServices.getGeofencingClient(mapsActivity);

        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(mapsActivity, aVoid -> Log.d(TAG, "onSuccess: removeGeofences"))
                .addOnFailureListener(mapsActivity, e -> {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure: removeGeofences");
                    Toast.makeText(mapsActivity, "Trouble removing existing fences: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    void addFence(FenceData fd) {
        this.fenceData  = fd;

        if (ActivityCompat.checkSelfPermission(mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Geofence geofence = new Geofence.Builder()
                .setRequestId(fd.getId()+","+fd.getAddress())
                .setCircularRegion(
                        fd.getLatLng().latitude,
                        fd.getLatLng().longitude,
                        fd.getRadius())
                .setTransitionTypes(fd.getType())
                .setExpirationDuration(Geofence.NEVER_EXPIRE) //Fence expires after N millis  -or- Geofence.NEVER_EXPIRE
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();

        geofencePendingIntent = getGeofencePendingIntent();

        geofencingClient
                .addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: addGeofences"))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure: addGeofences: " + e.getMessage());
                    Toast.makeText(mapsActivity, "Trouble adding new fence: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getGeofencePendingIntent() {

        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(mapsActivity, GeofenceBroadcastReceiver.class);
//        intent.putExtra("DATA",fenceData);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            geofencePendingIntent = PendingIntent.getBroadcast(
                    mapsActivity, 0, intent,
                    PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(
                    mapsActivity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return geofencePendingIntent;
    }


}

