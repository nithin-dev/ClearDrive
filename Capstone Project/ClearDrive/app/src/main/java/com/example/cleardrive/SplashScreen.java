package com.example.cleardrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            startPaymentActivity();

            if (data != null && "https".equals(data.getScheme()) && "mail.com".equals(data.getHost()) && "/PaymentActivity".equals(data.getPath())) {
                // Intent contains deep link to payment page, navigate to the payment activity
                startPaymentActivity();
            }
        }

        new Handler().postDelayed(() -> {
            // Start the login activity
            startActivity(new Intent(SplashScreen.this, Login.class));
            finish();
        }, 3000);
    }

    private void startPaymentActivity() {
        Intent paymentIntent = new Intent(this, PaymentActivity.class);
        startActivity(paymentIntent);
    }
}
