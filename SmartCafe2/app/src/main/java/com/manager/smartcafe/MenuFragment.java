package com.manager.smartcafe;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manager.smartcafe.Utils.Constants;
import com.manager.smartcafe.database.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements MenuRecyclerViewAdapter.MenuClickListener {


    public MenuFragment() {
        // Required empty public constructor
    }

    private MenuRecyclerViewAdapter adapter;
    List<Menu> menuList;
    private DatabaseReference menuDbRef;

    FloatingActionButton fabAddNewMenuItem;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        Objects.requireNonNull(getActivity()).setTitle("MENU");
        progressBar.setVisibility(View.VISIBLE);
        menuDbRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MENU_REFERENCE);
        menuList  = new ArrayList<>();
        adapter = new MenuRecyclerViewAdapter(getActivity(),new ArrayList<Menu>(),this);
        final RecyclerView recyclerView = view.findViewById(R.id.menu_recyclerview);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        menuDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                menuList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Menu menu = snapshot.getValue(Menu.class);
                    menuList.add(menu);
                }
                adapter.setMenuList(menuList);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabAddNewMenuItem  = view.findViewById(R.id.fab_add_menu_item);
        fabAddNewMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddMenuItemActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }




    @Override
    public void onMenuItemClick(final int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Do you want to delete "+menuList.get(location).getMenuName()+" From Menu")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MENU_REFERENCE)
                                .child(menuList.get(location).getMenuId()).removeValue();
                    }
                }).setNegativeButton("No",null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
