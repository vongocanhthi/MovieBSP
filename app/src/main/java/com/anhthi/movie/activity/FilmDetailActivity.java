package com.anhthi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.anhthi.movie.R;
import com.anhthi.movie.model.Movie;
import com.anhthi.movie.model.MovieResponse;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetailActivity extends YouTubeBaseActivity {
    MainActivity activity = new MainActivity();
    YouTubePlayerView ytbTrailer;
    TextView txtTitle, txtName, txtViews, txtGenres, txtActor, txtDirector, txtManufacturer, txtDuration, txtLike, txtDescription, txtSeeMore;
    ImageView imgImage, imgLike;
    LinearLayout llLike;
    Movie movie;
    String API_KEY = "AIzaSyBwdvS86j8bvmqLYZ9xDY8Kzjten1lU6qw";
    int id, views, viewsNew;
    String like;
    String[] titles, link;

    int REQUEST_VIDEO = 404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        init();
        callFilmDetailData();
        addEventLike();

    }

    private void intentLogin() {
        Intent intent = new Intent(FilmDetailActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void addEventLike() {
        if(txtLike.getText().equals("Thích")){
            imgLike.setImageResource(R.drawable.ic_like);
            txtLike.setTextColor(Color.parseColor("#ffffff"));
        }else{
            imgLike.setImageResource(R.drawable.ic_like_orange);
            //txtLike.setText("Đã thích");
            txtLike.setTextColor(Color.parseColor("#fd6003"));
        }

        llLike.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(txtLike.getText().equals("Thích")){
                    imgLike.setImageResource(R.drawable.ic_like_orange);
                    txtLike.setText("Đã thích");
                    txtLike.setTextColor(Color.parseColor("#fd6003"));
                    activity.insertLike(id);
                }else{
                    imgLike.setImageResource(R.drawable.ic_like);
                    txtLike.setText("Thích");
                    txtLike.setTextColor(Color.parseColor("#ffffff"));
                    activity.deleteLike(id);
                }
            }
        });
    }

    private void callFilmDetailData() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        like = bundle.getString("like");
        views = bundle.getInt("views");
        txtLike.setText(like);

        txtViews.setText((views + 1) + "");
        viewsNew = Integer.parseInt(txtViews.getText().toString());
        activity.database.QueryData("UPDATE Views SET views = "+ viewsNew +" WHERE id = "+ id +"");

        Call<MovieResponse> call = MainActivity.service.getMovieData();
        call.enqueue(new Callback<MovieResponse>() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(@NotNull Call<MovieResponse> call, Response<MovieResponse> response) {
                assert response.body() != null;
                MainActivity.movieArrayList.addAll(response.body().getData());

                movie = MainActivity.movieArrayList.get(id - 1);
                Picasso.get().load(movie.getImage()).into(imgImage);
                titles = movie.getTitle().split("/");
                txtTitle.setText(titles.length == 2 ? titles[0] : movie.getTitle());
                txtName.setText(titles.length == 2 ? titles[1].trim() : movie.getTitle());

                txtGenres.setText(movie.getCategory());
                txtActor.setText(movie.getActor());
                txtDirector.setText(movie.getDirector());
                txtManufacturer.setText(movie.getManufacturer());
                txtDuration.setText(movie.getDuration() + " minute");

                txtDescription.setText(movie.getDescription());
                seeMoreEvent(txtDescription.getLineCount());

                link = movie.getLink().split("v=");

                ytbTrailer.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(link[1]);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                        if (youTubeInitializationResult.isUserRecoverableError()) {
                        youTubeInitializationResult.getErrorDialog(FilmDetailActivity.this, REQUEST_VIDEO);
                        } else {
                            Toast.makeText(FilmDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call<MovieResponse> call, Throwable t) {
                Toast.makeText(FilmDetailActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void seeMoreEvent(int lineCount) {
        if(lineCount <= 3){
            txtSeeMore.setVisibility(View.GONE);
        }else{
            txtDescription.setMaxLines(3);
            txtSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkMaxLineDescription();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkMaxLineDescription() {
        if(txtSeeMore.getText().equals("Xem thêm")){
            txtDescription.setMaxLines(Integer.MAX_VALUE);
            txtSeeMore.setText("Thu gọn");
        }else{
            txtDescription.setMaxLines(3);
            txtSeeMore.setText("Xem thêm");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO) {
            ytbTrailer.initialize(API_KEY, (YouTubePlayer.OnInitializedListener) FilmDetailActivity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        ytbTrailer = findViewById(R.id.ytbTrailer);
        txtTitle = findViewById(R.id.txtTitle);
        txtName = findViewById(R.id.txtName);
        txtViews = findViewById(R.id.txtViews);
        txtGenres = findViewById(R.id.txtGenres);
        txtActor = findViewById(R.id.txtActor);
        txtDirector = findViewById(R.id.txtDirector);
        txtManufacturer = findViewById(R.id.txtManufacturer);
        txtDuration = findViewById(R.id.txtDuration);
        txtLike = findViewById(R.id.txtLike);
        txtDescription = findViewById(R.id.txtDescription);
        txtSeeMore = findViewById(R.id.txtSeeMore);
        imgImage = findViewById(R.id.imgImage);
        imgLike = findViewById(R.id.imgLike);
        llLike = findViewById(R.id.llLike);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FilmDetailActivity.this, MainActivity.class);
        startActivity(intent);
        FilmDetailActivity.this.finish();
    }
}
