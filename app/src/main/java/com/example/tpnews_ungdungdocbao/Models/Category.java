package com.example.tpnews_ungdungdocbao.Models;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tpnews_ungdungdocbao.Adapters.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Category {
    private String id;
    private String name;

    public Category() {
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void loadCategories(DatabaseReference myFireBaseDB, ArrayList<Category> categories , CategoryAdapter adapter, ProgressBar progressBar) {
        myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                categories.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    if (cat != null) {
                        categories.add(cat);
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


    public static void addCategory(DatabaseReference myFireBaseDB, EditText editText, Context context) {
        String name = editText.getText().toString();
        String key = myFireBaseDB.child("Category").push().getKey();
        Category cat = new Category(key, name);
        myFireBaseDB.child("Category").child(key).setValue(cat, (error, ref) -> {
                if (error == null) {
                    Toast.makeText(context, "Lưu danh mục thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lưu không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    public static void editCategory(DatabaseReference myFireBaseDB, Category category, EditText editText ,Context context) {
        DatabaseReference editCategory = myFireBaseDB.child("Category").child(category.getId());
        Map<String, Object> update = new HashMap<>();
        update.put("name", editText.getText().toString());
        editCategory.updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void deleteCategory (DatabaseReference myFireBaseDB, Category category, Context context)
    {
        String key = category.getId();
        // Xóa các bài báo có category được chọn
        myFireBaseDB.child("Article")
                .orderByChild("category")
                .equalTo(category.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Duyệt qua các bài báo có category được chọn và xóa
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
        myFireBaseDB.child("Category").child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
