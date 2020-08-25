package com.customer.smartcafe;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.customer.smartcafe.Utils.Constants;
import com.customer.smartcafe.database.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment implements View.OnClickListener

{


    public OrderFragment() {
        // Required empty public constructor
    }

    List<Order> orderList;

    OrderRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle("Order");

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Inflate the layout for this fragment;
        orderList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_order);
        adapter = new OrderRecyclerViewAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ORDER_REFERENCE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("AAAAAA", "DATA CHANGE");
                progressBar.setVisibility(View.VISIBLE);
                orderList.clear();
                List<Order> unCompletedOrder = new ArrayList<>();
                String currUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    Log.d("USER1111:", user.getKey());
                    if (currUserKey.equalsIgnoreCase(user.getKey())) {
                        for (DataSnapshot order : user.getChildren()) {
                            Log.d("ORDER:", order.getKey());
                            Order order1 = order.getValue(Order.class);
                            if (order1.getStatus() == Order.ORDER_STATUS_COMPLETED) {
                                unCompletedOrder.add(order1);
                            } else {
                                orderList.add(order1);
                            }
                        }
                    }
                }
                orderList.addAll(unCompletedOrder);
                adapter.setOrderList(orderList);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
    }
}
