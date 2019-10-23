package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhthi.movie.R;
import com.anhthi.movie.model.User;
import com.anhthi.movie.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        adControl();
        addEvent();
    }

    private void addEvent() {
        btnRegiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkError();


            }
        });
    }
    private void checkError(){
        String fullname = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if(fullname.isEmpty()){
            edtFullName.setError("Bạn chưa điền Họ tên");
        }else if(email.isEmpty()){
            edtEmail.setError("Bạn chưa điền Email");
        } else if(password.isEmpty()){
            edtPassword.setError("Bạn chưa tạo Mật khẩu");
        } else if(confirmPassword.isEmpty()){
            edtConfirmPassword.setError("Bạn chưa Xác nhận mật khẩu");
        } else if(!confirmPassword.equalsIgnoreCase(password) && !confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Xác nhận mật khẩu không chính xác");
        }else{
            callRegisterData(fullname, email, password);
        }
    }

    private void callRegisterData(final String fullname, final String email, final String password) {
        Call<UserResponse> call = MainActivity.service.getRegisterData(fullname, email, password);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.body().getMessage().equals("Email has been existed")){
                    Toast.makeText(RegisterActivity.this, "Email đã tồn tại !!!", Toast.LENGTH_SHORT).show();
                }else{
                    User user = new User();
                    user.setFullName(fullname);
                    user.setEmail(email);
                    user.setPassword(password);
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi đăng ký !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adControl() {
        edtFullName = findViewById(R.id.editTextFullName);
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegiter = findViewById(R.id.buttonRegiter);
    }


    public void Back(View view) {
        intentLogin();
    }

    private void intentLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        RegisterActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intentLogin();
    }
}
