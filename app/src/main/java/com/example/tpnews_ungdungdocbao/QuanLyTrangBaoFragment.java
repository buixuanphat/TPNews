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
import android.widget.AdapterView;
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
        load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_trang_bao, container, false);

        // Khởi tạo view
        edtInput = view.findViewById(R.id.edtInputOutlet);
        progressBar = view.findViewById(R.id.progressBarOutlet);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        btnAdd = view.findViewById(R.id.btnAddOutlet);
        btnCancel = view.findViewById(R.id.btnCancelOutlet);
        btnSave = view.findViewById(R.id.btnSaveOutlet);
        btnAddImage = view.findViewById(R.id.btnLogoOutlet);
        imgInputImage = view.findViewById(R.id.imgvLogoOutlet);
        lv = view.findViewById(R.id.lvOutlet);

        // Khởi tạo danh sách trang báo
        arrlOutlet = new ArrayList<>();
        outletAdapter = new OutletAdapter(getContext(), R.layout.layout_outlet, arrlOutlet);
        lv.setAdapter(outletAdapter);

        // Xử lý nút thêm bài viết
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdd.setVisibility(View.GONE);
                edtInput.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnAddImage.setVisibility(View.VISIBLE);
            }
        });

        // Mở intent chọn hình ảnh từ thiết bị
        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 504);
        });

        // Xử lý nút lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtInput.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
                } else if (imageURI == null) {
                    Toast.makeText(getContext(), "Vui lòng chọn hình ảnh!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String key = myFireBaseDB.child("Outlet").push().getKey();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageURI);
                        Outlet outlet = new Outlet(key, edtInput.getText().toString(), convertBitmapToBase64(bitmap));
                        myFireBaseDB.child("Outlet").child(key).setValue(outlet, (error, ref) -> {
                            if (error == null) {
                                resetForm();
                                load();
                                Toast.makeText(getContext(), "Lưu trang báo thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn muốn thực hiện hành động nào");

                builder.setPositiveButton("Sửa", (dialog, which) -> {
                    // Hiển thị các thành phần nhập liệu
                    btnAdd.setVisibility(View.GONE);
                    edtInput.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnAddImage.setVisibility(View.VISIBLE);

                    // Hiển thị dữ liệu trang báo được chọn
                    Outlet outletSelected = arrlOutlet.get(position);
                    edtInput.setText(outletSelected.getName());

                    // Chuyển đổi Base64 thành Bitmap để hiển thị hình ảnh
                    imgInputImage.setImageBitmap(convertBase64ToBitmap(outletSelected.getLogo()));

                    // Xử lý nút lưu
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference outletRef = FirebaseDatabase.getInstance().getReference("Outlet").child(arrlOutlet.get(position).getId());
                            if (edtInput.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
                            } else if (imageURI == null) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", edtInput.getText().toString());
                                outletRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            resetForm();
                                            load();
                                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else if (imageURI != null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageURI);
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("name", edtInput.getText().toString());
                                    updates.put("logo", convertBitmapToBase64(bitmap));
                                    outletRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null) {
                                                resetForm();
                                                load();
                                                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
                });
                builder.setNegativeButton("Xóa", (dialog, which) -> {
                    String key = arrlOutlet.get(position).getId();
                    myFireBaseDB.child("Article")
                            .orderByChild("outletName")
                            .equalTo(arrlOutlet.get(position).getName())
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

                    myFireBaseDB.child("Outlet").child(key).removeValue((error, ref) -> {
                        if (error == null) {
                            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                            outletAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Không thành công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                builder.setNeutralButton("Hủy", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    private void resetForm() {
        edtInput.setText("");
        imageURI = null;
        btnAdd.setVisibility(View.VISIBLE);
        edtInput.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnAddImage.setVisibility(View.GONE);
        imgInputImage.setVisibility(View.GONE);
    }

    private void load() {
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
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
