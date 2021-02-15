package com.example.playludo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.playludo.databinding.FragmentBidDetailsBinding;
import com.example.playludo.models.BidModel;
import com.example.playludo.models.TransactionModel;
import com.example.playludo.models.User;
import com.example.playludo.utils.AppUtils;
import com.example.playludo.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.playludo.fragments.AddCreditsFragment.USERS_QUERY;
import static com.example.playludo.fragments.BidFragment.BID_ID;
import static com.example.playludo.fragments.BidFragment.BID_QUERY;
import static com.example.playludo.utils.Bid.BID_STATUS;
import static com.example.playludo.utils.Utils.getFireStoreReference;
import static com.example.playludo.utils.Utils.getUid;


public class BidDetailsFragment extends Fragment {
    private static final String TAG = "BidDetailsFragment";
    public static final String BID_ACCEPT_TIMESTAMP = "bidAcceptTimestamp";
    public static final String BID_ACCEPTED_BY = "bidAcceptBy";
    public static final String BID_ACCEPTER_NAME = "bidAccepterName";
    public static final String GAME_STATUS = "gameStatus";
    public static final String TRANSACTIONS = "Transactions";
    public static final String TYPE_DEBIT = "debit";

    NavController navController;
    FragmentBidDetailsBinding bidDetailsBinding;
    String bidId = null;
    ProgressDialog progressDialog;

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


        progressDialog = new ProgressDialog(requireActivity());

        if (getArguments() == null)
            return;

        AppUtils.showRequestDialog(requireActivity());
        bidId = getArguments().getString(BID_ID);
        getBidData();

