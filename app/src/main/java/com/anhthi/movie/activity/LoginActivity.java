package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anhthi.movie.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void redirectRegiter(View view) {
        Intent intent = new Intent(LoginActivity.this, RegiterActivity.class);
        startActivity(intent);
    }
}
