package com.example.cleardrive;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cleardrive.databinding.ActivitySearchMechanicsBinding;
import com.example.cleardrive.databinding.DialogVehicleDetailsBinding;
import com.example.cleardrive.databinding.QuoteDialogBinding;

import java.util.ArrayList;
import java.util.List;


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

        mechanics.add(new Mechanic("Nithin Thomas", "11 sitler street", R.drawable.user_mechanic, 50.0, "N2K1A5", "n@g.com", "111"));
        mechanics.add(new Mechanic("George", "waterloo", R.drawable.user_mechanic, 60.0, "N2R1P6", "n@g.com", "111"));
        mechanics.add(new Mechanic("Ashmi", "kitchener", R.drawable.user_mechanic, 55.0, "12345", "n@g.com", "111"));
        mechanics.add(new Mechanic("John Smith", "22 Elm Street", R.drawable.user_mechanic, 70.0, "N2K1A5", "n@g.com", "111"));
        mechanics.add(new Mechanic("Emily Johnson", "33 Oak Street", R.drawable.user_mechanic, 65.0, "N2K1A5", "n@g.com", "111"));
        mechanics.add(new Mechanic("Michael Brown", "44 Maple Street", R.drawable.user_mechanic, 75.0, "N2K1A5", "n@g.com", "111"));
        mechanics.add(new Mechanic("Sarah Williams", "55 Pine Street", R.drawable.user_mechanic, 80.0, "N2R1P6", "n@g.com", "111"));
        mechanics.add(new Mechanic("David Miller", "66 Cedar Street", R.drawable.user_mechanic, 85.0, "12345", "n@g.com", "111"));
        mechanics.add(new Mechanic("Emma Wilson", "77 Birch Street", R.drawable.user_mechanic, 90.0, "N2R1P6", "n@g.com", "111"));

        adapter = new MechanicAdapter(this, mechanics);
        activitySearchMechanicsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitySearchMechanicsBinding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(mechanic -> showMechanicDetailsDialog(mechanic));

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

    private void filter(String query) {
        List<Mechanic> filteredMechanics = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {

            filteredMechanics.addAll(mechanics);
        } else {
            // Filter mechanics based on search query
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

    @SuppressLint("SetTextI18n")
    private void showMechanicDetailsDialog(Mechanic mechanic) {
        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        QuoteDialogBinding dialogBinding = QuoteDialogBinding.inflate(getLayoutInflater());
        View dialogView = dialogBinding.getRoot();
        builder.setView(dialogView);

        dialogBinding.tvName.setText("Name:"+mechanic.getName());
        dialogBinding.tvEmail.setText("Email:"+mechanic.getEmail());
        dialogBinding.tvPrice.setText("Amount:"+ mechanic.getPrice() +"$");

//        dialogBinding.etOwnerName.setText(mechanic.getOwner().getName());
//        dialogBinding.etOwnerAddress.setText(mechanic.getOwner().getAddress());
//        dialogBinding.etMobileNumber.setText(mechanic.getOwner().getMobileNumber());

//
//        dialogBinding.etCarMake.setText(mechanic.getVehicle().getCarMake());
//        dialogBinding.etCarModel.setText(mechanic.getVehicle().getCarModel());
//        dialogBinding.etCarYear.setText(String.valueOf(mechanic.getVehicle().getCarYear()));

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Close the dialog when the close button is clicked
        dialogBinding.close.setOnClickListener(v -> dialog.dismiss());
    }

}