        bidDetailsBinding.btnAccept.setOnClickListener(v -> showAlertDialog(bidDetailsBinding.tvBidingAmount.getText().toString()));
        bidDetailsBinding.btnWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Clicked !!", Toast.LENGTH_SHORT).show();
                selectImage();
            }
        });
    }

    private void selectImage() {

        ImagePicker.Companion.with(this)
                //.crop(4f, 4f)                    //Crop image(Optional), Check Customization for more option
                .compress(512)//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (null != data) {
                Uri uri = data.getData();
                bidDetailsBinding.imageView3.setImageURI(uri);
                Log.d(TAG, "onActivityResult: Uri" + data.getData());
                uploadImageToFirebase(uri);
            } else Log.d(TAG, "onActivityResult: No Data ");
        } else Log.d(TAG, "onActivityResult: resultCode not matched");


    }


    private void uploadImageToFirebase(Uri uri) {


        progressDialog.show();
        progressDialog.setMessage("Uploading  image,please wait...");
        final DocumentReference uploadImageUriRef = getFireStoreReference().collection(BID_QUERY).document(bidId);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();


        final String STORAGE_PATH = "bid_image/" + getUid() + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference spaceRef = storageRef.child(STORAGE_PATH);

        Bitmap bitmap2 = ((BitmapDrawable) bidDetailsBinding.imageView3.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] compressData = baos.toByteArray();
        UploadTask uploadTask = spaceRef.putBytes(compressData);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressDialog.setProgress((int) progress);
        }).addOnSuccessListener(taskSnapshot -> storageRef.child(STORAGE_PATH).getDownloadUrl().addOnSuccessListener(uri1 -> {
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("image", uri1.toString());
            imageMap.put("resultUploadedBy", getUid());
            uploadImageUriRef.update(imageMap).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), " Image Uploaded", Toast.LENGTH_SHORT).show();
                getBidData();
            });
        })).addOnCanceledListener(() -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "Upload cancelled, try again", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(requireActivity(), "failed to upload Image " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void showAlertDialog(String amount) {
        new AlertDialog.Builder(requireActivity()).setMessage("Do You want to accept Bid of " + amount + " ??")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    acceptBid();
                }).setNegativeButton("No", (dialog, which) -> {

        }).show();

    }

    private void acceptBid() {
        AppUtils.showRequestDialog(requireActivity(), false);
        String bidAmount = bidDetailsBinding.textView16.getText().toString();
        //checking wallet amount
        Utils.getFireStoreReference().collection(USERS_QUERY).document(getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (null == documentSnapshot) {
                    Log.d(TAG, "onSuccess: null");
                    Toast.makeText(requireActivity(), "User not found", Toast.LENGTH_SHORT).show();
                    AppUtils.hideDialog();
                    return;
                }

                User user = documentSnapshot.toObject(User.class);
                if (null != user) {
                    if (user.getCredits() >= Integer.parseInt(bidAmount)) {
                        initBatchToAccecptBid(bidAmount);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "Failed to get User details", Toast.LENGTH_SHORT).show();
        });

    }

    private void initBatchToAccecptBid(String bidAmount) {
        FirebaseFirestore db = Utils.getFireStoreReference();
        // Get a new write batch
        WriteBatch batch = db.batch();

        // deduct BidAmount From User's Account
        DocumentReference userRef = db.collection(USERS_QUERY).document(getUid());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("credits", FieldValue.increment(-Long.parseLong(bidAmount)));
        userMap.put("onHold", FieldValue.increment(Long.parseLong(bidAmount)));
        batch.update(userRef, userMap);

        // Update the Bids Status
        DocumentReference sfRef = db.collection(BID_QUERY).document(bidId);
        batch.update(sfRef, getBidUpdateMap());

        // Create Transaction of User
        DocumentReference transRef = db.collection(TRANSACTIONS).document();
        batch.set(transRef, getTransactionModel(bidAmount));

        // Commit the batch
        batch.commit().addOnSuccessListener(aVoid -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "Bid Accepted !!", Toast.LENGTH_SHORT).show();
            getBidData();
        }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "something went wrong !!", Toast.LENGTH_SHORT).show();
        });
    }

    private TransactionModel getTransactionModel(String bidAmount) {
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(bidAmount);
        transactionModel.setBidId(bidId);
        transactionModel.setUid(getUid());
        transactionModel.setTimestamp(System.currentTimeMillis());
        transactionModel.setType(TYPE_DEBIT);
        return transactionModel;
    }

    private Map<String, Object> getBidUpdateMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(BID_STATUS, true);
        map.put(BID_ACCEPT_TIMESTAMP, System.currentTimeMillis());
        map.put(BID_ACCEPTED_BY, getUid());
        map.put(BID_ACCEPTER_NAME, "Demo");
        map.put(GAME_STATUS, "onGoing");
        return map;
    }

    private void getBidData() {
        Utils.getFireStoreReference().collection(BID_QUERY).document(bidId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    AppUtils.hideDialog();
                    Log.d(TAG, "getBidData: " + documentSnapshot);
                    if (null == documentSnapshot) {
                        return;
                    }
                    BidModel bidModel = documentSnapshot.toObject(BidModel.class);
                    bidDetailsBinding.setBidModel(bidModel);


                    if (null != bidModel) {
                        bidDetailsBinding.tvBidingAmount.setText(AppUtils.getCurrencyFormat(bidModel.getBidAmount()));
                        bidDetailsBinding.tvBidingTime.setText(AppUtils.getTimeAgo(bidModel.getTimestamp()));
                        bidDetailsBinding.btnAccept.setEnabled(!bidModel.isBidStatus());

                        if (null != bidModel.getBidAcceptBy())
                            if (bidModel.getUid().equals(getUid()) || bidModel.getBidAcceptBy().equals(getUid())) {
                                if (null == bidModel.getBidAcceptBy())
                                    bidDetailsBinding.resultLay.setVisibility(View.GONE);
                                else if (null != bidModel.getImage()) {
                                    bidDetailsBinding.resultLay.setVisibility(View.GONE);
                                } else if (null != bidModel.getResultUploadedBy() && bidModel.getResultUploadedBy().equals(getUid())) {
                                    bidDetailsBinding.resultLay.setVisibility(View.GONE);
                                } else {
                                    bidDetailsBinding.resultLay.setVisibility(bidModel.getBidAcceptBy().equals(getUid()) ? View.VISIBLE : View.GONE);
                                }

                                if (null != bidModel.getImage())

                                    bidDetailsBinding.textView18.setVisibility(bidModel.getImage().equals("") ? View.GONE : View.VISIBLE);


                            }
                    }

                }).addOnFailureListener(e -> {
            AppUtils.hideDialog();
            Toast.makeText(requireActivity(), "Failed to get Bid Details, try again", Toast.LENGTH_SHORT).show();
        });
    }
}