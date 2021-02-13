package com.example.playludo.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class CustomLoadImages {
    @BindingAdapter("android:loadCustomImage")
    public static void loadHomeRecImage(ImageView imageView, int imageUrl) {
        imageView.setImageResource(imageUrl);
    }
}
