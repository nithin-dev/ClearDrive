package com.example.cleardrive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cleardrive.databinding.ActivitySearchMechanicsBinding;
import com.example.cleardrive.databinding.QuoteDialogBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchMechanicsActivity extends AppCompatActivity implements MechanicAdapter.OnItemClickListener {

    private ActivitySearchMechanicsBinding activitySearchMechanicsBinding;
    private MechanicAdapter adapter;
    private List<Mechanic> mechanics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchMechanicsBinding = ActivitySearchMechanicsBinding.inflate(getLayoutInflater());
        setContentView(activitySearchMechanicsBinding.getRoot());

        // Initialize mechanics list
        mechanics = new ArrayList<>();

        // Adding mechanics data
        addSampleMechanics();

        // Set up RecyclerView
        adapter = new MechanicAdapter(this, mechanics);
        activitySearchMechanicsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitySearchMechanicsBinding.recyclerView.setAdapter(adapter);
        MechanicAdapter.setListener(this);

        // Set up SearchView
        activitySearchMechanicsBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    // Method to add sample mechanics data
    private void addSampleMechanics() {
        mechanics.add(new Mechanic("Nithin Thomas", "11 Sitler Street", R.drawable.user_mechanic, 50.0, "N2K 1B3", "nithinthomasninan1996@gmail.com", "111"));
        mechanics.add(new Mechanic("George", "Waterloo", R.drawable.user_mechanic, 60.0, "N2L 3G1", "nithinthomasninan1996@gmail.com", "111"));
        mechanics.add(new Mechanic("Ashmi", "Kitchener", R.drawable.user_mechanic, 55.0, "N2M 4J5", "nithinthomasninan1996@gmail.com", "111"));
        mechanics.add(new Mechanic("Millie Bobbie Brown", "22 Elm Street", R.drawable.user_mechanic, 70.0, "N2K 1B3", "n@g.com", "111"));
        mechanics.add(new Mechanic("Sadie Sink", "33 Oak Street", R.drawable.user_mechanic, 65.0, "N2K 1B3", "n@g.com", "111"));
        mechanics.add(new Mechanic("Chandni", "44 Maple Street", R.drawable.user_mechanic, 75.0, "N2K 1B3", "n@g.com", "111"));
        mechanics.add(new Mechanic("Tessa", "55 Pine Street", R.drawable.user_mechanic, 80.0, "N2L 3G1", "n@g.com", "111"));
        mechanics.add(new Mechanic("Neel", "66 Cedar Street", R.drawable.user_mechanic, 85.0, "N2M 4J5", "n@g.com", "111"));
        mechanics.add(new Mechanic("Emma Watson", "77 Birch Street", R.drawable.user_mechanic, 90.0, "N2L 3G1", "n@g.com", "111"));
    }


    // Filter mechanics based on search query
    private void filter(String query) {
        List<Mechanic> filteredMechanics = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            filteredMechanics.addAll(mechanics);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Mechanic mechanic : mechanics) {
                String pinCodeString = String.valueOf(mechanic.getPinCode());
                if (pinCodeString.equals(query) || mechanic.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredMechanics.add(mechanic);
                }
            }
        }
        adapter.setMechanics(filteredMechanics);
    }

    @Override
    public void onItemClick(Mechanic mechanic) {
        showMechanicDetailsDialog(mechanic);
    }

    private void showMechanicDetailsDialog(Mechanic mechanic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        QuoteDialogBinding dialogBinding = QuoteDialogBinding.inflate(LayoutInflater.from(this));
        View dialogView = dialogBinding.getRoot();
        builder.setView(dialogView);

        dialogBinding.tvName.setText("Name: " + mechanic.getName());
        dialogBinding.tvEmail.setText("Email: " + mechanic.getEmail());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogBinding.close.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnCalculateRate.setOnClickListener(v -> {
            String ownerName = dialogBinding.etOwnerName.getText().toString().trim();
            String ownerAddress = dialogBinding.etOwnerAddress.getText().toString().trim();
            String mobileNumber = dialogBinding.etMobileNumber.getText().toString().trim();
            String carMake = dialogBinding.etCarMake.getText().toString().trim();
            String carModel = dialogBinding.etCarModel.getText().toString().trim();
            String carYear = dialogBinding.etCarYear.getText().toString().trim();

            if (TextUtils.isEmpty(ownerName) || TextUtils.isEmpty(ownerAddress) ||
                    TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(carMake) ||
                    TextUtils.isEmpty(carModel) || TextUtils.isEmpty(carYear)) {
                // If any of the fields are empty, display a toast message
                CustomDialog.showErrorDialog(this, "Please fill in all details");
            } else {
                LatLng mechanicLocation = getLatLngFromPinCode(mechanic.getPinCode());
                LatLng ownerLocation = getLatLngFromAddress(ownerAddress);

                if (mechanicLocation != null && ownerLocation != null) {
                    double distanceInKms = calculateDistanceInKms(mechanicLocation.latitude, mechanicLocation.longitude,
                            ownerLocation.latitude, ownerLocation.longitude);
                    double amountInCAD = calculateAmount(distanceInKms); // Calculate the amount based on the distance
                    SharedPreferencesManager.saveAmountInCAD(SearchMechanicsActivity.this, (float) amountInCAD);


                    Log.d("Distance", "Distance in kms: " + distanceInKms);
                    Log.d("Amount", "Amount in CAD: " + amountInCAD);

                    // Save the amount in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("amountInCAD", (float) amountInCAD);

                    QuoteRequest quoteRequest = new QuoteRequest(
                            mechanic.getName(),
                            mechanic.getEmail(),
                            ownerName,
                            ownerAddress,
                            mobileNumber,
                            carMake,
                            carModel,
                            carYear,
                            amountInCAD
                    );
                    editor.putString("quoteRequest", new Gson().toJson(quoteRequest));
                    editor.apply();

                    Intent intent = new Intent(SearchMechanicsActivity.this, MapActivity.class);
                    intent.putExtra("mechanicLatitude", mechanicLocation.latitude);
                    intent.putExtra("mechanicLongitude", mechanicLocation.longitude);
                    intent.putExtra("ownerLatitude", ownerLocation.latitude);
                    intent.putExtra("ownerLongitude", ownerLocation.longitude);
                    startActivity(intent);
                } else {
                    CustomDialog.showErrorDialog(this, "Enter correct ZipCode");

                }
            }
        });
    }


    private double calculateAmount(double distanceInKms) {
        return distanceInKms * 5.0;
    }


    private double calculateDistanceInKms(double latitude, double longitude, double latitude1, double longitude1) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(latitude1 - latitude);
        double lonDistance = Math.toRadians(longitude1 - longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(latitude1))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers

        return distance;
    }


    // Fetch latitude and longitude from mechanic's pin code
    private LatLng getLatLngFromPinCode(String pinCode) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(pinCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                latLng = new LatLng(fetchedAddress.getLatitude(), fetchedAddress.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    // Convert address string to LatLng
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                latLng = new LatLng(fetchedAddress.getLatitude(), fetchedAddress.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }
}
