package com.example.cleardrive;

import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cleardrive.databinding.ActivityLoginBinding;
import com.example.cleardrive.databinding.FragmentEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {
    FragmentEditProfileBinding fragmentEditProfileBinding;
     DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(getLayoutInflater());
        View view = fragmentEditProfileBinding.getRoot();
        setContentView(view);
        dbHelper = new DatabaseHelper(this);

        UserModel userModel = dbHelper.getUserData();
        if (userModel != null) {
            fragmentEditProfileBinding.editTextFirstName.setText(userModel.getFirstName());
            fragmentEditProfileBinding.editTextLastName.setText(userModel.getLastName());
            fragmentEditProfileBinding.editTextMobile.setText(userModel.getMobile());
            fragmentEditProfileBinding.editTextEmail.setText(userModel.getEmail());
            fragmentEditProfileBinding.editTextSignupUsername.setText(userModel.getUsername());
            fragmentEditProfileBinding.editTextSignupPassword.setText(userModel.getPassword());
        }
    }
}
