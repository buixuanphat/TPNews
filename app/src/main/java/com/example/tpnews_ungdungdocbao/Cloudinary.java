package com.example.tpnews_ungdungdocbao;

import android.app.Application;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class Cloudinary extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Cấu hình Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dnbno2tkc");
        MediaManager.init(this, config);
    }
}