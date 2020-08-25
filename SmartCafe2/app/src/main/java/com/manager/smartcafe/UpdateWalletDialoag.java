package com.manager.smartcafe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.User;

import androidx.annotation.NonNull;

public class UpdateWalletDialoag extends Dialog implements View.OnClickListener {

    Context context;
    public UpdateWalletDialoag(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    Button yes,no;
    EditText etEmail,etAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snippet_update_wallet);

        yes = findViewById(R.id.btn_update_wallet_ok);
        no = findViewById(R.id.btn_update_wallet_cancel);

        etEmail = findViewById(R.id.et_email);
        etAmount = findViewById(R.id.et_amount);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);




    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update_wallet_ok:
                updateWallet();

                break;
            case R.id.btn_update_wallet_cancel:
                dismiss();
                Log.d("AKJAK","ASKDLJ");
                break;
            default:
                break;
        }
        dismiss();
    }

    private void updateWallet() {


        final DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CUSTOMER_DATABASE_REFERENCE);

        userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user: dataSnapshot.getChildren()) {
                    User user1 = user.getValue(User.class);
                    if(user1.getEmail().equalsIgnoreCase(etEmail.getText().toString())){
                        if(user1.getWallet()==0){
                            userDbRef.child(user1.getId()).child("wallet").setValue(Integer.parseInt(etAmount.getText().toString()));

                        }else{
                            userDbRef.child(user1.getId()).child("wallet").setValue(user1.getWallet()+Integer.parseInt(etAmount.getText().toString()));
                        }
                        Toast.makeText(context , "Wallet Update", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Toast.makeText(context , "User not found", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
