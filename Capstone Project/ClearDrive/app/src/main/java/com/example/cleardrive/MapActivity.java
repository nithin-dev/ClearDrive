package com.example.cleardrive;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cleardrive.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.cleardrive.databinding.ActivityMapBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);

        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double mechanicLatitude = getIntent().getDoubleExtra("mechanicLatitude", 0);
        double mechanicLongitude = getIntent().getDoubleExtra("mechanicLongitude", 0);
        double ownerLatitude = getIntent().getDoubleExtra("ownerLatitude", 0);
        double ownerLongitude = getIntent().getDoubleExtra("ownerLongitude", 0);

        LatLng mechanicLocation = new LatLng(mechanicLatitude, mechanicLongitude);
        LatLng ownerLocation = new LatLng(ownerLatitude, ownerLongitude);

        Marker mechanicMarker = mMap.addMarker(new MarkerOptions()
                .position(mechanicLocation)
                .title("Mechanic"));
        Marker ownerMarker = mMap.addMarker(new MarkerOptions()
                .position(ownerLocation)
                .title("Vehicle Owner"));

        // Show info window for each marker
        assert mechanicMarker != null;
        mechanicMarker.showInfoWindow();
        ownerMarker.showInfoWindow();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mechanicLocation);
        builder.include(ownerLocation);
        LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(() -> {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        });
        drawRouteAndCalculateDistance(mechanicLocation, ownerLocation);
    }


    private void drawRouteAndCalculateDistance(LatLng origin, LatLng destination) {
        // Calculate distance between origin and destination
        double distanceInMeters = calculateDistance(origin, destination);
        double distanceInKilometers = distanceInMeters / 1000;
        binding.tvDistance.setText(String.format("%.2f km", distanceInKilometers));
        String url = getDirectionsUrl(origin, destination);
        FetchDirectionsTask fetchDirectionsTask = new FetchDirectionsTask();
        fetchDirectionsTask.execute(url);
    }

    private double calculateDistance(LatLng origin, LatLng destination) {
        // Radius of the earth in meters
        final int R = 6371 * 1000;

        double lat1 = origin.latitude;
        double lon1 = origin.longitude;
        double lat2 = destination.latitude;
        double lon2 = destination.longitude;

        // Calculate differences of latitude and longitude
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        String originStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destStr = "destination=" + destination.latitude + "," + destination.longitude;
        String modeStr = "mode=driving";
        String parameters = originStr + "&" + destStr + "&" + modeStr;
        String apiKey = "AIzaSyBpGK9qDkc7q41ktvImskaqNr7_CUboT-Y";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + apiKey;
        return url;
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                data = stringBuilder.toString();
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            drawRoute(result);
        }

        private void drawRoute(String routeData) {
            try {
                JSONObject jsonObject = new JSONObject(routeData);
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPolyline = overviewPolyline.getString("points");

                List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);
                PolylineOptions polylineOptions = new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(8);
                mMap.addPolyline(polylineOptions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private double parseRouteAndCalculateDistance(String routeData) {
        double distance = 0;
        try {
            JSONObject jsonObject = new JSONObject(routeData);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);
            JSONObject distanceObject = leg.getJSONObject("distance");
            distance = distanceObject.getDouble("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return distance;
    }

}
