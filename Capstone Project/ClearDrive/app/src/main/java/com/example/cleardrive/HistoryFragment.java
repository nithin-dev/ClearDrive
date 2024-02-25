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

import com.example.cleardrive.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private FragmentHistoryBinding binding;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        setupViews();
        return binding.getRoot();
    }

    private void setupViews() {
        binding.backButton.setOnClickListener(this);

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
