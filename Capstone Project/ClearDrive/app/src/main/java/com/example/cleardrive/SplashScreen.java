package com.example.cleardrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.cleardrive.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding activitySplashScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashScreenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = activitySplashScreenBinding.getRoot();
        setContentView(view);

        new Handler().postDelayed(() -> {
            // Start the login activity
            startActivity(new Intent(SplashScreen.this, Login.class));
            finish();
        }, 3000);
    }
}
