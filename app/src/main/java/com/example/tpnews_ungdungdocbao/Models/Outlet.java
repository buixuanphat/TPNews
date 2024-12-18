package com.example.tpnews_ungdungdocbao.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tpnews_ungdungdocbao.Adapters.OutletAdapter;
import com.example.tpnews_ungdungdocbao.Data.Static;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Outlet {
    private String id;
    private String name;
    private String logo;

    public Outlet() {
    }

    public Outlet(String id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public static void loadOutlet(DatabaseReference myFireBaseDB, ArrayList<Outlet> outlets, OutletAdapter outletAdapter, ProgressBar progressBar) {
        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                outlets.clear();
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        outlets.add(outlet);
                    }
                }
                outletAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static void addOutlet(DatabaseReference myFireBaseDB, EditText editText, Uri uri, Context context) {

        try {
            String key = myFireBaseDB.child("Outlet").push().getKey();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            Outlet outlet = new Outlet(key, editText.getText().toString(), Static.convertBitmapToBase64(bitmap));
            myFireBaseDB.child("Outlet").child(key).setValue(outlet, (error, ref) -> {
                if (error == null) {
                    Toast.makeText(context, "Lưu trang báo thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void editOultlet(String id, EditText editText, Uri uri, Context context) {
        DatabaseReference outletRef = FirebaseDatabase.getInstance().getReference("Outlet").child(id);
        if (uri == null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", editText.getText().toString());
            outletRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", editText.getText().toString());
                updates.put("logo", Static.convertBitmapToBase64(bitmap));
                outletRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteOutlet(DatabaseReference myFireBaseDB, Outlet outlet, Context context) {
        // Xóa các bài báo của category được chọn
        myFireBaseDB.child("Article")
                .orderByChild("outletName")
                .equalTo(outlet.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                            String articleId = articleSnapshot.getKey();
                            myFireBaseDB.child("Article").child(articleId).removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        // Xóa category
        myFireBaseDB.child("Outlet").child(outlet.getId()).removeValue((error, ref) -> {
            if (error == null) {
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
