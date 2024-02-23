package com.example.cleardrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.cleardrive.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding activityHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = activityHomeBinding.getRoot();
        setContentView(view);

        // Load default fragment
        loadFragment(new HomeFragment());

        // Bottom navigation item click listener
        activityHomeBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profileFragment) {
                fragment = new ProfileFragment();
            } else if (item.getItemId() == R.id.historyFragment) {
                fragment = new HistoryFragment();
            } else if (item.getItemId() == R.id.aboutUsFragment) {
                fragment = new AboutUsFragment();
            }
            return loadFragment(fragment);
        });
    }

    // Method to load a fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
