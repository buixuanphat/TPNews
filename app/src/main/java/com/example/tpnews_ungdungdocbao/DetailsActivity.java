package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    ImageView imgImage, imgLogo;
    TextView txtTitle, txtDescription, txtContent;
    ImageButton ibFontSize;

    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Thiết lập giao diện toàn màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các thành phần giao diện
        imgImage = findViewById(R.id.imgvDetailImage);
        imgLogo = findViewById(R.id.imgvDetailOutlet);
        txtTitle = findViewById(R.id.txtDetailTitle);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtContent = findViewById(R.id.txtDetailContent);
        ibFontSize = findViewById(R.id.imageButton);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        txtTitle.setText(intent.getStringExtra("title"));
        txtDescription.setText(intent.getStringExtra("description"));
        txtContent.setText(intent.getStringExtra("content"));
        Glide.with(this).load(intent.getStringExtra("imageurl")).into(imgImage);
        Glide.with(this).load(intent.getStringExtra("logourl")).into(imgLogo);

        // Thiết lập lắng nghe nút thay đổi kích thước chữ
        ibFontSize.setOnClickListener(v -> {
            BottomDrawerFragment bottomDrawerFragment = new BottomDrawerFragment();
            bottomDrawerFragment.show(getSupportFragmentManager(), bottomDrawerFragment.getTag());
        });

        // Lấy SharedPreferences
        preferences = getSharedPreferences("FontSize", Context.MODE_PRIVATE);

        // Cài đặt listener để theo dõi thay đổi kích thước chữ
        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("size".equals(key)) {
                int size = preferences.getInt("size", 20); // Giá trị mặc định là 20
                updateTextSize(size);
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        // Cập nhật kích thước chữ ngay khi mở màn hình
        int initialSize = preferences.getInt("size", 20);
        updateTextSize(initialSize);
    }

    // Phương thức cập nhật kích thước chữ
    private void updateTextSize(int size) {
        txtTitle.setTextSize(size);
        txtDescription.setTextSize(size);
        txtContent.setTextSize(size);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký listener khi Activity bị hủy để tránh rò rỉ bộ nhớ
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
