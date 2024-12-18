package com.example.tpnews_ungdungdocbao.Views;

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

import com.example.tpnews_ungdungdocbao.Data.MyDatabase;
import com.example.tpnews_ungdungdocbao.R;

public class SignInActivity extends AppCompatActivity {

    EditText edSignInEmail, edSignInPassword;

    Button btSignIn;
    CheckBox cbTerms;

    ImageView imgHideSignInPassword;
    boolean isPasswordVisible = false;

    //Đọc dữ liệu
    MyDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edSignInEmail = findViewById(R.id.edSignInEmail);
        edSignInPassword = findViewById(R.id.edSignInPassword);
        btSignIn = findViewById(R.id.btSignIn);
        imgHideSignInPassword = findViewById(R.id.imgHidePasswordSignIn);
        cbTerms = findViewById(R.id.cbTerms);

        //Đăng nhập xuống Database
        myDB = new MyDatabase(SignInActivity.this);
        signIn();


        //Giấu Password
        hidePassword();

    }

    //Giấu Password
    public void hidePassword() {
        imgHideSignInPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    edSignInPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imgHideSignInPassword.setImageResource(R.drawable.close);
                } else {
                    edSignInPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgHideSignInPassword.setImageResource(R.drawable.open);
                }
                edSignInPassword.setSelection(edSignInPassword.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });
    }


    //Đăng nhập xuống Database
    public void signIn() {
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edSignInEmail.getText().toString();
                String password = edSignInPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                } if (!cbTerms.isChecked()) {
                    Toast.makeText(SignInActivity.this, "Chưa đồng ý điều khoản!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Boolean checkUsernamePassword = myDB.checkUsernamePassword(username, password);
                    if (checkUsernamePassword) {
                        Toast.makeText(SignInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        edSignInEmail.setText(""); edSignInPassword.setText("");
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("active", myDB.getActive(username));
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Tài khoản hoặc mật khẩu không đúng!!", Toast.LENGTH_SHORT).show();
                        edSignInEmail.setText(""); edSignInPassword.setText("");
                    }
                }
            }
        });
    }
}