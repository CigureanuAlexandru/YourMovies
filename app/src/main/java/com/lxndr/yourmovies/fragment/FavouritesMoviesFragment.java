package com.lxndr.yourmovies.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.lxndr.yourmovies.data.MovieContract;
import com.lxndr.yourmovies.data.MovieDbHelper;
import com.lxndr.yourmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 22/03/2017.
 */

public class FavouritesMoviesFragment extends AbstractMoviesFragment {

    private static final String TAG = FavouritesMoviesFragment.class.getSimpleName();

    private SQLiteDatabase mDb;

    final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MovieDbHelper dbHelper = new MovieDbHelper(getContext());

        mDb = dbHelper.getReadableDatabase();

    }

    @Override
    public void onMoviesRefresh() {
        page = 1;
        loadMovieData(page);
    }

    @Override
    public List<Movie> onBackgroundLoad(Integer... params) {


        Cursor cursor = getAllFavouriteMovies();

        List<Movie> movies = new ArrayList<Movie>();

        try {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                int remoteId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REMOTE_ID));
                String imagePath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH));
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

                Movie m = new Movie(id, remoteId, imagePath, title, overview, rating, releaseDate, 0.0, (byte) 1);
                movies.add(m);
            }
        } finally {
            cursor.close();
        }

        return movies;
    }

    @Override
    public boolean hasScrollListener() {
        return false;
    }

    private Cursor getAllFavouriteMovies() {
        try {
            return getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null,
                    MovieContract.MovieEntry._ID);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load data");
            e.printStackTrace();
            return null;
        }
    }


}
