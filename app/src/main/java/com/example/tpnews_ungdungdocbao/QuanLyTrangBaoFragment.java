package com.example.tpnews_ungdungdocbao;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuanLyTrangBaoFragment extends Fragment {

    Button btnAdd, btnSave, btnCancel, btnImage;
    EditText edtInput;
    ImageView imgvLogoOutlet;
    String logoLink;
    Uri selectedImageUri; // Store the selected image URI

    ListView lvOutlet;
    ProgressBar progressBarOutlet;

    private DatabaseReference myFireBaseDB;

    ArrayList<Outlet> arrlOutlet;
    OutletAdapter olAdapter;

    // Image picker launcher to handle the result from image selection
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        imgvLogoOutlet.setImageURI(imageUri);
                        imgvLogoOutlet.setVisibility(View.VISIBLE);
                        selectedImageUri = imageUri; // Store the selected image URI
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy dữ liệu ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOutlet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_trang_bao, container, false);


        // Initialize UI components
        progressBarOutlet = view.findViewById(R.id.progressBarOutlet);
        btnAdd = view.findViewById(R.id.btnAddOutlet);
        btnSave = view.findViewById(R.id.btnSaveOutlet);
        btnCancel = view.findViewById(R.id.btnCancelOutlet);
        btnImage = view.findViewById(R.id.btnLogoOutlet);
        edtInput = view.findViewById(R.id.edtInputOutlet);
        imgvLogoOutlet = view.findViewById(R.id.imgvLogoOutlet);
        lvOutlet = view.findViewById(R.id.lvOutlet);

        arrlOutlet = new ArrayList<>();
        olAdapter = new OutletAdapter(getContext(), R.layout.layout_outlet, arrlOutlet);
        lvOutlet.setAdapter(olAdapter);



        // Set listeners for buttons
        btnAdd.setOnClickListener(v -> {
            btnAdd.setVisibility(View.GONE);
            edtInput.setVisibility(View.VISIBLE);
            btnImage.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            edtInput.setText("");
        });

        // Open image picker when "Select Image" button is clicked
        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Handle save button click
        btnSave.setOnClickListener(v -> {
            if (edtInput.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
            } else if (selectedImageUri == null) { // Ensure image has been selected
                Toast.makeText(getContext(), "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            } else {
                uploadImageToCloudinary(selectedImageUri);
            }
            btnAdd.setVisibility(View.VISIBLE);
            edtInput.setVisibility(View.GONE);
            btnImage.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            imgvLogoOutlet.setVisibility(View.GONE);
        });

        // Reset the form when "Cancel" button is clicked
        btnCancel.setOnClickListener(v -> resetForm());

        lvOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        edtInput.setVisibility(View.VISIBLE);
                        btnImage.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                        imgvLogoOutlet.setVisibility(View.VISIBLE);
                        edtInput.setText("");

                        edtInput.setText(arrlOutlet.get(position).getName());
                        Glide.with(getContext()).load(arrlOutlet.get(position).getLogoLink()).into(imgvLogoOutlet);

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (edtInput.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập tên trang báo", Toast.LENGTH_SHORT).show();
                                } else {
                                    String id = arrlOutlet.get(position).getId();
                                    updateImageToCloudinary(selectedImageUri, id);
                                }
                                btnAdd.setVisibility(View.VISIBLE);
                                edtInput.setVisibility(View.GONE);
                                btnImage.setVisibility(View.GONE);
                                btnSave.setVisibility(View.GONE);
                                btnCancel.setVisibility(View.GONE);
                                imgvLogoOutlet.setVisibility(View.GONE);
                            }
                        });


                    }
                });

                // Thêm nút "Xóa"
                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String key = arrlOutlet.get(position).getId();
                        myFireBaseDB.child("Outlet").child(key).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    olAdapter.notifyDataSetChanged();
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

    private void uploadImageToCloudinary(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Không có hình ảnh để upload", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaManager.get().upload(imageUri)
                .unsigned("OutletImage")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(getContext(), "Bắt đầu upload", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String uploadedUrl = (String) resultData.get("secure_url");
                        Toast.makeText(getContext(), "Upload thành công", Toast.LENGTH_LONG).show();
                        logoLink = uploadedUrl;
                        String key = myFireBaseDB.child("Outlet").push().getKey();
                        Outlet outlet = new Outlet(key, edtInput.getText().toString(), logoLink);
                        myFireBaseDB.child("Outlet").child(key).setValue(outlet, new DatabaseReference.CompletionListener() {
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
                    }
                }).dispatch();
        selectedImageUri = null;
    }

    private void updateImageToCloudinary(Uri imageUri, String id) {
        if (imageUri != null) {
            MediaManager.get().upload(imageUri)
                    .unsigned("OutletImage")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Toast.makeText(getContext(), "Bắt đầu upload", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String uploadedUrl = (String) resultData.get("secure_url");
                            Toast.makeText(getContext(), "Upload thành công", Toast.LENGTH_LONG).show();
                            logoLink = uploadedUrl;
                            DatabaseReference editOutlet = myFireBaseDB.child("Outlet").child(id);
                            Map<String, Object> update = new HashMap<>();
                            update.put("name", edtInput.getText().toString());
                            update.put("logoLink", logoLink);
                            editOutlet.updateChildren(update, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        olAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getContext(), "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        }
                    }).dispatch();

        } else if(imageUri == null){
            DatabaseReference editOutlet = myFireBaseDB.child("Outlet").child(id);
            Map<String, Object> update = new HashMap<>();
            update.put("name", edtInput.getText().toString());
            editOutlet.updateChildren(update, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        olAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Không thành công:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        selectedImageUri = null;
    }

    private void loadOutlet() {
        // Đọc dữ liệu từ Firebase
        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarOutlet.setVisibility(View.VISIBLE);
                arrlOutlet.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        arrlOutlet.add(outlet);
                    }
                }
                olAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                progressBarOutlet.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi Firebase
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
            }

    private void resetForm() {
        btnAdd.setVisibility(View.VISIBLE);
        edtInput.setVisibility(View.GONE);
        btnImage.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        imgvLogoOutlet.setVisibility(View.GONE);
        edtInput.setText("");
        imgvLogoOutlet.setImageDrawable(null);
        selectedImageUri = null;
    }
}
