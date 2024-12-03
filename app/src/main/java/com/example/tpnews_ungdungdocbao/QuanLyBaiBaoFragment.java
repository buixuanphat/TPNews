package com.example.tpnews_ungdungdocbao;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuanLyBaiBaoFragment extends Fragment {

    Button btnSaveContent, btnAddImageNews, btnAddContent, btnCancelContent;
    EditText edtInputContent, edtInputDescription, edtInputTitle;

    ImageView imgInputImageNews;
    Uri imageUri;

    private DatabaseReference myFireBaseDB;
    String imageLink;

    ListView lvArticleManager;

    Spinner spCategory;
    Spinner spOutlet;

    ArrayList<Article> arrlArticle;

    ProgressBar progressBarArticleManager;
    ArticleManagerAdapter articleAdapter;

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quan_ly_bai_bao, container, false);
        progressBarArticleManager = view.findViewById(R.id.progressBarArticleManager);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAddContent = view.findViewById(R.id.btnAddContent);
        btnCancelContent = view.findViewById(R.id.btnCancelContent);
        btnSaveContent = view.findViewById(R.id.btnSaveContent);
        btnAddImageNews = view.findViewById(R.id.btnAddImageNews);
        edtInputDescription = view.findViewById(R.id.edtInputDescription);
        edtInputTitle = view.findViewById(R.id.edtInputTitle);
        edtInputContent = view.findViewById(R.id.edtInputContent);
        imgInputImageNews = view.findViewById(R.id.imgInputImageNews);
        lvArticleManager = view.findViewById(R.id.lvArticleManager);
        spOutlet = view.findViewById(R.id.spOutlet);
        spCategory = view.findViewById(R.id.spCategory);
        ArrayList<String> arrlOutlet = new ArrayList<>();
        ArrayList<String> arrlCategory = new ArrayList<>();
        ArrayAdapter outletAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, arrlOutlet);
        spOutlet.setAdapter(outletAdapter);
        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlOutlet.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        arrlOutlet.add(outlet.getName());
                    }
                }
                outletAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi Firebase
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ArrayAdapter catAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, arrlCategory);
        spCategory.setAdapter(catAdapter);
        myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlCategory.clear(); // Xóa dữ liệu cũ trước khi thêm mới
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
                // Xử lý lỗi Firebase
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        arrlArticle = new ArrayList<>();
        articleAdapter = new ArticleManagerAdapter(getContext(), R.layout.layout_article_manager, arrlArticle);
        lvArticleManager.setAdapter(articleAdapter);

        btnAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddContent.setVisibility(View.GONE);
                btnSaveContent.setVisibility(View.VISIBLE);
                btnCancelContent.setVisibility(View.VISIBLE);
                btnAddImageNews.setVisibility(View.VISIBLE);
                edtInputTitle.setVisibility(View.VISIBLE);
                edtInputDescription.setVisibility(View.VISIBLE);
                edtInputContent.setVisibility(View.VISIBLE);
                spCategory.setVisibility(View.VISIBLE);
                spOutlet.setVisibility(View.VISIBLE);
                edtInputContent.setText("");
                edtInputDescription.setText("");
                edtInputTitle.setText("");

            }
        });

        btnAddImageNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 500);
            }
        });
        btnSaveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtInputTitle.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                } else if (edtInputDescription.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                } else if (edtInputContent.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                } else if (imgInputImageNews.getDrawable() == null) {
                    Toast.makeText(getContext(), "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                } else {

                    uploadImageToCloudinary(imageUri);
                    btnAddContent.setVisibility(View.VISIBLE);
                    btnSaveContent.setVisibility(View.GONE);
                    btnCancelContent.setVisibility(View.GONE);
                    btnAddImageNews.setVisibility(View.GONE);
                    imgInputImageNews.setVisibility(View.GONE);
                    edtInputTitle.setVisibility(View.GONE);
                    edtInputDescription.setVisibility(View.GONE);
                    edtInputContent.setVisibility(View.GONE);
                    spCategory.setVisibility(View.GONE);
                    spOutlet.setVisibility(View.GONE);
                    imageUri = null;

                }
            }
        });

        btnCancelContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddContent.setVisibility(View.VISIBLE);
                btnSaveContent.setVisibility(View.GONE);
                btnCancelContent.setVisibility(View.GONE);
                btnAddImageNews.setVisibility(View.GONE);
                imgInputImageNews.setVisibility(View.GONE);
                edtInputTitle.setVisibility(View.GONE);
                edtInputDescription.setVisibility(View.GONE);
                edtInputContent.setVisibility(View.GONE);
                spCategory.setVisibility(View.GONE);
                spOutlet.setVisibility(View.GONE);
            }
        });

        lvArticleManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // Thiết lập tiêu đề và nội dung
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn muốn thực hiện hành động nào");

                // Thêm nút "Sửa"
                builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnAddContent.setVisibility(View.GONE);
                        btnSaveContent.setVisibility(View.VISIBLE);
                        btnCancelContent.setVisibility(View.VISIBLE);
                        btnAddImageNews.setVisibility(View.VISIBLE);
                        edtInputTitle.setVisibility(View.VISIBLE);
                        edtInputDescription.setVisibility(View.VISIBLE);
                        edtInputContent.setVisibility(View.VISIBLE);
                        spCategory.setVisibility(View.VISIBLE);
                        spOutlet.setVisibility(View.VISIBLE);
                        imgInputImageNews.setVisibility(View.VISIBLE);

                        edtInputTitle.setText(arrlArticle.get(position).getTitle());
                        edtInputDescription.setText(arrlArticle.get(position).getDescription());
                        edtInputContent.setText(arrlArticle.get(position).getContent());
                        Glide.with(getContext()).load(arrlArticle.get(position).getImageUrl()).into(imgInputImageNews);
                        spCategory.setSelection(arrlCategory.indexOf(arrlArticle.get(position).getCategory()));
                        spOutlet.setSelection(arrlOutlet.indexOf(arrlArticle.get(position).getOutlet()));
                        btnSaveContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtInputTitle.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                                } else if (edtInputDescription.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                                } else if (edtInputContent.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                                } else {
                                    updateImageToCloudinary(imageUri, arrlArticle.get(position).getId());
                                    btnAddContent.setVisibility(View.VISIBLE);
                                    btnSaveContent.setVisibility(View.GONE);
                                    btnCancelContent.setVisibility(View.GONE);
                                    btnAddImageNews.setVisibility(View.GONE);
                                    imgInputImageNews.setVisibility(View.GONE);
                                    edtInputTitle.setVisibility(View.GONE);
                                    edtInputDescription.setVisibility(View.GONE);
                                    edtInputContent.setVisibility(View.GONE);
                                    spCategory.setVisibility(View.GONE);
                                    spOutlet.setVisibility(View.GONE);
                                    imageUri = null;
                                }
                            }
                        });


                    }
                });

                // Thêm nút "Xóa"
                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String key = arrlArticle.get(position).getId();
                        myFireBaseDB.child("Article").child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    articleAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getContext(), "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                // Thêm nút "Hủy"
                builder.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Hiển thị Alert Dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 500 && resultCode == RESULT_OK && data != null) {
            // Lấy URI của hình ảnh được chọn
            imageUri = data.getData();
            imgInputImageNews.setImageURI(imageUri);  // Hiển thị hình ảnh lên ImageView
            imgInputImageNews.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Không có hình ảnh để upload", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Cloudinary
        MediaManager.get().upload(imageUri)
                .unsigned("OutletImage")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(getContext(), "Bắt đầu upload", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Optionally handle upload progress
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String uploadedUrl = (String) resultData.get("secure_url");
                        Toast.makeText(getContext(), "Upload thành công", Toast.LENGTH_LONG).show();
                        imageLink = uploadedUrl;
                        String key = myFireBaseDB.child("Article").push().getKey();
                        Article article = new Article(key, edtInputTitle.getText().toString(), edtInputDescription.getText().toString(), edtInputContent.getText().toString(), imageLink, spOutlet.getSelectedItem().toString(), spCategory.getSelectedItem().toString());
                        myFireBaseDB.child("Article").child(key).setValue(article, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(getContext(), "Lưu trang báo thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Lưu không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Toast.makeText(getContext(), "Upload không thành công: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        // Optionally handle rescheduling
                    }
                }).dispatch();
    }

    private void updateImageToCloudinary(Uri imageUri, String id) {
        if (imageUri != null) {
            // Upload image to Cloudinary
            MediaManager.get().upload(imageUri)
                    .unsigned("OutletImage")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Toast.makeText(getContext(), "Bắt đầu upload", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            // Optionally handle upload progress
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String uploadedUrl = (String) resultData.get("secure_url");
                            Toast.makeText(getContext(), "Upload thành công", Toast.LENGTH_LONG).show();
                            imageLink = uploadedUrl;

                            DatabaseReference editArticle = myFireBaseDB.child("Article").child(id);
                            Map<String, Object> update = new HashMap<>();
                            update.put("title", edtInputTitle.getText().toString());
                            update.put("description", edtInputDescription.getText().toString());
                            update.put("content", edtInputContent.getText().toString());
                            update.put("category", spCategory.getSelectedItem().toString());
                            update.put("outlet", spOutlet.getSelectedItem().toString());
                            update.put("imageUrl", imageLink);

                            editArticle.updateChildren(update, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "Upload không thành công: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            // Optionally handle rescheduling
                        }
                    }).dispatch();
        } else if (imageUri == null) {
            DatabaseReference editArticle = myFireBaseDB.child("Article").child(id);
            Map<String, Object> update = new HashMap<>();
            update.put("title", edtInputTitle.getText().toString());
            update.put("description", edtInputDescription.getText().toString());
            update.put("content", edtInputContent.getText().toString());
            update.put("category", spCategory.getSelectedItem().toString());
            update.put("outlet", spOutlet.getSelectedItem().toString());

            editArticle.updateChildren(update, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void load() {
        myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarArticleManager.setVisibility(View.VISIBLE);
                arrlArticle.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        arrlArticle.add(article);
                    }
                }
                articleAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                progressBarArticleManager.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi Firebase
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
