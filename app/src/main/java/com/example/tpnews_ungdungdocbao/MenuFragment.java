package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MenuFragment() {
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrlOutlet.clear();
        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        arrlOutlet.add(outlet);
                    }
                }
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    Button btnAdmin;
    private DatabaseReference myFireBaseDB;
    public static Switch switchDarkMode;

    OutletAdapter adapter;
    ArrayList<Outlet> arrlOutlet;
    ListView lvOutlet;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        // Lấy SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        boolean isDarkMode = preferences.getBoolean("isDarkMode", false);
        if (isDarkMode)
        {
            switchDarkMode.setChecked(true);
        }
        else if (!isDarkMode){
            switchDarkMode.setChecked(false);
        }

        // Lắng nghe thay đổi trạng thái của switch và lưu vào SharedPreferences
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkMode", true); // Lưu trạng thái dark mode
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkMode", false); // Lưu trạng thái sáng mode
            }
            editor.apply(); // Áp dụng thay đổi
        });

        // Firebase Database setup
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAdmin = view.findViewById(R.id.btnAdmin);
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminActivity.class);
            startActivity(intent);
        });

        // Quản lý đầu báo
        lvOutlet = view.findViewById(R.id.lvMenuOutlet);
        arrlOutlet = new ArrayList<>();
        adapter = new OutletAdapter(getContext(), R.layout.layout_outlet, arrlOutlet);
        lvOutlet.setAdapter(adapter);
        progressBar = view.findViewById(R.id.progressBarMenu);


        lvOutlet.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getContext(), ChosenOutletActivity.class);
            intent.putExtra("outlet", arrlOutlet.get(position).getName());
            startActivity(intent);
        });




        return view;
    }
}
