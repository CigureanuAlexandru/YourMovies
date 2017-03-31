package com.lxndr.yourmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxndr.yourmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 30/03/2017.
 */

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MyImageAdapter.class.getSimpleName();

    private static final String IMAGE_URL_BASE = "http://image.tmdb.org/t/p/w185";

    private Context mContext;

    List<Movie> movies = new ArrayList<>();


    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MyImageAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }


    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.grid_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieAdapterViewHolder forecastAdapterViewHolder, int position) {
        Picasso.with(mContext).load(IMAGE_URL_BASE +
                movies.get(position).getImagePath()).into(forecastAdapterViewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.size();
    }

    public void setData(List<Movie> ms) {
        movies = ms;
        notifyDataSetChanged();
    }

    public void appendData(List<Movie> ms) {
        movies.addAll(ms);
        notifyDataSetChanged();
        //Log.d(TAG, movies.toString());
    }

    public ArrayList<Movie> getData() {
        return new ArrayList<Movie>(movies);
    }
}