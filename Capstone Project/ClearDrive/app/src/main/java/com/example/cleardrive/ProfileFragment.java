package com.example.cleardrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cleardrive.databinding.FragmentProfileBinding;
import android.content.Intent;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);


        binding.editText.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
