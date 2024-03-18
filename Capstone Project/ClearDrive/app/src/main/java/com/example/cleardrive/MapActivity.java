package com.example.cleardrive;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final double RATE_PER_KILOMETER = 5.0;

    private GoogleMap mMap;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

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

    private void setListeners() {
        binding.btnRequestAppointment.setOnClickListener(this);
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

        double distanceInMeters = calculateDistance(mechanicLocation, ownerLocation);
        double distanceInKms = distanceInMeters / 1000;
        double amountInCAD = calculateAmount(distanceInKms);

        showDialogWithDistanceAndAmount(distanceInKms, amountInCAD);

        drawRouteAndCalculateDistance(mechanicLocation, ownerLocation);
    }

    private void showDialogWithDistanceAndAmount(double distanceInKms, double amountInCAD) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_curved, null);
        builder.setView(dialogView);

        TextView textViewMessage = dialogView.findViewById(R.id.textViewMessage);
        double roundedDistance = Math.round(distanceInKms * 100.0) / 100.0;
        double roundedAmount = Math.round(amountInCAD * 100.0) / 100.0;
        String message = "Distance: " + roundedDistance + " km\n";
        message += "Amount: CAD " + roundedAmount;
        textViewMessage.setText(message);

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();

        // Customize dialog appearance
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private double calculateAmount(double distanceInKms) {
        return distanceInKms * RATE_PER_KILOMETER;
    }

    private void drawRouteAndCalculateDistance(LatLng origin, LatLng destination) {
        double distanceInMeters = calculateDistance(origin, destination);
        double distanceInKms = distanceInMeters / 1000;
        String url = getDirectionsUrl(origin, destination);
        FetchDirectionsTask fetchDirectionsTask = new FetchDirectionsTask();
        fetchDirectionsTask.execute(url);
    }

    private double calculateDistance(LatLng origin, LatLng destination) {
        final int R = 6371 * 1000;

        double lat1 = origin.latitude;
        double lon1 = origin.longitude;
        double lat2 = destination.latitude;
        double lon2 = destination.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

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

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnRequestAppointment){
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String quoteRequestJson = sharedPreferences.getString("quoteRequest", "");
            QuoteRequest quoteRequest = new Gson().fromJson(quoteRequestJson, QuoteRequest.class);
            if (quoteRequest != null) {
                createAndSendPdf(quoteRequest);
                saveQuoteRequestData(quoteRequest);

            } else {
                Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveQuoteRequestData(QuoteRequest quoteRequest) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String quoteRequestJson = gson.toJson(quoteRequest);
        editor.putString("quoteRequest", quoteRequestJson);
        editor.apply();
    }

    private void createAndSendPdf(QuoteRequest quoteRequest) {
        try {
            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "quote_request.pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, Files.newOutputStream(pdfFile.toPath()));
            document.open();

            // Add QuoteRequest data to the PDF
            Font headingFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph heading = new Paragraph("Quote Request Details", headingFont);
            document.add(heading);
//            Paragraph mechanicName = new Paragraph("Mechanic Name: " + quoteRequest.getMechanicName());
//            document.add(mechanicName);
//            Paragraph mechanicEmail = new Paragraph("Mechanic Email: " + quoteRequest.getMechanicEmail());
//            document.add(mechanicEmail);
            Paragraph ownerName = new Paragraph("Owner Name: " + quoteRequest.getOwnerName());
            document.add(ownerName);
            Paragraph ownerAddress = new Paragraph("Owner Address: " + quoteRequest.getOwnerAddress());
            document.add(ownerAddress);
            Paragraph mobileNumber = new Paragraph("Mobile Number: " + quoteRequest.getMobileNumber());
            document.add(mobileNumber);
            Paragraph carMake = new Paragraph("Car Make: " + quoteRequest.getCarMake());
            document.add(carMake);
            Paragraph carModel = new Paragraph("Car Model: " + quoteRequest.getCarModel());
            document.add(carModel);
            Paragraph carYear = new Paragraph("Car Year: " + quoteRequest.getCarYear());
            document.add(carYear);
            Paragraph amountInCAD = new Paragraph("Amount in CAD: " + quoteRequest.getAmountInCAD());
            document.add(amountInCAD);

            document.close();

            // Send the PDF via email
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            String deepLink = "https://gmail.com/PaymentActivity";
            String emailContent = "Please find the details attached.\n\nDo you accept these quote?\n1. ACCEPTED\n2. REJECTED\n\n" + deepLink;

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Quote Request Details PDF");
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{quoteRequest.getMechanicEmail()});
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    }
}
