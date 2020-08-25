package com.manager.smartcafe;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    private DatabaseReference currCustomerDbRef;
    private FirebaseUser currUser;

    private TextView tvName,tvEmail,tvPhoneNo;


    ImageView ivProfileImage;

    ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Profile");

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        tvName = rootView.findViewById(R.id.tv_name);
        tvEmail = rootView.findViewById(R.id.tv_email);
        tvPhoneNo= rootView.findViewById(R.id.tv_phoneNo);


        currUser = FirebaseAuth.getInstance().getCurrentUser();

        currCustomerDbRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MANAGER_DATABASE_REFERENCE).child(currUser.getUid());

        currCustomerDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                User user = dataSnapshot.getValue(User.class);
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvPhoneNo.setText(user.getPhoneNo());

                Glide.with(getContext()).load(Uri.parse(user.getProfileUrl())).error(R.drawable.ic_add_a_photo_black_96dp).into(ivProfileImage);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ivProfileImage = rootView.findViewById(R.id.iv_profile_image);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),
                        Constants.PROFILE_IMAGE_CHOOSER_REQUEST_CODE);
            }
        });


        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constants.PROFILE_IMAGE_CHOOSER_REQUEST_CODE == requestCode){
            if(resultCode == RESULT_OK && data!=null){
                Uri uri = data.getData();
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(uri).error(R.drawable.ic_add_a_photo_primary_96dp).into(ivProfileImage);

                uploadtoDatabase(uri);
            }
        }
    }

    private void uploadtoDatabase(Uri uri) {
        long time = System.currentTimeMillis();
        String imageName = time+"."+getFileExtension(uri);
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGE_STORAGE_REFERENCE).child(imageName);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("TESTUser",currUser);
                        DatabaseReference managerdatabase = FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_MANAGER_DATABASE_REFERENCE)
                                .child(currUser)
                                .child("profileUrl");
                        managerdatabase.setValue(uri.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });



    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}
