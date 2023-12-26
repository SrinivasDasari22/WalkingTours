package com.example.walkingtours;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

public class FenceVolley implements Runnable{

    private static final String fenceUrl =
            "https://www.christopherhield.com/data/WalkingTourContent.json";
    private static final String TAG = "AlertVolley";

    public static HashMap<String,FenceData> fenceDataHashMap = new HashMap<>();

    private SplashActivity mapsActivity;
    private static final ArrayList<FenceData> fence_List = new ArrayList<>();
//    private static SharedPreferences.Editor ctaCachedDataEditor;
//    private static boolean usedCache = false;
    private static final ArrayList<LatLng> path_List = new ArrayList<>();



    public FenceVolley(SplashActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public  void run() {


        RequestQueue queue = Volley.newRequestQueue(mapsActivity);

        Uri.Builder buildURL = Uri.parse(fenceUrl).buildUpon();

        String urlToUse = buildURL.build().toString();
        System.out.println("url::"+urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = FenceVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }




    private  void handleSuccess(String responseText
                                      ) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONArray jsonArray = response.getJSONArray("fences");
//        JSONArray routes = jsonObject.getJSONArray("Alert");
        fence_List.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject alert = jsonArray.getJSONObject(i);
            String id = alert.getString("id");
            String address = alert.getString("address");

            LatLng ll = new LatLng(Double.parseDouble(alert.getString("latitude")),Double.parseDouble(alert.getString("longitude")));
//            String latitude = alert.getString("latitude");
//            String longitude = alert.getString("longitude");
            Float radius = Float.valueOf(alert.getString("radius"));
            String description = alert.getString("description");
            String fenceColor = alert.getString("fenceColor");

            String image = alert.getString("image");

            FenceData fenceData = new FenceData(id,address,ll,radius,description,fenceColor,image);

            fence_List.add(fenceData);
            fenceDataHashMap.put(id,fenceData);
            System.out.println("Fence added....");

        }

        JSONArray jsonArray1 = response.getJSONArray("path");
        path_List.clear();
        for (int i = 0; i < jsonArray1.length(); i++) {
            String loc = jsonArray1.getString(i);



            String[] latLong = loc.split(", ");


            LatLng ll = new LatLng(Double.parseDouble(latLong[1]),Double.parseDouble(latLong[0]));



            path_List.add(ll);

            System.out.println("Fence added....");

        }




//        mapsActivity.runOnUiThread(() -> {
//            try {
//                mapsActivity.acceptAlerts(fence_List);
//            } catch (ParserConfigurationException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (SAXException e) {
//                throw new RuntimeException(e);
//            }
//        });

    }

    public static ArrayList<FenceData> getFenceList(){
        return fence_List;
    }

    public static ArrayList<LatLng> getPath_List(){
        return path_List;
    }
    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }


}
