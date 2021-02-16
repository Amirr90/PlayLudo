package com.example.playludo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.playludo.HomeScreen;
import com.example.playludo.R;
import com.example.playludo.databinding.FragmentAddedSuccessfullyBinding;

import org.jetbrains.annotations.NotNull;


public class AddedSuccessfullyFragment extends Fragment {

    FragmentAddedSuccessfullyBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddedSuccessfullyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnOk.setOnClickListener(v -> HomeScreen.getInstance().onSupportNavigateUp());
    }
}