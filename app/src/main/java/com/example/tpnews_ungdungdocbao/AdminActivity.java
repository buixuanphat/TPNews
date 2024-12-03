package com.example.tpnews_ungdungdocbao;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AdminFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new AdminFragmentAdapter(this);
        viewPager.setAdapter(adapter);

        // Thiết lập TabLayout và ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Bài báo");
                    break;
                case 1:
                    tab.setText("Danh mục");
                    break;
                case 2:
                    tab.setText("Đầu báo");
                    break;
            }
        }).attach();

        // Chọn tab mặc định "Quản lý bài báo" khi khởi động
        tabLayout.selectTab(tabLayout.getTabAt(0)); // Chọn tab ở vị trí 0
    }
}
