package com.anhthi.movie.activity;

import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhthi.movie.R;
import com.anhthi.movie.model.Movie;
import com.anhthi.movie.model.MovieResponse;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetailActivity extends YouTubeBaseActivity {
    MainActivity activity = new MainActivity();
    YouTubePlayerView ytbTrailer;
    TextView txtTitle, txtName, txtViews, txtGenres, txtActor, txtDirector, txtManufacturer, txtDuration, txtLike, txtDescription, txtSeeMore;
    ImageView imgImage, imgLike;
    Movie movie;
    String API_KEY = "AIzaSyBwdvS86j8bvmqLYZ9xDY8Kzjten1lU6qw";
    int id;
    String like;
    String[] titles, link;

    int REQUEST_VIDEO = 404;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        init();
        dataIntent();
        addEventLike();

    }

    private void addEventLike() {
        if(txtLike.getText().equals("Thích")){
            imgLike.setImageResource(R.drawable.ic_like);
            txtLike.setTextColor(Color.parseColor("#ffffff"));
        }else{
            imgLike.setImageResource(R.drawable.ic_like_orange);
            txtLike.setText("Đã thích");
            txtLike.setTextColor(Color.parseColor("#fd6003"));
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
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

    private void dataIntent() {
        txtLike.setText(like);

        Call<MovieResponse> call = MainActivity.service.getMovieData();
        call.enqueue(new Callback<MovieResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MainActivity.movieArrayList.addAll(response.body().getData());

                movie = MainActivity.movieArrayList.get(id - 1);
                Picasso.get().load(movie.getImage()).into(imgImage);
                titles = movie.getTitle().split("/");
                txtTitle.setText(titles.length == 2 ? titles[0] : movie.getTitle());
                txtName.setText(titles.length == 2 ? titles[1].trim() : movie.getTitle());

                txtViews.setText("Lượt xem: " + movie.getViews());
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
            public void onFailure(Call<MovieResponse> call, Throwable t) {
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

        Bundle bundle = getIntent().getExtras();
        id = (int) bundle.get("id");
        like = (String) bundle.get("like");

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FilmDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}