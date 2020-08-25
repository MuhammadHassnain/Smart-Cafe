package com.customer.smartcafe;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText etEmail,etPassword;
    Button btnLogin;

    ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        progressBar = findViewById(R.id.progressbar);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);


        btnLogin = findViewById( R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnLogin:Clicked");
                progressBar.setVisibility(View.VISIBLE);
                LoginUser();

            }
        });

    }

    private void LoginUser() {
        //check any field is empty
        if(etEmail.getText().toString().equals("") ||etPassword.getText().toString().equals("") ){
            Log.d(TAG, "LoginUser: A field is empty");
            Toast.makeText(this, "Please Enter Both Email and Password", Toast.LENGTH_SHORT).show();
            return;
        }

        String Email = etEmail.getText().toString();
        String Password = etPassword.getText().toString();


        mAuth.signInWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG,"LoginUser: User Login in successfulyy:");
                progressBar.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Can't login user:"+e.getMessage());
                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(LoginActivity.this, "Can't Login! Try Agian", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
