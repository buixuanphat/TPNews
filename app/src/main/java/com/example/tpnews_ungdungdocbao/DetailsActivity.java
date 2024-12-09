package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    private DatabaseReference firebaseDB;
    private ValueEventListener firebaseListener;
    private ImageView imgImage, imgLogo;
    private TextView txtTitle, txtDescription, txtContent;
    private ImageButton ibFontSize;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private NetworkReceiver networkReceiver;

    private String articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initViews();

        // Lấy ID bài viết từ Intent
        Intent intent = getIntent();
        articleId = intent.getStringExtra("id");

        // Cài đặt trạng thái mạng
        setupNetworkReceiver();

        // Cài đặt nút thay đổi kích thước chữ
        setupFontSizeButton();

        // Cài đặt SharedPreferences listener
        setupSharedPreferences();
    }

    private void initViews() {
        imgLogo = findViewById(R.id.imgvDetailOutlet);
        imgImage = findViewById(R.id.imgvDetailImage);
        txtTitle = findViewById(R.id.txtDetailTitle);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtContent = findViewById(R.id.txtDetailContent);
        ibFontSize = findViewById(R.id.imageButton);
    }

    private void setupNetworkReceiver() {
        networkReceiver = new NetworkReceiver(new NetworkReceiver.NetworkListener() {
            @Override
            public void onNetworkAvailable() {
                loadArticleFromFirebase();
            }

            @Override
            public void onNetworkLost() {
                loadArticleFromSQLite();
            }
        });

        // Đăng ký lắng nghe thay đổi mạng
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void setupFontSizeButton() {
        ibFontSize.setOnClickListener(v -> {
            BottomDrawerFragment bottomDrawerFragment = new BottomDrawerFragment();
            bottomDrawerFragment.show(getSupportFragmentManager(), bottomDrawerFragment.getTag());
        });
    }

    private void setupSharedPreferences() {
        preferences = getSharedPreferences("FontSize", Context.MODE_PRIVATE);
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

    private void loadArticleFromFirebase() {
        if (articleId == null) return;

        firebaseDB = FirebaseDatabase.getInstance().getReference("Article").child(articleId);

        // Tạo và thêm Firebase listener
        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) return;

                Article article = snapshot.getValue(Article.class);
                if (article != null) {
                    displayArticle(article);
                } else {
                    showToast("Bài báo không tồn tại hoặc bị lỗi.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Lỗi khi tải dữ liệu từ Firebase.");
            }
        };

        firebaseDB.addValueEventListener(firebaseListener);
    }

    private void loadArticleFromSQLite() {
        if (articleId == null) return;

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Article article = databaseHelper.getNewsOffline(articleId);
        if (article != null) {
            displayArticle(article);
        } else {
            showToast("Không thể tải bài báo từ SQLite.");
        }
    }

    private void displayArticle(Article article) {
        txtTitle.setText(article.getTitle());
        txtDescription.setText(article.getDescription());
        txtContent.setText(article.getContent());

        Bitmap imageBitmap = convertBase64ToBitmap(article.getImage());
        Glide.with(this)
                .load(imageBitmap)
                .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(30))))
                .into(imgImage);

        Bitmap logoBitmap = convertBase64ToBitmap(article.getOutletLogo());
        imgLogo.setImageBitmap(logoBitmap);
    }

    private void updateTextSize(int size) {
        txtTitle.setTextSize(size + 15);
        txtDescription.setTextSize(size + 5);
        txtContent.setTextSize(size);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap convertBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Hủy Firebase listener
        if (firebaseDB != null && firebaseListener != null) {
            firebaseDB.removeEventListener(firebaseListener);
        }

        // Hủy đăng ký SharedPreferences listener
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);

        // Hủy đăng ký BroadcastReceiver
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }
}
