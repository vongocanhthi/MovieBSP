package com.anhthi.movie.network;

import com.anhthi.movie.model.MovieResponse;
import com.anhthi.movie.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    String APP_TOKEN = "app_token: dCuW7UQMbdvpcBDfzolAOSGFIcAec11a";

    // danh sách movie
    @Headers(APP_TOKEN)
    @GET("movie/list")
    Call<MovieResponse> getMovieData();

    // danh sách movie scroll
    @Headers(APP_TOKEN)
    @FormUrlEncoded
    @POST("movie/list")
    Call<MovieResponse> getMovieDataScroll(@Field("per_page") Integer per_page);

    // Đăng nhập
    @Headers(APP_TOKEN)
    @FormUrlEncoded
    @POST("user/login")
    Call<UserResponse> getLoginData(@Field("email") String email,
                                    @Field("password") String password);

    // Đăng ký
    @Headers(APP_TOKEN)
    @FormUrlEncoded
    @POST("user/registry")
    Call<UserResponse> getRegisterData(@Field("full_name") String fullname,
                                       @Field("email") String email,
                                       @Field("password") String password);

    // Quên mật khẩu
    @Headers(APP_TOKEN)
    @FormUrlEncoded
    @POST("user/forgot-password")
    Call<UserResponse> getForgotpasswordData(@Field("email") String email);

}
