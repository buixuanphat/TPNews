package com.example.tpnews_ungdungdocbao;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    WebView webView;
    String kq;
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
        webView = findViewById(R.id.webViewDetails);
        webView.setWebViewClient(new WebViewClient());
        Intent intent = getIntent();
        ArrayList<String> content = intent.getStringArrayListExtra("content");

        int i =0;
        for (String s : content)
        {
            kq += content.get(i);
            i++;
        }

        String htmlData = "<html><body><h1>Chào mừng!</h1>"+ kq +"</body></html>";
        webView.loadData(htmlData, "text/html", "UTF-8");

    }
}