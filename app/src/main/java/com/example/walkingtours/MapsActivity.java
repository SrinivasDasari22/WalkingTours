package com.example.walkingtours;

import androidx.fragment.app.FragmentActivity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.walkingtours.databinding.ActivityMapsBinding;
import com.example.walkingtours.databinding.ActivityMapsBinding;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.christopherhield.googlemapwithgeofences.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng prevLatLng;
    private ActivityMapsBinding binding;

    LocationManager locationManager ;
    LocationListener locationListener;

    private static HashMap<String,Circle> circleHash = new HashMap<>();


    private static boolean travelPathFlag,geoFenceFlag;

    private static final String TAG = "MapsActivity";
//    private GoogleMap mMap;
    private FenceMgr fenceMgr;
    private static final int LOCATION_REQUEST = 111;
    private static final int BACKGROUND_LOCATION_REQUEST = 222;
    private final List<PatternItem> pattern = Collections.singletonList(new Dot());
    private final ArrayList<LatLng> latLonHistory = new ArrayList<>();
    private Polyline llHistoryPolyline;
    private Marker carMarker;
    private Geocoder geocoder;

    private boolean showAddress,tourPathFlag;

    private static ArrayList<FenceData> fence_List = new ArrayList<>();
    private  ArrayList<LatLng> pathList = new ArrayList<>();


    private static final float zoomLevel = 15f;

//    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        address = findViewById(R.id.address);
        setContentView(binding.getRoot());
        prevLatLng = new LatLng(0.00,0.00);

//        FenceVolley fenceVolley = new FenceVolley(this);
//        Thread t1 = new Thread(fenceVolley);
//        t1.start();
//        try {
//            t1.wait();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        fence_List.clear();
        fence_List = FenceVolley.getFenceList();

        pathList.clear();
        pathList = FenceVolley.getPath_List();

//        Thread workerThread = new Thread(new FenceVolley());
//        FenceVolley.downloadFence(this);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        changeScreen();

        geocoder = new Geocoder(this, Locale.getDefault());
        initMap();

        //check boxes

        //address
        CheckBox checkBox = findViewById(R.id.s_address);
        showAddress = true;
        checkBox.setChecked(showAddress);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showAddress = true;
                    binding.address.setText("");
                    setupLocationListener();
                    // perform action when checkbox is checked
                } else {
                    showAddress = false;
                    setupLocationListener();

                    // perform action when checkbox is unchecked
                }
            }
        });

        //address
        CheckBox checkBox2 = findViewById(R.id.s_travelPath);
        travelPathFlag = true;
        checkBox2.setChecked(travelPathFlag);

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    travelPathFlag = true;
                    // perform action when checkbox is checked
                } else {
                    travelPathFlag = false;

                    // perform action when checkbox is unchecked
                }
            }
        });



    }


    public void initMap() {

        fenceMgr = new FenceMgr(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


//        if (!fence_List.isEmpty()) {\


        setupLocationListener();
        System.out.println("fence list: "+fence_List);

        //geoFence

        makeFences();
        CheckBox checkBox3 = findViewById(R.id.s_geofence);
        geoFenceFlag = true;
        checkBox3.setChecked(geoFenceFlag);

        ArrayList<FenceData> temp_fence_List = new ArrayList<>();
        temp_fence_List.addAll(fence_List);




        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    geoFenceFlag = true;

                    makeFences();


                    // perform action when checkbox is checked
                } else {

                    geoFenceFlag = false;
                    removeFence();
                    // perform action when checkbox is unchecked
                }
            }
        });










        //travel path


        CheckBox checkBox1 = findViewById(R.id.s_tourPath);

        System.out.println("pathist : "+ pathList);
        PolylineOptions pl = new PolylineOptions();
        //Color.parseColor("#FFD700")
        pl.addAll(pathList)
                .color(Color.parseColor("#FFD700"))
                .width(10)
                .startCap(new RoundCap())
                .endCap(new RoundCap());
        final Polyline[] polyline = {mMap.addPolyline(pl)};
        tourPathFlag = true;
        checkBox1.setChecked(tourPathFlag);

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
//                    tourPathFlag = true;

                    polyline[0].remove();
