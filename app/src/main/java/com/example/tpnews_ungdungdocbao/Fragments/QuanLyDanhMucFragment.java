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
        Category.loadCategories(myFireBaseDB, arrlCategory, categoryAdapter, progressBar);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_danh_muc, container, false);
        // Ánh xạ giao diện
        progressBar = view.findViewById(R.id.progressBar);
        lvCategory = view.findViewById(R.id.lvCategory);
        btnAdd = view.findViewById(R.id.btnAddCat);
        btnSave = view.findViewById(R.id.btnSaveCat);
        btnCancel = view.findViewById(R.id.btnCancelCat);
        edtInput = view.findViewById(R.id.edtInputCat);

        //Thiết lập listview
        arrlCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), R.layout.layout_category, arrlCategory);
        lvCategory.setAdapter(categoryAdapter);


        btnAdd.setOnClickListener(v -> {
            appearForm();
        });


        btnSave.setOnClickListener(v -> {
            // Lưu danh mục
            if (edtInput.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
            else {
                Category.addCategory(myFireBaseDB, edtInput, getContext());
                hideForm();
            }
        });

        // Hủy việc thêm danh mục
        btnCancel.setOnClickListener(v -> {
            hideForm();
        });

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn muốn thực hiện hành động nào");

                // Thêm nút "Sửa"
                builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appearForm();
                        edtInput.setText(arrlCategory.get(position).getName());
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtInput.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Category.editCategory(myFireBaseDB, arrlCategory.get(position), edtInput, getContext());
                                    Category.loadCategories(myFireBaseDB, arrlCategory, categoryAdapter, progressBar);
                                    hideForm();
                                }
                            }
                        });


                    }
                });

                // Thêm nút "Xóa"
                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category.deleteCategory(myFireBaseDB, arrlCategory.get(position), getContext());
                        Category.loadCategories(myFireBaseDB, arrlCategory, categoryAdapter, progressBar);
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

    //Hiển thị giao diện nhập danh mục
    private void appearForm() {
        btnAdd.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        edtInput.setVisibility(View.VISIBLE);
        edtInput.requestFocus();
    }

    private void hideForm() {
        btnAdd.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        edtInput.setVisibility(View.GONE);
        edtInput.setText("");
    }


}
