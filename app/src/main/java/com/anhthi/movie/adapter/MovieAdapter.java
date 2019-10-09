package com.anhthi.movie.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anhthi.movie.R;
import com.anhthi.movie.model.Movie;
import com.anhthi.movie.activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    MainActivity activity;
    ArrayList<Movie> movieArrayList;

    public MovieAdapter(MainActivity activity, ArrayList<Movie> movieList) {
        this.activity = activity;
        this.movieArrayList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_film, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Movie movie = movieArrayList.get(position);
        final int id = movie.getId();
        String[] title = movie.getTitle().split("/");
        Picasso.get().load(movie.getImage()).into(holder.imgImage);
        holder.txtTitle.setText(title.length == 2 ? title[0] : movie.getTitle());
        holder.txtName.setText(title.length == 2 ? title[1].trim() : movie.getTitle());
        holder.txtViews.setText("Lượt xem: " + movie.getViews());
        holder.txtDescription.setText(movie.getDescription() + ".");

        //select
        //movie id call == id sqlite => setText like
        activity.selectLike(id, holder.txtLike);

        if(holder.txtLike.getText().equals("Thích")){
            holder.imgLike.setImageResource(R.drawable.ic_like);
            holder.txtLike.setTextColor(Color.parseColor("#ffffff"));
        }else{
            holder.imgLike.setImageResource(R.drawable.ic_like_orange);
            holder.txtLike.setText("Đã thích");
            holder.txtLike.setTextColor(Color.parseColor("#fd6003"));
        }

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.txtLike.getText().equals("Thích")){
                    holder.imgLike.setImageResource(R.drawable.ic_like_orange);
                    holder.txtLike.setText("Đã thích");
                    holder.txtLike.setTextColor(Color.parseColor("#fd6003"));
                    activity.insertLike(id);
                }else{
                    holder.imgLike.setImageResource(R.drawable.ic_like);
                    holder.txtLike.setText("Thích");
                    holder.txtLike.setTextColor(Color.parseColor("#ffffff"));
                    activity.deleteLike(id);
                }
            }
        });

        holder.btnWatchMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getDataFromItemFilm(id, holder.txtLike.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgImage, imgLike;
        TextView txtTitle, txtViews, txtDescription, txtName, txtLike;
        Button btnWatchMovie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgImage = itemView.findViewById(R.id.imgImage);
            imgLike = itemView.findViewById(R.id.imgLike);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtName = itemView.findViewById(R.id.txtName);
            txtViews = itemView.findViewById(R.id.txtViews);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtLike = itemView.findViewById(R.id.txtLike);
            btnWatchMovie = itemView.findViewById(R.id.btnWatchMovie);
        }
    }
}
