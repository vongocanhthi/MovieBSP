package com.anhthi.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anhthi.movie.R;
import com.anhthi.movie.model.UserResponse;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnLogin, btnForgotPassword;
    LoginButton btnFacebook;
    static SharedPreferences sharedPreferences;
    CallbackManager callbackManager;
    static boolean isLoginFB = false;
    String id, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addInit();
        addEvent();
        setLoginButton();
    }

    private void setLoginButton() {
        btnFacebook.setReadPermissions(Arrays.asList("public_profile", "email"));

        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                isLoginFB = true;
                MainActivity.isLogin = true;
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();

                result();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void result() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON", response.getJSONObject().toString());
                try {
                    //id = object.getString("id");
                    name = object.getString("name");
                    //email = object.getString("email");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("fullname", name);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
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

                                //profile
                                editor.putString("id", response.body().getData().getId());
                                editor.putString("fullname", response.body().getData().getFullName());
                                editor.commit();

                                MainActivity.isLogin = true;
                                intentLogin();

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
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        LoginActivity.this.finish();
    }

    private void back() {
        if(!MainActivity.isLogin){
            intentLogin();
        }else{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            LoginActivity.this.finish();
        }
    }

    private void intentLogin() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        LoginActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
