package com.example.walkingtours;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class FenceData implements Serializable {

    private final String id;

    private final String address;

    private final LatLng latLng;

    private final Float radius;

    private final String description;

    private final String fenceColor;

    private final String image;
    private final int type = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;


    public FenceData(String id, String address, LatLng latLng, Float radius, String description, String fenceColor, String image) {
        this.id = id;
        this.address = address;
        this.latLng = latLng;
        this.radius = radius;
        this.description = description;
        this.fenceColor = fenceColor;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Float getRadius() {
        return radius;
    }

    public String getDescription() {
        return description;
    }

    public String getFenceColor() {
        return fenceColor;
    }

    public String getImage() {
        return image;
    }

    public int getType() {
        return type;
    }
}
