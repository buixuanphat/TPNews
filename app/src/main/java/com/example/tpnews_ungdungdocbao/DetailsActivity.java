package com.example.tpnews_ungdungdocbao;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    ImageView imgImage, imgLogo;
    TextView txtTitle, txtDescription, txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgImage = findViewById(R.id.imgvDetailImage);
        imgLogo = findViewById(R.id.imgvDetailOutlet);
        txtTitle = findViewById(R.id.txtDetailTitle);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtContent = findViewById(R.id.txtDetailContent);

        Intent intent = getIntent();
        txtTitle.setText(intent.getStringExtra("title"));
        txtDescription.setText(intent.getStringExtra("description"));
        txtContent.setText(intent.getStringExtra("content"));
        Glide.with(DetailsActivity.this).load(intent.getStringExtra("imageurl")).into(imgImage);
        Glide.with(DetailsActivity.this).load(intent.getStringExtra("logourl")).into(imgLogo);





    }
}