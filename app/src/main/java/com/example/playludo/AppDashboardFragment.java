package com.example.playludo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.databinding.FragmentAppDashboardBinding;
import com.example.playludo.databinding.HomeViewBinding;
import com.example.playludo.models.HomeScreenModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppDashboardFragment extends Fragment {

    public static final String GAME_NAME = "gameName";
    FragmentAppDashboardBinding binding;
    NavController navController;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppDashboardBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        binding.homeRec.setAdapter(new HomeAdapter(getHomeList()));
    }

    private List<HomeScreenModel> getHomeList() {
        List<HomeScreenModel> screenModels = new ArrayList<>();
        screenModels.add(new HomeScreenModel(R.drawable.ludo_image, "Ludo King"));
        screenModels.add(new HomeScreenModel(R.drawable.pool_image, "Pool"));
        screenModels.add(new HomeScreenModel(R.drawable.pubg_image, "PubG"));
        screenModels.add(new HomeScreenModel(R.drawable.frefire_image, "FreeFire"));
        return screenModels;
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeVH> {

        List<HomeScreenModel> homeList;

        public HomeAdapter(List<HomeScreenModel> homeList) {
            this.homeList = homeList;
        }

        @NonNull
        @Override
        public HomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            HomeViewBinding viewBinding = HomeViewBinding.inflate(inflater, parent, false);
            return new HomeVH(viewBinding);

        }

        @Override
        public void onBindViewHolder(@NonNull HomeVH holder, int position) {
            HomeScreenModel homeScreenModel = homeList.get(position);
            holder.viewBinding.setHomeModel(homeScreenModel);
            holder.viewBinding.mainLayout.setOnClickListener(v -> {
                if (position == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(GAME_NAME, "Ludo");
                    navController.navigate(R.id.action_appDashboardFragment_to_dashboardFragment, bundle);
                } else Toast.makeText(requireActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }
}