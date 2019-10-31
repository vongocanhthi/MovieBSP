package com.anhthi.movie.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anhthi.movie.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    ImageView imgAvatar;
    TextView txtId, txtEmail, txtFullName;
    Button btnEdit;
    int REQUEST_CODE_PROFILE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        addInit();
        addEvents();

    }

    private void addEvents() {
        imgAvatar.setEnabled(false);

        txtId.setText(LoginActivity.sharedPreferences.getString("id", ""));
        txtEmail.setText(LoginActivity.sharedPreferences.getString("email", ""));
        txtFullName.setText(LoginActivity.sharedPreferences.getString("fullname", ""));

        // select Avatar sqlite
        MainActivity.selectAvatar(Integer.parseInt(txtId.getText().toString()), imgAvatar);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PROFILE);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnEdit.getText().toString().equals("Xác nhận")){
                    imgAvatar.setEnabled(false);
                    btnEdit.setText("Chỉnh sửa");

                    // insert image sqlite
                    try{
                        MainActivity.database.insertAvatar(Integer.parseInt(txtId.getText().toString()), MainActivity.Image_To_Byte(imgAvatar));
                    }catch (Exception e){}

                    Toast.makeText(ProfileActivity.this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                }else{
                    imgAvatar.setEnabled(true);
                    btnEdit.setText("Xác nhận");

                    // delete avatar
                    try{
                        MainActivity.deleteAvatar(Integer.parseInt(txtId.getText().toString()));
                    }catch (Exception e){}
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PROFILE && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatar.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addInit() {
        btnEdit = findViewById(R.id.btnEdit);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtId = findViewById(R.id.txtId);
        txtEmail = findViewById(R.id.txtEmail);
        txtFullName = findViewById(R.id.txtFullName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        ProfileActivity.this.finish();
    }
}
