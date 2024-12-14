package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

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
            else if(item.getItemId()==R.id.nav_trend)
            {
                viewPager.setCurrentItem(1);
            }
            else if(item.getItemId()==R.id.nav_video)
            {
                viewPager.setCurrentItem(2);
            }
            else if(item.getItemId()==R.id.nav_menu) {
                viewPager.setCurrentItem(3);
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
                        bottomNavigationView.setSelectedItemId(R.id.nav_trend);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_video);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_menu);
                        break;
                }
            }
        });
    }
}
