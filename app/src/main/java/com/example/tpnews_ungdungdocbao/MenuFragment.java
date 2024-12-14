package com.example.tpnews_ungdungdocbao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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



    //Khai báo biến đăng nhập/đăng xuất
    Button btnAccount, btnLogOut;
    TextView tvName;

    //Khai báo biến đăng nhập Google
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    UserTPNew userTP = new UserTPNew();


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



        //Lấy dữ liệu
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivityForResult(intent, 211);


        //Đâng nhập Google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);


        btnAccount = view.findViewById(R.id.btnAccount);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        tvName = view.findViewById(R.id.tvName);


        //Đăng nhập
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userTP.getUsername() != null) {
                    Toast.makeText(getContext(), "Bạn phải đăng xuất!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });



        //Đăng xuất
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (userTP.getUsername() == null) {
                            Toast.makeText(getContext(), "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            userTP = new UserTPNew(null, "", 0);
                            tvName.setText(userTP.getUsername());
                            btnAdmin.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        return view;
    }

    //Lấy dữ liệu
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 211 && resultCode == Activity.RESULT_OK) {
            String username = data.getStringExtra("username");
            int active = data.getIntExtra("active", 0);
            userTP = new UserTPNew(username, "", active);
            tvName.setText(userTP.getUsername());
            if (userTP.getActive() == 1) {
                btnAdmin.setVisibility(View.VISIBLE);
            } else {
                btnAdmin.setVisibility(View.INVISIBLE);
            }
//            Toast.makeText(getContext(), String.valueOf("Menu username = " + userTP.getUsername() + " active = " + userTP.getActive()), Toast.LENGTH_SHORT).show();
        }
    }
}
