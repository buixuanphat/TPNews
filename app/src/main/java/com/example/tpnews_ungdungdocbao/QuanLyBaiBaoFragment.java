package com.example.tpnews_ungdungdocbao;

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


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuanLyBaiBaoFragment extends Fragment {

    Button btnSaveContent, btnAddImageNews, btnAddContent, btnCancelContent, btnUpdate;
    EditText edtInputContent, edtInputDescription, edtInputTitle;
    ImageView imgInputImage;
    Uri imageURI;
    private DatabaseReference myFireBaseDB;
    ListView lvArticle;
    Spinner spCategory;
    Spinner spOutlet;
    ArrayList<Article> arrlArticle;
    ArrayList<Outlet> arrlOutlet;
    ArrayList<String> arrlCategory;
    ProgressBar progressBar;
    ArticleManagerAdapter articleAdapter;

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_bai_bao, container, false);


        progressBar= view.findViewById(R.id.progressBarArticleManager);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAddContent = view.findViewById(R.id.btnAddContent);
        btnCancelContent = view.findViewById(R.id.btnCancelContent);
        btnSaveContent = view.findViewById(R.id.btnSaveContent);
        btnAddImageNews = view.findViewById(R.id.btnAddImageNews);
        btnUpdate = view.findViewById(R.id.btnUpdateArticle);
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
                        arrlCategory.add(cat.getName());
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
            btnAddContent.setVisibility(View.GONE);
            btnSaveContent.setVisibility(View.VISIBLE);
            btnCancelContent.setVisibility(View.VISIBLE);
            btnAddImageNews.setVisibility(View.VISIBLE);
            edtInputTitle.setVisibility(View.VISIBLE);
            edtInputDescription.setVisibility(View.VISIBLE);
            edtInputContent.setVisibility(View.VISIBLE);
            spCategory.setVisibility(View.VISIBLE);
            spOutlet.setVisibility(View.VISIBLE);
            lvArticle.setVisibility(View.GONE);
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
                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageURI);
                        String base64String = convertBitmapToBase64(bitmap);
                        Outlet outlet =  (Outlet) spOutlet.getSelectedItem();
                        String outletLogo = outlet.getLogo();
                        String outletName = outlet.getName();
                        String key = myFireBaseDB.child("Article").push().getKey();
                        Article article = new Article(key, edtInputTitle.getText().toString(), edtInputDescription.getText().toString(), edtInputContent.getText().toString(), base64String, outletName, outletLogo, spCategory.getSelectedItem().toString());
                        myFireBaseDB.child("Article").child(key).setValue(article, (error, ref) -> {
                            if (error == null) {
                                load();
                                Toast.makeText(getContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getContext(), "Lỗi khi lưu bài viết: "+e, Toast.LENGTH_SHORT).show();
                    }

                    resetForm();
            }
        });

        btnCancelContent.setOnClickListener(v -> resetForm());

        lvArticle.setOnItemClickListener((parent, view1, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Thông báo");
            builder.setMessage("Bạn muốn thực hiện hành động nào");

            builder.setPositiveButton("Sửa", (dialog, which) -> {
                // Hiển thị các thành phần nhập liệu
                btnAddContent.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.VISIBLE);
                btnCancelContent.setVisibility(View.VISIBLE);
                btnAddImageNews.setVisibility(View.VISIBLE);
                edtInputTitle.setVisibility(View.VISIBLE);
                edtInputDescription.setVisibility(View.VISIBLE);
                edtInputContent.setVisibility(View.VISIBLE);
                spCategory.setVisibility(View.VISIBLE);
                spOutlet.setVisibility(View.VISIBLE);
                imgInputImage.setVisibility(View.VISIBLE);
                lvArticle.setVisibility(View.GONE);

                // Hiển thị dữ liệu bài báo được chọn
                Article selectedArticle = arrlArticle.get(position);
                edtInputTitle.setText(selectedArticle.getTitle());
                edtInputDescription.setText(selectedArticle.getDescription());
                edtInputContent.setText(selectedArticle.getContent());

                // Chuyển đổi Base64 thành Bitmap để hiển thị hình ảnh
                imgInputImage.setImageBitmap(convertBase64ToBitmap(selectedArticle.getImage()));

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

                btnUpdate.setOnClickListener(v -> {
                    if (edtInputTitle.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                    } else if (edtInputDescription.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                    } else if (edtInputContent.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                    } else {
                            DatabaseReference articleRef = FirebaseDatabase.getInstance().getReference("Article").child(arrlArticle.get(position).getId());
                            if (imageURI!=null)
                            {
                                try
                                {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageURI);
                                    String base64String = convertBitmapToBase64(bitmap);
                                    Outlet outlet = (Outlet) spOutlet.getSelectedItem();
                                    String outletName = outlet.getName();
                                    String outletLogo = outlet.getLogo();

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("title", edtInputTitle.getText().toString());
                                    updates.put("description", edtInputDescription.getText().toString());
                                    updates.put("content", edtInputContent.getText().toString());
                                    updates.put("image", base64String);
                                    updates.put("outletName", outletName);
                                    updates.put("outletLogo", outletLogo);
                                    updates.put("category", spCategory.getSelectedItem().toString());
                                    articleRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error==null) {
                                                resetForm();
                                                load();
                                                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Lỗi: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getContext(), "Lỗi khi cập nhật bài viết: "+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Outlet outlet = (Outlet) spOutlet.getSelectedItem();
                                String outletName = outlet.getName();
                                String outletLogo = outlet.getLogo();

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("title", edtInputTitle.getText().toString());
                                updates.put("description", edtInputDescription.getText().toString());
                                updates.put("content", edtInputContent.getText().toString());
                                updates.put("outletName", outletName);
                                updates.put("outletLogo", outletLogo);
                                updates.put("category", spCategory.getSelectedItem().toString());

                                articleRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error==null) {
                                            resetForm();
                                            load();
                                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Lỗi: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        resetForm();
                    }
                });


            });

            builder.setNegativeButton("Xóa", (dialog, which) -> {
                String key = arrlArticle.get(position).getId();
                myFireBaseDB.child("Article").child(key).removeValue((error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        articleAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Xóa không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void load() {
        myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlArticle.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        arrlArticle.add(article);
                    }
                }
                articleAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        btnAddContent.setVisibility(View.VISIBLE);
        btnSaveContent.setVisibility(View.GONE);
        btnCancelContent.setVisibility(View.GONE);
        btnAddImageNews.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);
        edtInputTitle.setVisibility(View.GONE);
        edtInputDescription.setVisibility(View.GONE);
        edtInputContent.setVisibility(View.GONE);
        spCategory.setVisibility(View.GONE);
        spOutlet.setVisibility(View.GONE);
        imgInputImage.setVisibility(View.GONE);
        lvArticle.setVisibility(View.VISIBLE);
        edtInputTitle.setText("");
        edtInputDescription.setText("");
        edtInputContent.setText("");
        imageURI = null;
    }
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
