package com.anhthi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anhthi.movie.R;
import com.anhthi.movie.adapter.MovieAdapter;
import com.anhthi.movie.database.Database;
import com.anhthi.movie.model.Movie;
import com.anhthi.movie.model.MovieResponse;
import com.anhthi.movie.network.ApiService;
import com.anhthi.movie.network.InternetConnection;
import com.anhthi.movie.network.RetrofitClientInstance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RelativeLayout rlloadMore;
    SwipeRefreshLayout srlMovie;
    RecyclerView rccMovie;
    static ArrayList<Movie> movieArrayList;
    MovieAdapter movieAdapter;
    public static Database database;
    int page = 1;
    boolean isEmptyData = false, isLoading = false;
    static boolean isLogin = false, isCheckInternet = false;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    static ApiService service = RetrofitClientInstance.getRetrofitClientInstance().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customActionBar();
        init();
        checkInternet();
        callDataAPI();
        refresh();
        loadMore();

    }

    private void customActionBar() {
        ActionBar actionBar = getSupportActionBar();
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        assert actionBar != null;
        actionBar.setCustomView(view, layoutParams);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void checkInternet() {
        if(!isCheckInternet){
            InternetConnection internetConnection = new InternetConnection();
            if(internetConnection.checkConnection(MainActivity.this)){
                Toast.makeText(this, "Connecting by Wifi", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Connecting by Mobile Network", Toast.LENGTH_SHORT).show();
            }
            isCheckInternet = true;
        }

    }

    private void refresh() {
        srlMovie.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                movieArrayList.clear();
                callDataAPI();
                page = 1;
                isEmptyData = false;
                movieAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlMovie.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    // Init
    private void init() {
        rlloadMore = findViewById(R.id.rlloadMore);
        srlMovie = findViewById(R.id.srlMovie);
        rccMovie = findViewById(R.id.rccMovie);
        rccMovie.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        // divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_divider);
        assert drawable != null;
        dividerItemDecoration.setDrawable(drawable);
        rccMovie.addItemDecoration(dividerItemDecoration);

        movieArrayList = new ArrayList<>();
        movieAdapter = new MovieAdapter(MainActivity.this, movieArrayList);
        rccMovie.setAdapter(movieAdapter);

        database = new Database(MainActivity.this, "movie.sqlite", null, 1);

        // create database
        database.QueryData("CREATE TABLE IF NOT EXISTS Movie(id_table INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER)");

        database.QueryData("CREATE TABLE IF NOT EXISTS Views(id_table INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, views INTEGER)");

    }

    //select SQLite
    @SuppressLint("SetTextI18n")
    public void selectLike(int id, TextView like) {
        Cursor cursor = MainActivity.database.getData("SELECT * FROM Movie");
        while (cursor.moveToNext()){
            //int id_table = cursor.getInt(0);
            int id_movie = cursor.getInt(1);
            if(id == id_movie){
                like.setText("Đã thích");
                break;
            }else {
                like.setText("Thích");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void selectViews(int id, TextView views) {
        Cursor cursor = MainActivity.database.getData("SELECT * FROM Views");
        while (cursor.moveToNext()){
            //int id_table = cursor.getInt(0);
            int id_sqlite = cursor.getInt(1);
            int views_sqlite = cursor.getInt(2);
            if(id == id_sqlite){
                views.setText(views_sqlite + "");
                break;
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
    public void getDataFromItemFilm(int id, String like, int views) {
        // neu chua dang nhap thi chuyen sang page login
        if(!isLogin){
            intentLogin();
            MainActivity.this.finish();
        }else{
            Intent intent = new Intent(MainActivity.this, FilmDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putString("like",like);
            bundle.putInt("views", views);
            intent.putExtras(bundle);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    //chuyen sang trang Dang nhap
    public void intentLogin(){
        Toast.makeText(this, "Vui lòng đăng nhập !!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    //call API
    private void callDataAPI() {
        Call<MovieResponse> call = service.getMovieData();
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NotNull Call<MovieResponse> call, Response<MovieResponse> response) {
                assert response.body() != null;
                movieArrayList.addAll(response.body().getData());
                movieAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(@NotNull Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //call API Scroll
    private void callDataAPIScroll() {
        Call<MovieResponse> call = service.getMovieDataScroll(page);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NotNull Call<MovieResponse> call, Response<MovieResponse> response) {
                assert response.body() != null;
                if(movieArrayList.size() == response.body().getPaging().getTotalItem()){
                    Toast.makeText(MainActivity.this, "Đã hết dữ liệu", Toast.LENGTH_SHORT).show();
                    isEmptyData = true;
                }else{
                    isEmptyData = false;
                    movieArrayList.addAll(response.body().getData());
                    movieAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, ""+ movieArrayList.size(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NotNull Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Internet not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load more
    private void loadMore(){
        rccMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert linearLayoutManager != null;
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                if((visibleItemCount + pastVisiblesItems) == totalItemCount && !isLoading){
                    if(!isEmptyData){
                        rlloadMore.setVisibility(View.VISIBLE);
                        page += 1;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callDataAPIScroll();
                                movieAdapter.notifyDataSetChanged();
                                rlloadMore.setVisibility(View.GONE);
                                isLoading = false;
                            }
                        }, 2000);

                        isLoading = true;
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isLogin){
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.option, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnLogout:
                isLogin = false;
                intentLogin();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
