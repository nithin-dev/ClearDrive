package com.example.cleardrive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cleardrive.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FragmentProfileBinding binding;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setupViews();
        return binding.getRoot();
    }

    private void setupViews() {
        binding.editText.setOnClickListener(v -> openEditProfileActivity());
        binding.backButton.setOnClickListener(this);
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            openHomeActivity();
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }
}