//                    tourPath();
                    // perform action when checkbox is checked
                } else{
                    polyline[0] = mMap.addPolyline(pl);

                }
            }
        });







    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
    }

    private void removeFence() {

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);



        ArrayList<String> requestIdList = new ArrayList<>();


        for(FenceData fenceData: fence_List){

            requestIdList.add(fenceData.getId());
            Circle circle = circleHash.remove(fenceData.getId());
            if (circle != null) {
                circle.remove();
            }
        }
        geofencingClient.removeGeofences(requestIdList);


//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//
//                        Log.d(TAG, "onSuccess: "+requestId+" removed Successfully");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Failed to remove geofences
//                        Log.d(TAG, "onFailure:  Failed to remove GeoFence");
//                    }
//                });
//
//        for()
//        Circle circle = circleHash.remove(requestId);
//        if (circle != null) {
//            circle.remove();
//        }


    }


    public void changeScreen() {
            hideSystemUI();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
                hideSystemUI();
        }
    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }






    public void updateLocation(Location location) {


        getAddress(location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latLonHistory.add(latLng);// Add the LL to our location history



        if (llHistoryPolyline != null) {
            llHistoryPolyline.remove(); // Remove old polyline
        }

        if (latLonHistory.size() == 1) { // First update
            mMap.addMarker(new MarkerOptions().alpha(0.5f).position(latLng).title("My Origin"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            return;
        }



        if (latLonHistory.size() > 1) { // Second (or more) update
            PolylineOptions polylineOptions = new PolylineOptions();

            for (LatLng ll : latLonHistory) {
                polylineOptions.add(ll);
            }


            if(travelPathFlag){
                llHistoryPolyline = mMap.addPolyline(polylineOptions);
                llHistoryPolyline.setEndCap(new RoundCap());
                llHistoryPolyline.setWidth(8);
                llHistoryPolyline.setColor(Color.GREEN);
            }




//            llHistoryPolyline = mMap.addPolyline(polylineOptions);
//            llHistoryPolyline.setEndCap(new RoundCap());
//            llHistoryPolyline.setWidth(8);
//            llHistoryPolyline.setColor(Color.GREEN);


            float r = getRadius();
            if (r > 0) {
                int i = getMarkerDirection(prevLatLng,latLng);
                prevLatLng = latLng;
                Bitmap icon;
                if(i==1){
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_up);
                } else if (i==2) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_right);

                }else if (i==3) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_down);

                } else{
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_left);
                }

                Bitmap resized = Bitmap.createScaledBitmap(icon, (int) r, (int) r, false);

                BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(resized);

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.icon(iconBitmap);
                options.rotation(location.getBearing());
                options.anchor(0.5f,0.5f);

                if (carMarker != null) {
                    carMarker.remove();
                }

                carMarker = mMap.addMarker(options);
            }
        }
        Log.d(TAG, "updateLocation: " + mMap.getCameraPosition().zoom);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        sumIt();
    }


    public int getMarkerDirection(LatLng prevLocation, LatLng newLocation){

        double lonDiff = newLocation.longitude - prevLocation.longitude;
        double latDiff = newLocation.latitude - prevLocation.latitude;
        double angle = Math.atan2(lonDiff,latDiff); angle = Math.toDegrees(angle) + 90;
        System.out.println(angle);

        if(angle>=45 && angle<135){
            return 1;
            
        } else if (angle>=135 && angle<225) {
            return  2;
        }else if (angle>=225 && angle<315) {
            return  3;
        } else{
            return  4;
        }

    }

    private void getAddress(Location location) {
        try {
            List<Address> addresses;



            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            String addressString =  addresses.get(0).getAddressLine(0);

            if(showAddress){
                binding.address.setText(addressString);
            } else{
                binding.address.setText("");
            }



            Log.d(TAG, "doLatLon: " + addresses.get(0));
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void sumIt() {
        double sum = 0;
        LatLng last = latLonHistory.get(0);
        for (int i = 1; i < latLonHistory.size(); i++) {
            LatLng current = latLonHistory.get(i);
            sum += SphericalUtil.computeDistanceBetween(current, last);
            last = current;
        }
        Log.d(TAG, "sumIt: " + String.format("%.3f km", sum/1000.0));

    }

    private float getRadius() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        float z = mMap.getCameraPosition().zoom;
        float factor = (float) ((35.0 / 2.0 * z) - (355.0 / 2.0));
        float multiplier = ((7.0f / 7200.0f) * screenWidth) - (1.0f / 20.0f);
        float radius = factor * multiplier;
        return radius;


//        float z = mMap.getCameraPosition().zoom;
//        return 15f * z - 145f;
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        int permNum = permissions.length + 1;
//        int permCount = 0;
//
//        if (requestCode == LOCATION_REQUEST) {
//
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
//                permCount++;
//
//            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getBackgroundLocPerm();
//                permCount++;
//            }
//
//            if (permissions.length == 2) {
//                if (permissions[1].equals(Manifest.permission.POST_NOTIFICATIONS) &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    permCount++;
//                }
//            }
//
//            if (permCount == permNum) {
//                setupLocationListener();
//                makeFences();
//            }
//
//        }
//        else if (requestCode == BACKGROUND_LOCATION_REQUEST) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                setupLocationListener();
//                makeFences();
//            }
//        }
//    }
//
//
//    private void getBackgroundLocPerm() {
//
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
//            return;
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "NEED BASIC PERMS FIRST!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                    BACKGROUND_LOCATION_REQUEST);
//        } else {
//            Toast.makeText(this, "ALREADY HAS BACKGROUND LOC PERMS", Toast.LENGTH_LONG).show();
//        }
//    }



    private void setupLocationListener() throws SecurityException{

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocListener(this);

        //minTime	    long: minimum time interval between location updates, in milliseconds
        //minDistance	float: minimum distance between location updates, in meters
        if (locationManager != null)
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

//    private boolean checkPermission() {
//        ArrayList<String> perms = new ArrayList<>();
//
//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            perms.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this,
//                    android.Manifest.permission.POST_NOTIFICATIONS) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                perms.add(android.Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//
//        if (!perms.isEmpty()) {
//            String[] array = perms.toArray(new String[0]);
//            ActivityCompat.requestPermissions(this,
//                    array, LOCATION_REQUEST);
//            return false;
//        }
//
//        return ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }

    private void makeFences() {

        for(FenceData fd : fence_List){
            addFence(fd);
        }


        // Hard-coded test fences
//        LatLng ll = new LatLng(41.8754, -87.6242);
//        addFence(ll, 100.0);
//
//        LatLng ll2 = new LatLng(41.8794, -87.6242);
//        addFence(ll2, 80.0);
    }

    private void addFence(FenceData fd) {
//        String id = UUID.randomUUID().toString();
//        FenceData fd = new FenceData(id, latLng, radius);
        fenceMgr.addFence(fd);

        int color = Color.parseColor(fd.getFenceColor());
        // Just to see the fence

//        ColorDrawable color
//                = new ColorDrawable(Color.parseColor(fd.getFenceColor()));
//        int line = ContextCompat.getColor(this,R.color.purple_500 );
        int fill = ColorUtils.setAlphaComponent(color, 50);

        CircleOptions circleOptions = new CircleOptions()
                .center(fd.getLatLng())
                .radius(fd.getRadius())
                .strokePattern(pattern)
                .strokeColor(color)
                .fillColor(fill);
        Circle circle = mMap.addCircle(circleOptions);
        circleHash.put(fd.getId(), circle);

//        mMap.addCircle(new CircleOptions()
//                .center(fd.getLatLng())
//                .radius(fd.getRadius())
//                .strokePattern(pattern)
//                .strokeColor(color)
//                .fillColor(fill));
    }

    public void acceptAlerts(ArrayList<FenceData> fenceListIn) throws ParserConfigurationException, IOException, SAXException {


        fence_List.clear();

        fence_List.addAll(fenceListIn);

    }


}