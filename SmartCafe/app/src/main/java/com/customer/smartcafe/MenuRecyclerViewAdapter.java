package com.customer.smartcafe;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.smartcafe.Utils.Constants;
import com.customer.smartcafe.database.Menu;
import com.customer.smartcafe.database.Order;
import com.customer.smartcafe.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder> {

    private Context mContext;
    private List<Menu> menuList;

    MenuRecyclerViewAdapter(Context mContext, List<Menu> menuList) {
        this.mContext = mContext;
        this.menuList = menuList;
    }


    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_menu_row,parent,false);
        return new MenuViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        final Menu menu = this.menuList.get(position);


        Glide.with(mContext).load(menu.getMenuImageUrl()).error(R.mipmap.smartcafe_200x200).into(holder.ivMenuImage);
        holder.tvMenuName.setText(menu.getMenuName());
        holder.tvMenuPrice.setText("RS: "+menu.getMenuPrice());
        holder.tvMenuDescription.setText(menu.getMenuDescription());

        holder.orderMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

// 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(menu.getMenuName())
                        .setTitle("Payment method");
                builder.setPositiveButton("CASH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference userDatabase = FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_CUSTOMER_DATABASE_REFERENCE)
                                .child(user.getUid());
                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User _user = dataSnapshot.getValue(User.class);
                                String orderno = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())+System.currentTimeMillis();
                                DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ORDER_REFERENCE)
                                        .child(user.getUid()).child(orderno);
                                Order order = new Order(orderno,
                                        menu.getMenuName(),
                                        _user.getName(),
                                        menu.getMenuPrice()+"",
                                        Order.PAYMENT_METHOD_CASH,
                                        Order.ORDER_STATUS_NOT_COMPLETED,
                                user.getUid());
                                orderDatabaseReference.setValue(order);

                                Toast.makeText(mContext,"Order Placed Successfully",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }).setNegativeButton("WALLET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference userDatabase = FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_CUSTOMER_DATABASE_REFERENCE)
                                .child(user.getUid());
                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user1 = dataSnapshot.getValue(User.class);
                                        if(user1.getWallet()<menu.getMenuPrice()){
                                            Toast.makeText(mContext, "NO enough value in wallet", Toast.LENGTH_SHORT).show();
                                        }else{
                                            String orderno = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())+System.currentTimeMillis();
                                            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ORDER_REFERENCE)
                                                    .child(user.getUid()).child(orderno);
                                            Order order = new Order(orderno,
                                                    menu.getMenuName(),
                                                    user1.getName(),
                                                    menu.getMenuPrice()+"",
                                                    Order.PAYMENT_METHOD_WALLET,
                                                    Order.ORDER_STATUS_NOT_COMPLETED,
                                                    user.getUid());
                                            orderDatabaseReference.setValue(order);
                                            Toast.makeText(mContext, "Order Placed", Toast.LENGTH_SHORT).show();
                                            userDatabase.child("wallet").setValue(user1.getWallet()-menu.getMenuPrice());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }





    @Override
    public int getItemCount() {
        return menuList.size();
    }


    class MenuViewHolder extends   RecyclerView.ViewHolder{

        ImageView ivMenuImage;
        TextView tvMenuName,tvMenuPrice,tvMenuDescription;
        Button orderMenuItem;
        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuImage = itemView.findViewById(R.id.iv_menu_image);
            tvMenuName = itemView.findViewById(R.id.tv_menu_name);
            tvMenuDescription = itemView.findViewById(R.id.tv_menu_discription);
            tvMenuPrice = itemView.findViewById(R.id.tv_menu_price);
            orderMenuItem  = itemView.findViewById(R.id.btn_order_menu_item);
        }
    }
}
