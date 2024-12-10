package com.example.tpnews_ungdungdocbao;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmailSignUpActivity extends AppCompatActivity {

    EditText edSignUpEmail, edSignUpPassword, edSignUpConfirmPassword;

    ImageView imgHideSignUpPassword, imgHideSignUpConfirmPassword;
    boolean isPasswordVisible = false;

    Button btSignUp;
    CheckBox cbTerms;

    int active = 0;

    MyDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edSignUpEmail = findViewById(R.id.edSignUpEmail);
        edSignUpPassword = findViewById(R.id.edSignUpPassword);
        edSignUpConfirmPassword = findViewById(R.id.edSignUpConfirmPassword);
        imgHideSignUpPassword = findViewById(R.id.imgHidePasswordSignUp);
        imgHideSignUpConfirmPassword = findViewById(R.id.imgHideConfirmPasswordSignUp);
        btSignUp = findViewById(R.id.btSignUp);
        cbTerms = findViewById(R.id.cbTerms);

        //Giấu Password
        hidePassword();
        hideConfirmPassword();

        //Đăng ký xuống Database
        myDB = new MyDatabase(EmailSignUpActivity.this);
        signUp();
    }

    //Giấu Password
    public void hidePassword() {
        imgHideSignUpPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    edSignUpPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imgHideSignUpPassword.setImageResource(R.drawable.close);
                } else {
                    edSignUpPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgHideSignUpPassword.setImageResource(R.drawable.open);
                }
                edSignUpPassword.setSelection(edSignUpPassword.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });
    }


    //Giấu Password
    public void hideConfirmPassword() {
        imgHideSignUpConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    edSignUpConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imgHideSignUpConfirmPassword.setImageResource(R.drawable.close);
                } else {
                    edSignUpConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgHideSignUpConfirmPassword.setImageResource(R.drawable.open);
                }
                edSignUpConfirmPassword.setSelection(edSignUpConfirmPassword.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });
    }


    //Đăng ký xuống Database
    public void signUp() {
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edSignUpEmail.getText().toString();
                String password = edSignUpPassword.getText().toString();
                String confirmPassword = edSignUpConfirmPassword.getText().toString();


                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(EmailSignUpActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                } if (!password.equals(confirmPassword)) {
                    Toast.makeText(EmailSignUpActivity.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    return;
                } if (!cbTerms.isChecked()) {
                    Toast.makeText(EmailSignUpActivity.this, "Chưa đồng ý điều khoản!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Boolean checkUsername = myDB.checkUsername(username);
                    if (!checkUsername) {
                        myDB.addUser(username, password, active);
                        Toast.makeText(EmailSignUpActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        edSignUpEmail.setText(""); edSignUpPassword.setText(""); edSignUpConfirmPassword.setText("");
                        Intent intent = new Intent(EmailSignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EmailSignUpActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                        edSignUpEmail.setText(""); edSignUpPassword.setText(""); edSignUpConfirmPassword.setText("");
                    }
                }
            }
        });
    }
}