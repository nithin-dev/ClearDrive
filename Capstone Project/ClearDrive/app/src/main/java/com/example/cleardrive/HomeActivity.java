package com.example.cleardrive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.cleardrive.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    ActivityHomeBinding activityHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        setListeners();

        // Bottom navigation item click listener
        activityHomeBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.profileFragment) {
                fragment = new ProfileFragment();
                hideGetStartedButton();
            } else if (item.getItemId() == R.id.historyFragment) {
                fragment = new HistoryFragment();
                hideGetStartedButton();
            } else if (item.getItemId() == R.id.aboutUsFragment) {
                fragment = new AboutUsFragment();
                hideGetStartedButton();
            }
            loadFragmentWithAnimation(fragment, null);
            return true;
        });
    }

    private void hideGetStartedButton() {
        activityHomeBinding.btnGetStarted.setVisibility(View.GONE);
    }

    private void setListeners() {
        activityHomeBinding.btnGetStarted.setOnClickListener(this);
    }

    // Method to load a fragment with animation
    private void loadFragmentWithAnimation(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.nav_host_fragment, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGetStarted) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), SearchMechanicsActivity.class);
                startActivity(intent);
            }, 200);
        }
    }
}
