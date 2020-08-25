package com.manager.smartcafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.Menu;

public class AddMenuItemActivity extends AppCompatActivity {

    ImageView iv_menu_item_image;
    EditText et_menu_name,et_menu_price,et_menu_description;

    Button btnAddMenu;


    StorageReference menuItemImagesStorage;
    DatabaseReference menuDBRef;

    Uri uri;
    String imageName;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);


        iv_menu_item_image = findViewById(R.id.iv_menu_image);
        et_menu_name = findViewById(R.id.et_menu_name);
        et_menu_price = findViewById(R.id.et_menu_price);
        et_menu_description = findViewById(R.id.et_menu_description);
        btnAddMenu = findViewById(R.id.btn_add_menu_item);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        menuItemImagesStorage = FirebaseStorage.getInstance().getReference(Constants.MENU_ITEM_IMAGE_STORAGE_REFERENCE);

        menuDBRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MENU_REFERENCE).push();

        iv_menu_item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"choose menu item picture"), Constants.MENU_ITEM_IMAGE_CHOOSER_REQUEST_CODE);
            }
        });

        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                saveMenu();
            }
        });
    }

    private void saveMenu() {
        if(uri!=null){
            long time = System.currentTimeMillis();
            imageName = time+"."+getFileExtension(uri);
            final StorageReference fileReference = menuItemImagesStorage.child(imageName);
            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri _uri) {


                            DatabaseReference ref  = FirebaseDatabase.getInstance()
                                    .getReference(Constants.FIREBASE_MENU_REFERENCE).push();
                            String key = ref.getKey();

                            Menu menu = new Menu(
                                    et_menu_name.getText().toString(),
                                    Integer.parseInt(et_menu_price.getText().toString()),
                                    et_menu_description.getText().toString(),
                                    _uri.toString(),
                                    key
                            );

                            ref.setValue(menu);
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddMenuItemActivity.this,"Can't upload file",Toast.LENGTH_SHORT).show();

                }
            });
        }



    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constants.MENU_ITEM_IMAGE_CHOOSER_REQUEST_CODE == requestCode){
            if(resultCode == RESULT_OK && data!=null){
                uri = data.getData();
                Glide.with(this).asBitmap().load(uri).into(iv_menu_item_image);
            }
        }
    }
}
