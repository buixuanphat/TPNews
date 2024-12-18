package com.example.tpnews_ungdungdocbao.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tpnews_ungdungdocbao.Models.Category;
import com.example.tpnews_ungdungdocbao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.tpnews_ungdungdocbao.Adapters.CategoryAdapter;

public class QuanLyDanhMucFragment extends Fragment {

    private ListView lvCategory;
    private Button btnAdd, btnSave, btnCancel;
    private EditText edtInput;
    private DatabaseReference myFireBaseDB;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> arrlCategory;
    ProgressBar progressBar;

    public QuanLyDanhMucFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference(); // Khởi tạo Firebase tại đây
    }
    @Override
    public void onResume() {
        super.onResume();
        loadCategories(); // Gọi lại phương thức đọc dữ liệu từ Firebase
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_danh_muc, container, false);
        //Khởi tạo các view
        progressBar = view.findViewById(R.id.progressBar);
        lvCategory = view.findViewById(R.id.lvCategory);
        btnAdd = view.findViewById(R.id.btnAddCat);
        btnSave = view.findViewById(R.id.btnSaveCat);
        btnCancel = view.findViewById(R.id.btnCancelCat);
        edtInput = view.findViewById(R.id.edtInputCat);

        //Thiết lập cho listview
        arrlCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), R.layout.layout_category, arrlCategory);
        lvCategory.setAdapter(categoryAdapter);


        // Hiển thị form nhập danh mục mới
        btnAdd.setOnClickListener(v -> {
            btnAdd.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            edtInput.setVisibility(View.VISIBLE);
            edtInput.setText("");
        });

        // Lưu danh mục mới vào Firebase
        btnSave.setOnClickListener(v -> {
            String name = edtInput.getText().toString();
            if (!name.isEmpty()) {
                String key = myFireBaseDB.child("Category").push().getKey();
                Category cat = new Category(key, name);
                myFireBaseDB.child("Category").child(key).setValue(cat, (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getContext(), "Lưu danh mục thành công", Toast.LENGTH_SHORT).show();
                        edtInput.setText("");  // Xóa input sau khi lưu
                    } else {
                        Toast.makeText(getContext(), "Lưu không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
            btnAdd.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            edtInput.setVisibility(View.GONE);
        });

        // Hủy việc thêm danh mục
        btnCancel.setOnClickListener(v -> {
            edtInput.setText("");
            btnAdd.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            edtInput.setVisibility(View.GONE);
        });

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        btnAdd.setVisibility(View.GONE);
                        btnSave.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                        edtInput.setVisibility(View.VISIBLE);

                        edtInput.setText(arrlCategory.get(position).getName());

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id = arrlCategory.get(position).getId();
                                DatabaseReference editCat = myFireBaseDB.child("Category").child(id);
                                Map<String, Object> update = new HashMap<>();
                                update.put("name", edtInput.getText().toString());
                                editCat.updateChildren(update, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            categoryAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getContext(), "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });


                    }
                });

                // Thêm nút "Xóa"
                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = arrlCategory.get(position).getId();

                        myFireBaseDB.child("Article")
                                .orderByChild("category")
                                .equalTo(arrlCategory.get(position).getName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Duyệt qua các bài báo có category và xóa
                                        for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                                            String articleId = articleSnapshot.getKey();
                                            // Xóa bài báo
                                            myFireBaseDB.child("Article").child(articleId).removeValue();
                                        }

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });


                        myFireBaseDB.child("Category").child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    categoryAdapter.notifyDataSetChanged();
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
    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlCategory.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    if (cat != null) {
                        arrlCategory.add(cat);
                    }
                }
                categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi Firebase
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
