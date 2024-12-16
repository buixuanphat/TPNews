package com.example.tpnews_ungdungdocbao;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuanLyUserFragment extends Fragment {

    private ListView lvUser;
    private TextView tvUsername;
    private EditText edUsername, edPassword;
    private Button btAddUser, btSave, btCancel, btUpdateUser;
    private ProgressBar progressBar;

    private Switch swActive;
    private MyDatabase myDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Load dữ liệu
    @Override
    public void onResume() {
        super.onResume();
        loadDataUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quan_ly_user, container, false);

        //Ánh xạ
        lvUser = view.findViewById(R.id.lvUser);
        tvUsername = view.findViewById(R.id.tvUsername);
        edUsername = view.findViewById(R.id.edUsername);
        edPassword = view.findViewById(R.id.edPassword);
        btAddUser = view.findViewById(R.id.btAddUser);
        btSave = view.findViewById(R.id.btSave);
        btCancel = view.findViewById(R.id.btCancel);
        btUpdateUser = view.findViewById(R.id.btUpdateUser);
        progressBar = view.findViewById(R.id.progressBar);
        swActive = view.findViewById(R.id.swActive);

        myDB = new MyDatabase(getContext());

        //Hiện tiện ích
        btAddUser.setOnClickListener(v -> {
            showData();
        });

        //Ẩn tiện tích
        btCancel.setOnClickListener(v -> {
            hideData();
        });


        //Thêm User
        btSave.setOnClickListener(v -> {
            String username = edUsername.getText().toString();
            String password = edPassword.getText().toString();
            Boolean checkAccount = myDB.checkUsername(username);

            int active;
            if (swActive.isChecked()) {
                active = 1;
            } else {
                active = 0;
            }
            swActive.setChecked(false);

            if (!username.isEmpty() && !password.isEmpty()) {
                if (!checkAccount) {
                    myDB.addUser(username, password, active);
                    Toast.makeText(getContext(), "Thêm user thành công!", Toast.LENGTH_SHORT).show();
                    loadDataUser();
                    hideData();
                } else {
                    Toast.makeText(getContext(), "User đã tổn tại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });


        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                //Thiết lập tiêu đề và nội dung
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn muốn thực hiện hành động nào");

                //Thêm nút xóa
                builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = (String) parent.getItemAtPosition(position);
                        String result = username.substring(0, username.indexOf('_'));


                        boolean isDeleted = myDB.deleteUser(result);
                        if (isDeleted) {
                            loadDataUser();
                            Toast.makeText(getContext(), "Xóa user thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Thêm nút sửa
                builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btAddUser.setVisibility(View.GONE);
                        btUpdateUser.setVisibility(View.VISIBLE);
                        tvUsername.setVisibility(View.VISIBLE);
                        edPassword.setVisibility(View.VISIBLE);
                        btCancel.setVisibility(View.VISIBLE);
                        swActive.setVisibility(View.VISIBLE);


                        String user = (String) parent.getItemAtPosition(position);
                        String[] parts = user.split("_");
                        String username = parts[0];
                        String password = parts[1];
                        String active = parts[2];


                        tvUsername.setText(username);
                        edPassword.setText(password);
                        int actAdmin = Integer.parseInt(active);
                        if (actAdmin == 0) {
                            swActive.setChecked(false);
                        } else {
                            swActive.setChecked(true);
                        }

                        btUpdateUser.setOnClickListener(v -> {
                            String newPassword = edPassword.getText().toString();
                            int newActive;
                            if (swActive.isChecked()) {
                                newActive = 1;
                            } else {
                                newActive = 0;
                            }

                            boolean isUpdate = myDB.updateUser(username, newPassword, newActive);
                            loadDataUser();
                            hideData();
                            if (isUpdate) {
                                Toast.makeText(getContext(), "Cập nhật user thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Cập nhật user thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                // Thêm nút hủy
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


    //Hiện tiện ích
    public void showData() {
        btAddUser.setVisibility(View.GONE);
        edUsername.setVisibility(View.VISIBLE);
        edPassword.setVisibility(View.VISIBLE);
        btSave.setVisibility(View.VISIBLE);
        btCancel.setVisibility(View.VISIBLE);
        swActive.setVisibility(View.VISIBLE);
    }


    //Ẩn tiện ích
    public void hideData() {
        btAddUser.setVisibility(View.VISIBLE);
        edUsername.setVisibility(View.GONE);
        edPassword.setVisibility(View.GONE);
        btSave.setVisibility(View.GONE);
        btCancel.setVisibility(View.GONE);
        btUpdateUser.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        swActive.setVisibility(View.GONE);
        edUsername.setText("");
        edPassword.setText("");
        swActive.setChecked(false);
    }


    //Load dữ liệu
    public void loadDataUser() {
        progressBar.setVisibility(View.INVISIBLE);
        ArrayList<String> userList = new ArrayList<>();
        Cursor cursor = myDB.readAllUser();


        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow("admin_active"));

                userList.add(username + "_" + password + "_" + active);
            } while (cursor.moveToNext());
            progressBar.setVisibility(View.GONE);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userList);
        lvUser.setAdapter(adapter);
    }
}