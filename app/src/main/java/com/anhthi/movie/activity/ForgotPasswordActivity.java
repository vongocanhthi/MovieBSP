package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhthi.movie.R;
import com.anhthi.movie.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addInit();
        addEvent();
    }

    private void addEvent() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<UserResponse> call = MainActivity.service.getForgotpasswordData(edtEmail.getText().toString());
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if(response.body().getMessage().equals("Mail had send to us, We will contact to you")){
                            btnSend.setEnabled(false);
                            Toast.makeText(ForgotPasswordActivity.this, "Vui lòng kiểm tra email", Toast.LENGTH_SHORT).show();
                            ForgotPasswordActivity.this.finish();
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại !!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi quên mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void addInit() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSend);
    }

    public void Back(View view) {
        intentLogin();
    }

    private void intentLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        ForgotPasswordActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intentLogin();
    }
}
