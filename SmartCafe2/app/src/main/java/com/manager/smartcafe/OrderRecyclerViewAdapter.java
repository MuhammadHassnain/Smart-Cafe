package com.manager.smartcafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.Order;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderHolder>{


    Context mContext;
    List<Order> orderList;


    public OrderRecyclerViewAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holder= LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_order_row,parent,false);
        return new OrderHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        final Order order =orderList.get(position);

        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvItemName.setText(order.getItemName());
        holder.tvOrderNumber.setText(order.getOrderNo());
        int paymentMethod  = order.getPaymentMethod();

        if(paymentMethod == Order.PAYMENT_METHOD_CASH){
            holder.tvPaymentMethod.setText("CASH");
        }else if(paymentMethod == Order.PAYMENT_METHOD_WALLET){
            holder.tvPaymentMethod.setText("WALLET");
        }

        holder.tvPrice.setText(order.getPrice());
        final int status = order.getStatus();

        if(Order.ORDER_STATUS_COMPLETED==status){
            holder.btnStatusChange.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.btnStatusChange.setText("Delivered");
            holder.btnStatusChange.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            Log.d("COLOR OF ORDER STATUS","ACCENT");

        }else{
            holder.btnStatusChange.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            Log.d("COLOR OF ORDER STATUS","PRIMARY");
            holder.btnStatusChange.setText("Click to Deliver");
            holder.btnStatusChange.setTextColor(mContext.getResources().getColor(R.color.colorWhite));

        }


        holder.btnStatusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == Order.ORDER_STATUS_NOT_COMPLETED){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle("Order Compilation");
                    builder.setMessage("Do you want to mark order as completed?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference(Constants.FIREBASE_ORDER_REFERENCE)
                                    .child(order.getCustomerId())
                                    .child(order.getOrderNo());

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ref.child("status").setValue(Order.ORDER_STATUS_COMPLETED);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder{

        TextView tvCustomerName,tvItemName,tvOrderNumber,tvPaymentMethod,tvPrice;
        Button btnStatusChange;

         OrderHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_no);
            btnStatusChange = itemView.findViewById(R.id.view_status);
        }
    }

}
