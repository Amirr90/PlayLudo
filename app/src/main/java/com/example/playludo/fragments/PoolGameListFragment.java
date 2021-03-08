package com.example.playludo.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playludo.R;
import com.example.playludo.databinding.FragmentPoolGameListBinding;
import com.example.playludo.databinding.HomeViewBinding;
import com.example.playludo.models.HomeScreenModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.playludo.fragments.BidFragment.GAME_IMAGE;
import static com.example.playludo.utils.AppConstant.ALL_INDIRECT;
import static com.example.playludo.utils.AppConstant.ALL_INDIRECT_BLACK_DOUBLE;
import static com.example.playludo.utils.AppConstant.GAME_TYPE;
import static com.example.playludo.utils.AppConstant.SIMPLE_JAKARTHA;
import static com.example.playludo.utils.AppConstant.VENICE_RULE;


public class PoolGameListFragment extends Fragment {
    private static final String TAG = "PoolGameListFragment";


    FragmentPoolGameListBinding binding;

    NavController navController;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPoolGameListBinding.inflate(getLayoutInflater());
        initAds();
        return binding.getRoot();
    }

    private void setBannerAdd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView2.loadAd(adRequest);
        binding.adView3.loadAd(adRequest);
        binding.adView6.loadAd(adRequest);
        binding.adView5.loadAd(adRequest);
        binding.adView4.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d(TAG, "onAdFailedToLoad: ");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
            }
        });
    }

    private void initAds() {
        MobileAds.initialize(requireActivity(), initializationStatus -> Log.d(TAG, "onInitializationComplete: " + initializationStatus));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        binding.recPoolGame.setAdapter(new HomeAdapter(getHomeList()));
        setBannerAdd();

    }

    private List<HomeScreenModel> getHomeList() {
        List<HomeScreenModel> screenModels = new ArrayList<>();
        screenModels.add(new HomeScreenModel(R.drawable.pool_jakarta, SIMPLE_JAKARTHA));
        screenModels.add(new HomeScreenModel(R.drawable.pool_jakarta, VENICE_RULE));
        screenModels.add(new HomeScreenModel(R.drawable.pool_jakarta, ALL_INDIRECT));
        screenModels.add(new HomeScreenModel(R.drawable.pool_jakarta, ALL_INDIRECT_BLACK_DOUBLE));
        return screenModels;
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeVH> {

        List<HomeScreenModel> homeList;

        public HomeAdapter(List<HomeScreenModel> homeList) {
            this.homeList = homeList;
        }

        @NonNull
        @Override
        public HomeAdapter.HomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            HomeViewBinding viewBinding = HomeViewBinding.inflate(inflater, parent, false);
            return new HomeAdapter.HomeVH(viewBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull HomeAdapter.HomeVH holder, int position) {
            HomeScreenModel homeScreenModel = homeList.get(position);
            holder.viewBinding.setHomeModel(homeScreenModel);
            holder.viewBinding.mainLayout.setOnClickListener(v -> {
                if (position == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(GAME_TYPE, homeScreenModel.getTitle());
                    bundle.putInt(GAME_IMAGE, homeScreenModel.getImage());
                    navController.navigate(R.id.action_poolGameListFragment_to_dashboardFragment, bundle);
                } else showComingSoonDialog();
            });
        }

        @Override
        public int getItemCount() {
            if (null == homeList)
                homeList = new ArrayList<>();
            return homeList.size();
        }

        public class HomeVH extends RecyclerView.ViewHolder {
            HomeViewBinding viewBinding;

            public HomeVH(@NonNull HomeViewBinding viewBinding) {
                super(viewBinding.getRoot());
                this.viewBinding = viewBinding;
            }
        }
    }

    private void showComingSoonDialog() {
        new AlertDialog.Builder(requireActivity()).setTitle("Coming Soon").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }
}