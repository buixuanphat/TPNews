package com.example.tpnews_ungdungdocbao.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tpnews_ungdungdocbao.Adapters.ViewPagerAdapter;
import com.example.tpnews_ungdungdocbao.Models.UserTPNew;
import com.example.tpnews_ungdungdocbao.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    //Lấy dữ liệu
    UserTPNew userTP;
    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy chế độ Dark Mode từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("isDarkMode", false);

        // Thiết lập chế độ Dark Mode
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        // Đặt layout chính
        setContentView(R.layout.activity_main);

        // Ánh xạ các thành phần giao diện
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        // Thiết lập Adapter cho ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(4);

        // Đồng bộ BottomNavigationView và ViewPager
        setupNavigation();



        //Lấy dữ liệu
        myPrefs = getSharedPreferences("userTP", MODE_PRIVATE);
        setInfo();
        String rsUsername = myPrefs.getString("username", null);
        int rsActive = myPrefs.getInt("active", 0);
        userTP = new UserTPNew(rsUsername, "", rsActive);
//        Toast.makeText(MainActivity.this, String.valueOf("username = " + userTP.getUsername() + " active = " + userTP.getActive()), Toast.LENGTH_SHORT).show();



        //Truyền dữ liệu
        if (userTP.getUsername() != null) {
            transInfo();
        }
    }

    /**
     * Đồng bộ hóa ViewPager và BottomNavigationView
     */
    private void setupNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId()==R.id.nav_home)
            {
                viewPager.setCurrentItem(0);
            }
            else if(item.getItemId()==R.id.nav_video)
            {
                viewPager.setCurrentItem(1);
            }
            else if(item.getItemId()==R.id.nav_menu) {
                viewPager.setCurrentItem(2);
            }
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_video);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_menu);
                        break;
                }
            }
        });
    }



    //Lấy dữ liệu
    public void setInfo() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        int active = intent.getIntExtra("active", 0);
        if (username != null) {
            SharedPreferences.Editor myEdit = myPrefs.edit();
            myEdit.putString("username", username);
            myEdit.putInt("active", active);
            myEdit.commit();
        }
    }


    //Truyền dữ liệu
    public void transInfo() {
        String username = userTP.getUsername();
        int active = userTP.getActive();

        Intent intent = new Intent();
        intent.putExtra("username", username);
        intent.putExtra("active", active);
        setResult(Activity.RESULT_OK, intent);
    }
}
