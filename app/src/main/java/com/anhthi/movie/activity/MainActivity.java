package com.anhthi.movie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.anhthi.movie.R;
import com.anhthi.movie.adapter.MovieAdapter;
import com.anhthi.movie.database.Database;
import com.anhthi.movie.model.Movie;
import com.anhthi.movie.model.MovieResponse;
import com.anhthi.movie.network.ApiService;
import com.anhthi.movie.network.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView rccMovie;
    static ArrayList<Movie> movieArrayList;
    MovieAdapter movieAdapter;
    public static Database database;

    static ApiService service = RetrofitClientInstance.getRetrofitClientInstance().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        init();
        callDataAPI();

    }

    // Init
    private void init() {
        rccMovie = findViewById(R.id.rccMovie);
        rccMovie.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(drawable);
        rccMovie.addItemDecoration(dividerItemDecoration);
        movieArrayList = new ArrayList<>();
        movieAdapter = new MovieAdapter(MainActivity.this, movieArrayList);
        rccMovie.setAdapter(movieAdapter);

        database = new Database(MainActivity.this, "movie.sqlite", null, 1);
        // create database
        database.QueryData("CREATE TABLE IF NOT EXISTS Movie(id_like INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER)");

    }

    //select SQLite
    public void selectLike(int id, TextView like) {
        Cursor cursor = MainActivity.database.getData("SELECT * FROM Movie");
        while (cursor.moveToNext()){
            //int id_like = cursor.getInt(0);
            int id_movie = cursor.getInt(1);
            if(id == id_movie){
                like.setText("Đã thích");
            }
        }
    }

    //update SQLite
//    public void update(){
//        database.QueryData("UPDATE Movie SET like = '"+ like +"' WHERE id = '"+ id +"'");
//    }

    //insert SQLite
    public void insertLike(int id){
        database.QueryData("INSERT INTO Movie VALUES(null, "+ id +")");
    }

    //delete SQLite
    public void deleteLike(int id){
        database.QueryData("DELETE FROM Movie WHERE id = "+ id +"");
    }

    // Get data intent
    public void getDataFromItemFilm(int id, String like) {
        Intent intent = new Intent(MainActivity.this, FilmDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("like",like);
        startActivity(intent);
    }

    //call API
    private void callDataAPI() {
        Call<MovieResponse> call = service.getMovieData();
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                movieArrayList.addAll(response.body().getData());

                movieAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
