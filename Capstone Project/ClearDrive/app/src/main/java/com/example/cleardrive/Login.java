package com.example.cleardrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cleardrive.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding activityLoginBinding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        setContentView(view);
        dbHelper = new DatabaseHelper(this);

        // Set click listeners for login and signup buttons
        activityLoginBinding.buttonLogin.setOnClickListener(this);
        activityLoginBinding.tvCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogin) {
            String username = activityLoginBinding.editTextUsername.getText().toString().trim();
            String password = activityLoginBinding.editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                CustomDialog.showErrorDialog(this, "Please enter both username and password");
                return;
            }
            boolean isAuthenticated = dbHelper.checkUser(username, password);
            if (isAuthenticated) {
                startActivity(new Intent(Login.this, HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            } else {
                CustomDialog.showErrorDialog(this, "Invalid username or password");
            }
        } else if (v.getId() == R.id.tvCreateAccount) {
            // Redirect to the signup activity
            startActivity(new Intent(Login.this, SignUpActivity.class));
        }
    }
}
