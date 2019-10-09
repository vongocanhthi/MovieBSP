package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anhthi.movie.R;

public class RegiterActivity extends AppCompatActivity {
    EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);

        adControl();
        addEvent();
    }

    private void addEvent() {
        btnRegiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KiemTraRong();
            }
        });
    }
    private  boolean KiemTraRong(){
        String fullNameInput = edtFullName.getText().toString().trim();
        String emailInput = edtEmail.getText().toString().trim();
        String passwordInput = edtPassword.getText().toString().trim();
        String confirmPasswordInput = edtConfirmPassword.getText().toString().trim();

        if(fullNameInput.isEmpty()){
            edtFullName.setError("Bạn chưa điền Họ tên");
        }
        if(emailInput.isEmpty()){
            edtEmail.setError("Bạn chưa điền Email");
        }
        if(passwordInput.isEmpty()){
            edtPassword.setError("Bạn chưa tạo Mật khẩu");
        }
        if(confirmPasswordInput.isEmpty()){
            edtConfirmPassword.setError("Bạn chưa Xác nhận mật khẩu");
        }
        if(confirmPasswordInput.equalsIgnoreCase(passwordInput) == false && confirmPasswordInput.isEmpty() == false) {
            edtConfirmPassword.setError("Xác nhận mật khẩu không chính xác");
        }
        return true;
    }

    private void adControl() {
        edtFullName = findViewById(R.id.editTextFullName);
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegiter = findViewById(R.id.buttonRegiter);
    }
}
