package com.example.tpnews_ungdungdocbao.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tpnews_ungdungdocbao.Adapters.ArticleAdapter;
import com.example.tpnews_ungdungdocbao.Adapters.ArticleManagerAdapter;
import com.example.tpnews_ungdungdocbao.Data.Static;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Article {
    private String id;
    private String title;
    private String description;
    private String content;
    private String image;
    private String outletName;
    private String outletLogo;
    private String category;

    public Article() {
    }

    public Article(String id, String title, String description, String content, String image, String outletName, String outletLogo, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.image = image;
        this.outletName = outletName;
        this.outletLogo = outletLogo;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getOutletName() {
        return outletName;
    }

    public String getOutletLogo() {
        return outletLogo;
    }

    public String getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public void setOutletLogo(String outletLogo) {
        this.outletLogo = outletLogo;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static void loadArticles(DatabaseReference myFireBaseDB, ArrayList<Article> articles, ArticleManagerAdapter articleAdapter, ProgressBar progressBar) {
        myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articles.clear();
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        articles.add(article);
                    }
                }
                articleAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void addArticle(DatabaseReference myFireBaseDB, EditText title, EditText description, EditText content, Spinner spOutlet, Spinner spCategory, Uri uri, Context context) {

        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            String base64String = Static.convertBitmapToBase64(bitmap);
            Outlet outlet =  (Outlet) spOutlet.getSelectedItem();
            String key = myFireBaseDB.child("Article").push().getKey();
            Article article = new Article(key, title.getText().toString(), description.getText().toString(), content.getText().toString(), base64String, outlet.getName(), outlet.getLogo(), spCategory.getSelectedItem().toString());
            myFireBaseDB.child("Article").child(key).setValue(article, (error, ref) -> {
                if (error == null) {
                    Toast.makeText(context, "Lưu bài báo thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Lỗi: " + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    public static void editArticle(String id, EditText title, EditText description, EditText content, Spinner spOutlet, Spinner spCategory, Uri uri, Context context) {
        DatabaseReference articleRef = FirebaseDatabase.getInstance().getReference("Article").child(id);
        if (uri!=null)
        {
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                String base64String = Static.convertBitmapToBase64(bitmap);
                Outlet outlet = (Outlet) spOutlet.getSelectedItem();
                Map<String, Object> updates = new HashMap<>();
                updates.put("title", title.getText().toString());
                updates.put("description", description.getText().toString());
                updates.put("content", content.getText().toString());
                updates.put("image", base64String);
                updates.put("outletName", ((Outlet) spOutlet.getSelectedItem()).getName());
                updates.put("outletLogo", ((Outlet) spOutlet.getSelectedItem()).getLogo());
                updates.put("category", spCategory.getSelectedItem().toString());
                articleRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error==null) {
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Lỗi: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            catch (Exception e)
            {}
        }
        else
        {
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", title.getText().toString());
            updates.put("description", description.getText().toString());
            updates.put("content", content.getText().toString());
            updates.put("outletName", ((Outlet) spOutlet.getSelectedItem()).getName());
            updates.put("outletLogo", ((Outlet) spOutlet.getSelectedItem()).getLogo());
            updates.put("category", spCategory.getSelectedItem().toString());

            articleRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error==null) {
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void deleteArticle(DatabaseReference myFireBaseDB, String key, Context context) {
        myFireBaseDB.child("Article").child(key).removeValue((error, ref) -> {
            if (error == null) {
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Xóa không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
