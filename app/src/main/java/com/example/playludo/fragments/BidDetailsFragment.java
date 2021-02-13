package com.example.playludo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.playludo.R;
import com.example.playludo.databinding.FragmentBidDetailsBinding;

import org.jetbrains.annotations.NotNull;


public class BidDetailsFragment extends Fragment {

    NavController navController;
    FragmentBidDetailsBinding bidDetailsBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bidDetailsBinding = FragmentBidDetailsBinding.inflate(getLayoutInflater());
        return bidDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
    }
}