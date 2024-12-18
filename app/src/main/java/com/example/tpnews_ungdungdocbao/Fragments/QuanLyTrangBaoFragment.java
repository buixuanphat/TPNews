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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tpnews_ungdungdocbao.Data.Static;
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

import com.example.tpnews_ungdungdocbao.Adapters.OutletAdapter;

public class QuanLyTrangBaoFragment extends Fragment {

    private Button btnAdd, btnSave, btnAddImage, btnCancel;
    private EditText edtInput;
    private ImageView imgInputImage;
    private Uri imageURI;
    private DatabaseReference myFireBaseDB;
    private ListView lv;
    private ArrayList<Outlet> arrlOutlet;
    private ProgressBar progressBar;
    private OutletAdapter outletAdapter;

    @Override
    public void onResume() {
        super.onResume();
        Outlet.loadOutlet(myFireBaseDB, arrlOutlet, outletAdapter, progressBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_trang_bao, container, false);

        // Ánh xạ giao diện
        edtInput = view.findViewById(R.id.edtInputOutlet);
        progressBar = view.findViewById(R.id.progressBarOutlet);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAdd = view.findViewById(R.id.btnAddOutlet);
        btnCancel = view.findViewById(R.id.btnCancelOutlet);
        btnSave = view.findViewById(R.id.btnSaveOutlet);
        btnAddImage = view.findViewById(R.id.btnLogoOutlet);
        imgInputImage = view.findViewById(R.id.imgvLogoOutlet);
        lv = view.findViewById(R.id.lvOutlet);

        // Khởi tạo listview
        arrlOutlet = new ArrayList<>();
        outletAdapter = new OutletAdapter(getContext(), R.layout.layout_outlet, arrlOutlet);
        lv.setAdapter(outletAdapter);

        // Thêm bài viết
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               appearForm();
            }
        });

        // Mở intent chọn hình ảnh từ thiết bị
        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 504);
        });

        // Lưu bài viết
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtInput.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
                }
                else if (imageURI == null)
                {
                    Toast.makeText(getContext(), "Vui lòng chọn hình ảnh!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Outlet.addOutlet(myFireBaseDB, edtInput, imageURI, getContext());
                    Outlet.loadOutlet(myFireBaseDB, arrlOutlet, outletAdapter, progressBar);
                    hideForm();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideForm();
            }
        });
        // Xử lý sự kiện khi item được chọn
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn muốn thực hiện hành động nào");
                builder.setPositiveButton("Sửa", (dialog, which) -> {
                    appearForm();
                    Outlet outletSelected = arrlOutlet.get(position);
                    edtInput.setText(outletSelected.getName());
                    imgInputImage.setImageBitmap(Static.convertBase64ToBitmap(outletSelected.getLogo()));
                    imgInputImage.setVisibility(View.VISIBLE);

                    // Xử lý nút lưu
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edtInput.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                            Outlet.editOultlet(arrlOutlet.get(position).getId(), edtInput, imageURI, getContext());
                            Outlet.loadOutlet(myFireBaseDB, arrlOutlet, outletAdapter, progressBar);
                            hideForm();
                            }
                        }
                    });
                });
                builder.setNegativeButton("Xóa", (dialog, which) -> {
                    Outlet.deleteOutlet(myFireBaseDB, arrlOutlet.get(position), getContext());
                    Outlet.loadOutlet(myFireBaseDB, arrlOutlet, outletAdapter, progressBar);
                });
                builder.setNeutralButton("Hủy", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 504 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            imgInputImage.setImageURI(imageURI);
            imgInputImage.setVisibility(View.VISIBLE);
        }
    }
    private void appearForm()
    {
        btnAdd.setVisibility(View.GONE);
        edtInput.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        btnAddImage.setVisibility(View.VISIBLE);
        edtInput.requestFocus();
    }
     private void hideForm()
     {
         edtInput.setText("");
         imageURI = null;
         btnAdd.setVisibility(View.VISIBLE);
         edtInput.setVisibility(View.GONE);
         btnSave.setVisibility(View.GONE);
         btnCancel.setVisibility(View.GONE);
         btnAddImage.setVisibility(View.GONE);
         imgInputImage.setVisibility(View.GONE);
     }
}
