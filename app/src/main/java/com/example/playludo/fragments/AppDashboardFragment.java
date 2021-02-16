package com.example.playludo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.playludo.databinding.FragmentAppDashboardBinding;
import com.example.playludo.databinding.HomeViewBinding;
import com.example.playludo.databinding.SubmitBidDialogViewBinding;
import com.example.playludo.databinding.SubmitNameDialogViewBinding;
import com.example.playludo.models.HomeScreenModel;
import com.example.playludo.models.User;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Bid;
import com.example.playludo.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.fragments.BidFragment.GAME_IMAGE;
import static com.example.playludo.models.AppConstant.FREE_FIRE;
import static com.example.playludo.models.AppConstant.GAME_TYPE;
import static com.example.playludo.models.AppConstant.LUDO_KING;
import static com.example.playludo.models.AppConstant.POLL_8_BALL;
import static com.example.playludo.models.AppConstant.PUB_G;
import static com.example.playludo.utils.Utils.getUid;

public class AppDashboardFragment extends Fragment {

    public static final String GAME_NAME = "gameName";
    FragmentAppDashboardBinding binding;
    NavController navController;
    AlertDialog optionDialog;

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
        checkForUsername();
    }

    private void checkForUsername() {
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (null != documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user.getName().isEmpty()) {
                    showEnterNameDialog();
                }
            }
        });
    }

    private void showEnterNameDialog() {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.submit_name_dialog_view, null, false);

        final SubmitNameDialogViewBinding genderViewBinding = SubmitNameDialogViewBinding.bind(formElementsView);
        genderViewBinding.btnOk.setOnClickListener(v -> {
            String name = genderViewBinding.etAmount.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                genderViewBinding.etAmount.setError("Name required !!");
            } else {
                optionDialog.dismiss();
                AppUtils.showRequestDialog(requireActivity());
                Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).update("name", name).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AppUtils.hideDialog();
                        Toast.makeText(requireActivity(), "name updated successfully !!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    AppUtils.hideDialog();
                    Toast.makeText(requireActivity(), "try again", Toast.LENGTH_SHORT).show();
                });
            }

        });

        genderViewBinding.btnCancel.setOnClickListener(v -> optionDialog.dismiss());

        // the alert dialog
        optionDialog = new AlertDialog.Builder(requireActivity()).create();
        optionDialog.setView(formElementsView);
        optionDialog.setCancelable(false);
        optionDialog.show();
    }

    private List<HomeScreenModel> getHomeList() {
        List<HomeScreenModel> screenModels = new ArrayList<>();
        screenModels.add(new HomeScreenModel(R.drawable.ludo_image, LUDO_KING));
        screenModels.add(new HomeScreenModel(R.drawable.pool_image, POLL_8_BALL));
        screenModels.add(new HomeScreenModel(R.drawable.pubg_image, PUB_G));
        screenModels.add(new HomeScreenModel(R.drawable.frefire_image, FREE_FIRE));
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
                    bundle.putString(GAME_TYPE, homeScreenModel.getTitle());
                    bundle.putInt(GAME_IMAGE, homeScreenModel.getImage());
                    navController.navigate(R.id.action_appDashboardFragment_to_dashboardFragment, bundle);
                } else if (position == 1) {
                    navController.navigate(R.id.action_appDashboardFragment_to_poolGameListFragment);
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