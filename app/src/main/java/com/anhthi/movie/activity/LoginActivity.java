package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhthi.movie.R;
import com.anhthi.movie.model.User;
import com.anhthi.movie.model.UserResponse;

import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnLogin, btnForgotPassword, btnFacebook;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addInit();
        addEvent();
    }

    private void addEvent() {
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không được để trống !!!", Toast.LENGTH_SHORT).show();
                } else {
                    Call<UserResponse> call = MainActivity.service.getLoginData(email, password);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.body().getMessage().equals("Email cannot found") || response.body().getMessage().equals("Wrong password")) {
                                Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác !!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.commit();
                                LoginActivity.this.finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Lỗi đăng nhâp", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    private void addInit() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnFacebook = findViewById(R.id.btnFacebook);

        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        edtEmail.setText(sharedPreferences.getString("email", ""));
        edtPassword.setText(sharedPreferences.getString("password", ""));
    }

    public void redirectRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }
}
