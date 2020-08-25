package com.manager.smartcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    EditText etName,etPassword,etEmail,etPhoneNo;
    Button btnSignUp;

    ProgressBar progressBar;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigup);
        Log.d(TAG,"onCreate");


        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        etPhoneNo = findViewById(R.id.et_phoneNo);
        btnSignUp = findViewById(R.id.btn_sign_up);
        progressBar = findViewById(R.id.progressbar);
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnSignUpClicked");
                progressBar.setVisibility(View.VISIBLE);
                userSignUp();
            }
        });
    }

    private void userSignUp() {

        Log.d(TAG,"userSignUp");
        //check if any text field is empty
        if (etName.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this, "Name field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPassword.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this, "Password field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etEmail.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this, "Email field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPhoneNo.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this, "Phone number field is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"User SignUp successfully");
                        //store user credinal in data
                        User user = new User(authResult.getUser().getUid().toString(),etName.getText().toString(),
                                etEmail.getText().toString(),etPassword.getText().toString(),etPhoneNo.getText().toString(),0,"default");
                        saveUserInDatabase(user);
                    }
                });


     }

    private void saveUserInDatabase(User user) {
        Log.d(TAG,"saveUserInDatabase");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MANAGER_DATABASE_REFERENCE).child(user.getId());

        userRef.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"saveUserInDatabase:Success");
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SignUpActivity.this, "Sign Up successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"saveUserInDatabase:Failure");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
