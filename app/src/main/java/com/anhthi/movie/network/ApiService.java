package com.anhthi.movie.network;

import com.anhthi.movie.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {

    @Headers("app_token: dCuW7UQMbdvpcBDfzolAOSGFIcAec11a")
    @GET("movie/list")
    Call<MovieResponse> getMovieData();

}
