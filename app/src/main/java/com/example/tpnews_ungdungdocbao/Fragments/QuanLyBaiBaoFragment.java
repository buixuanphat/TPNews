package com.example.tpnews_ungdungdocbao.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.tpnews_ungdungdocbao.Data.Static;
import com.example.tpnews_ungdungdocbao.Models.Article;
import com.example.tpnews_ungdungdocbao.Models.Category;
import com.example.tpnews_ungdungdocbao.Models.Outlet;
import com.example.tpnews_ungdungdocbao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.tpnews_ungdungdocbao.Adapters.ArticleManagerAdapter;

public class QuanLyBaiBaoFragment extends Fragment {

    Button btnSaveContent, btnAddImageNews, btnAddContent, btnCancelContent;
    EditText edtInputContent, edtInputDescription, edtInputTitle;
    ImageView imgInputImage;
    Uri imageURI;
    private DatabaseReference myFireBaseDB;
    ListView lvArticle;
    Spinner spCategory;
    Spinner spOutlet;
    ArrayList<Article> arrlArticle;
    ArrayList<Outlet> arrlOutlet;
    ArrayList <Category> arrlCategory;
    ProgressBar progressBar;
    ArticleManagerAdapter articleAdapter;

    @Override
    public void onResume() {
        super.onResume();
        Article.loadArticles(myFireBaseDB, arrlArticle, articleAdapter, progressBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_bai_bao, container, false);

        // Ánh xạ giao diện
        progressBar= view.findViewById(R.id.progressBarArticleManager);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAddContent = view.findViewById(R.id.btnAddContent);
        btnCancelContent = view.findViewById(R.id.btnCancelContent);
        btnSaveContent = view.findViewById(R.id.btnSaveContent);
        btnAddImageNews = view.findViewById(R.id.btnAddImageNews);
        edtInputDescription = view.findViewById(R.id.edtInputDescription);
        edtInputTitle = view.findViewById(R.id.edtInputTitle);
        edtInputContent = view.findViewById(R.id.edtInputContent);
        imgInputImage = view.findViewById(R.id.imgInputImageNews);
        lvArticle = view.findViewById(R.id.lvArticleManager);
        spOutlet = view.findViewById(R.id.spOutlet);
        spCategory = view.findViewById(R.id.spCategory);

        ArrayAdapter outletAdapter;
        ArrayAdapter catAdapter;

        //Tải dữ liệu Outlet
        arrlOutlet = new ArrayList<>();
        outletAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, arrlOutlet);
        spOutlet.setAdapter(outletAdapter);
        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlOutlet.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        arrlOutlet.add(outlet);
                    }
                }
                outletAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Tải dữ liệu Category
        arrlCategory = new ArrayList<>();
        catAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, arrlCategory);
        spCategory.setAdapter(catAdapter);
        myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlCategory.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    if (cat != null) {
                        arrlCategory.add(cat);
                    }
                }
                catAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Khởi tạo listview Article
        arrlArticle = new ArrayList<>();
        articleAdapter = new ArticleManagerAdapter(getContext(), R.layout.layout_article_manager, arrlArticle);
        lvArticle.setAdapter(articleAdapter);

        btnAddContent.setOnClickListener(v -> {
           appearForm();
        });

        // Mở Intent chọn hình ảnh từ thiết bị
        btnAddImageNews.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 007);
        });

        // Xử lý nút lưu
        btnSaveContent.setOnClickListener(v -> {
            if (edtInputTitle.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            } else if (edtInputDescription.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
            } else if (edtInputContent.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            } else if (imageURI == null) {
                Toast.makeText(getContext(), "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Article.addArticle(myFireBaseDB, edtInputTitle, edtInputDescription, edtInputContent, spOutlet, spCategory ,imageURI, getContext());
                Article.loadArticles(myFireBaseDB, arrlArticle, articleAdapter, progressBar);
                hideForm();
            }
        });

        btnCancelContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideForm();
            }
        });

        lvArticle.setOnItemClickListener((parent, view1, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Thông báo");
            builder.setMessage("Bạn muốn thực hiện hành động nào");

            builder.setPositiveButton("Sửa", (dialog, which) -> {
                appearForm();
                Article selectedArticle = arrlArticle.get(position);
                edtInputTitle.setText(selectedArticle.getTitle());
                edtInputDescription.setText(selectedArticle.getDescription());
                edtInputContent.setText(selectedArticle.getContent());

                // Chuyển đổi Base64 thành Bitmap để hiển thị hình ảnh
                imgInputImage.setImageBitmap(Static.convertBase64ToBitmap(selectedArticle.getImage()));
                imgInputImage.setVisibility(View.VISIBLE);

                // Cập nhật spinner Category với dữ liệu bài viết chọn
                String selectedCategory = selectedArticle.getCategory();
                for (int i = 0; i < arrlCategory.size(); i++) {
                    if (arrlCategory.get(i).equals(selectedCategory)) {
                        spCategory.setSelection(i);
                        break;
                    }
                }

                // Cập nhật Outlet Spinner
                String selectedOutletName = selectedArticle.getOutletName();
                for (int i = 0; i < arrlOutlet.size(); i++) {
                    if (arrlOutlet.get(i).getName().equals(selectedOutletName)) {
                        spOutlet.setSelection(i);
                        break;
                    }
                }

                btnSaveContent.setOnClickListener(v -> {
                    if (edtInputTitle.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                    } else if (edtInputDescription.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                    } else if (edtInputContent.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Article.editArticle(arrlArticle.get(position).getId(), edtInputTitle, edtInputDescription, edtInputContent, spOutlet, spCategory, imageURI, getContext());
                        Article.loadArticles(myFireBaseDB, arrlArticle, articleAdapter, progressBar);
                        hideForm();
                    }
                });


            });

            builder.setNegativeButton("Xóa", (dialog, which) -> {
               Article.deleteArticle(myFireBaseDB, arrlArticle.get(position).getId(), getContext());
                Article.loadArticles(myFireBaseDB, arrlArticle, articleAdapter, progressBar);
            });

            builder.setNeutralButton("Hủy", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


        return view;
    }

    // Xử lý hình ảnh chọn từ thiết bị
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 007 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            imgInputImage.setImageURI(imageURI);
            imgInputImage.setVisibility(View.VISIBLE);
        }
    }
    private void appearForm()
    {
        btnAddContent.setVisibility(View.GONE);
        btnSaveContent.setVisibility(View.VISIBLE);
        btnCancelContent.setVisibility(View.VISIBLE);
        btnAddImageNews.setVisibility(View.VISIBLE);
        edtInputTitle.setVisibility(View.VISIBLE);
        edtInputDescription.setVisibility(View.VISIBLE);
        edtInputContent.setVisibility(View.VISIBLE);
        spCategory.setVisibility(View.VISIBLE);
        spOutlet.setVisibility(View.VISIBLE);
        edtInputTitle.requestFocus();
    }

    private void hideForm()
    {
        btnAddContent.setVisibility(View.VISIBLE);
        btnSaveContent.setVisibility(View.GONE);
        btnCancelContent.setVisibility(View.GONE);
        btnAddImageNews.setVisibility(View.GONE);
        edtInputTitle.setVisibility(View.GONE);
        edtInputDescription.setVisibility(View.GONE);
        edtInputContent.setVisibility(View.GONE);
        spCategory.setVisibility(View.GONE);
        spOutlet.setVisibility(View.GONE);
        imgInputImage.setVisibility(View.GONE);
        edtInputTitle.setText("");
        edtInputDescription.setText("");
        edtInputContent.setText("");
        imageURI = null;
    }


}
