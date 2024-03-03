package com.example.cleardrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cleardrive.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpBinding activitySignUpBinding;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = activitySignUpBinding.getRoot();
        setContentView(view);
        dbHelper = new DatabaseHelper(this);
        setListeners();
    }

    private void setListeners() {
        activitySignUpBinding.buttonSignup.setOnClickListener(this);
        activitySignUpBinding.tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSignup) {
            String firstName = activitySignUpBinding.editTextFirstName.getText().toString().trim();
            String lastName = activitySignUpBinding.editTextLastName.getText().toString().trim();
            String mobile = activitySignUpBinding.editTextMobile.getText().toString().trim();
            String email = activitySignUpBinding.editTextEmail.getText().toString().trim();
            String username = activitySignUpBinding.editTextSignupUsername.getText().toString().trim();
            String password = activitySignUpBinding.editTextSignupPassword.getText().toString().trim();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                    TextUtils.isEmpty(mobile) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                CustomDialog.showErrorDialog(this, "Please fill in all the details");
                return;
            }

            long rowId = dbHelper.addUser(firstName, lastName, mobile, email, username, password);
            if (rowId != -1) {
                CustomDialog.showSuccessDialog(this, "Signup successful");
                startActivity(new Intent(SignUpActivity.this, Login.class));

            } else {
                CustomDialog.showErrorDialog(this, "Error occurred,please try again !");
            }
        }
        if (v.getId()==R.id.tvLogin){
            startLoginActivityWithAnimation(new Intent(this, Login.class));

        }
    }

    private void startLoginActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.flip_in, R.anim.flip_out);
    }
}