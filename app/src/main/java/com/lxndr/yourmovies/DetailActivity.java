package com.lxndr.yourmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxndr.yourmovies.data.MovieContract;
import com.lxndr.yourmovies.model.Comment;
import com.lxndr.yourmovies.model.Movie;
import com.lxndr.yourmovies.model.Trailer;
import com.lxndr.yourmovies.utilities.CommentsJSONUtils;
import com.lxndr.yourmovies.utilities.NetworkUtils;
import com.lxndr.yourmovies.utilities.TrailersJSONUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final int COMMENTS_LOADER = 23;
    public static final int TRAILER_LOADER = 29;
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/w342";
    public static final String MOVIE_ID_KEY = "movie_id_key";
    public static final String MOVIE_KEY = "movie";
    public static final String TRAILERS_KEY = "trailers";
    public static final String MOVIE_COMMENTS_KEY = "movie_comments";

    private TextView mCommentsTextView;

    private ImageView mAddToFavouriteImageView;

    private boolean isFavourite;

    private Snackbar snackbar;

    private Movie mMovie;

    private List<Trailer> trailers = new ArrayList<Trailer>();

    CoordinatorLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mCommentsTextView = (TextView) findViewById(R.id.tv_comments);

        mAddToFavouriteImageView = (ImageView) findViewById(R.id.iv_add_to_favourites);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_KEY)) {
            mMovie = savedInstanceState.getParcelable(MOVIE_KEY);
        } else if (getIntent().hasExtra(MOVIE_KEY)){
            mMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(TRAILERS_KEY)) {
            trailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
            setUpTrailersContainer(trailers);
        } else {
            loadTrailer(String.valueOf(mMovie.getRemoteId()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mMovie.getTitle());

        getSupportActionBar().setTitle(mMovie.getTitle());

        cl = (CoordinatorLayout) findViewById(R.id.details_container);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_COMMENTS_KEY)) {
            String comments = savedInstanceState.getString(MOVIE_COMMENTS_KEY);
            if (!TextUtils.isEmpty(comments)) {
                mCommentsTextView.setText(Html.fromHtml(comments), TextView.BufferType.EDITABLE);
            } else {
                findViewById(R.id.line3).setVisibility(View.GONE);
            }

        } else {
            loadComments(String.valueOf(mMovie.getRemoteId()));
        }

        ImageView iv = (ImageView) findViewById(R.id.iv_movie_poster);
        ViewCompat.setElevation(iv, 10);
        Picasso.with(this).load(IMAGE_URL + mMovie.getImagePath()).into(iv);

        TextView tv = (TextView) findViewById(R.id.tv_movie_title);
        tv.setText(mMovie.getTitle());

        tv = (TextView) findViewById(R.id.tv_movie_overview);
        tv.setText(mMovie.getOverview());

        tv = (TextView) findViewById(R.id.tv_release_date);
        tv.setText(mMovie.getReleaseDate());

        tv = (TextView) findViewById(R.id.tv_rate);
        tv.setText(String.valueOf(mMovie.getRating()));

        mAddToFavouriteImageView.setImageResource(this.isFavourite ? R.drawable.ic_action_add_to_favourites
                : R.drawable.ic_action_remove_from_favourites);

        isFavourite = isFavourite(mMovie.getRemoteId());

        changeFavouriteIcon(isFavourite);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_KEY, mMovie);
        if(!TextUtils.isEmpty(mCommentsTextView.getText().toString())) {
            outState.putString(MOVIE_COMMENTS_KEY, Html.toHtml(mCommentsTextView.getEditableText()).toString());
        }
        outState.putParcelableArrayList(TRAILERS_KEY, new ArrayList<Trailer>(trailers));
        super.onSaveInstanceState(outState);
    }

    private boolean isFavourite(int remoteId) {

        String strRemoteId = Integer.toString(remoteId);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(strRemoteId).build();

        Cursor c = getContentResolver().query(uri, null, null, null, null);

        if (c.moveToFirst()) {
            return 0 < c.getInt(0);
        }
        return false;
    }

    private void loadTrailer(String movieId) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID_KEY, movieId);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(TRAILER_LOADER);

        if (trailerLoader == null) {
            loaderManager.initLoader(TRAILER_LOADER, queryBundle, new TrailerLoader(this)).forceLoad();
        } else {
            loaderManager.restartLoader(TRAILER_LOADER, queryBundle, new TrailerLoader(this));
        }
    }

    private void loadComments(String movieId) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID_KEY, movieId);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> commentsLoader = loaderManager.getLoader(COMMENTS_LOADER);

        if (commentsLoader == null) {
            loaderManager.initLoader(COMMENTS_LOADER, queryBundle, new CommentsLoader(this)).forceLoad();
        } else {
            loaderManager.restartLoader(COMMENTS_LOADER, queryBundle, new CommentsLoader(this));
        }
    }

    public void addRemoveFromFavourites(View view) {
        if (!isFavourite) {
            addMovieToFavourites(mMovie);
        } else {
            removeMovieFromFavourites(mMovie);
        }
    }

    private void addMovieToFavourites(Movie movie) {
        persistMovieToFavourites(movie);
        showAddToFavouritesSnackBar();
        changeFavouriteIcon(!isFavourite);
    }

    private void persistMovieToFavourites(Movie mMovie) {
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_REMOTE_ID, mMovie.getRemoteId());
        cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, mMovie.getImagePath());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());

        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
    }

    private void removeMovieFromFavourites(Movie movie) {
        if (deleteMovieFromFavourites(movie) != 0) {
            showRemoveFromFavouritesSnackBar();
            changeFavouriteIcon(!isFavourite);
        } else {

        }
    }

    private int deleteMovieFromFavourites(Movie mMovie) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(mMovie.getRemoteId())).build();
        int rows = getContentResolver().delete(uri, null, null);
        return rows;
    }

    private void changeFavouriteIcon(boolean isFavourite) {
        this.isFavourite = isFavourite;
        mAddToFavouriteImageView.setImageResource(this.isFavourite
                ? R.drawable.ic_action_add_to_favourites
                : R.drawable.ic_action_remove_from_favourites);
    }

    private void showAddToFavouritesSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        snackbar = Snackbar
                .make(cl, R.string.info_added_to_favourites, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.snackbar_action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMovieFromFavourites(mMovie);
                    }
                })
                .setActionTextColor(Color.YELLOW);;
        snackbar.show();
    }

    private void showRemoveFromFavouritesSnackBar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        snackbar = Snackbar
                .make(cl, R.string.info_removed_from_favourites, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.snackbar_action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addMovieToFavourites(mMovie);
                    }
                })
                .setActionTextColor(Color.YELLOW);;
        snackbar.show();
    }

    private class TrailerLoader implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        Context c;

        public TrailerLoader(Context c) {
            this.c = c;
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(c) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                }

                @Override
                public List<Trailer> loadInBackground() {
                    String movieId = args.getString(MOVIE_ID_KEY);
                    if (movieId == null || TextUtils.isEmpty(movieId)) {
                        return null;
                    }

                    URL trailersRequestUrl = NetworkUtils.buildVideosURL(movieId);

                    try {
                        String jsonTrailersResponse = NetworkUtils
                                .getResponseFromHttpUrl(trailersRequestUrl);

                        List<Trailer> trailersData = TrailersJSONUtils
                                .getTrailersFromJson(jsonTrailersResponse);

                        return trailersData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, final List<Trailer> data) {
            trailers = data;

            if (data != null) {
                if (data.size() < 1) {
                    return;
                }
                setUpTrailersContainer(data);
            }
        }
    }

    public void setUpTrailersContainer(List<Trailer> trailers) {

        GridLayout buttonsContainer = (GridLayout) findViewById(R.id.trailer_buttons_container);
        buttonsContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < trailers.size(); i++) {
            final Trailer t = trailers.get(i);
            Button b = new Button(DetailActivity.this);
            b.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

            b.setText(getString(R.string.btn_label_trailer));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=" + t.getKey()));
                    intent.putExtra("VIDEO_ID", t.getKey());
                    startActivity(intent);
                }
            });

            buttonsContainer.addView(b);
        }
    }

    private class CommentsLoader implements LoaderManager.LoaderCallbacks<String> {

        Context c;

        public CommentsLoader(Context c) {
            this.c = c;
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }

        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(c) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                }

                @Override
                public String loadInBackground() {
                    String movieId = args.getString(MOVIE_ID_KEY);
                    if (movieId == null || TextUtils.isEmpty(movieId)) {
                        return null;
                    }

                    URL commentsRequestUrl = NetworkUtils.buildCommentsURL(movieId);

                    try {
                        String jsonCommentsResponse = NetworkUtils
                                .getResponseFromHttpUrl(commentsRequestUrl);

                        List<Comment> commentsData = CommentsJSONUtils
                                .getCommentsFromJson(jsonCommentsResponse);

                        String commentsText;

                        if (commentsData.size() > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (Comment c : commentsData) {
                                sb.append("<b>" + c.getAuthor() + "</b><br>");
                                sb.append("<pre>" + c.getContent().replaceAll("\n", "<br>") + "<pre><br><br>");
                            }
                            commentsText = sb.toString();
                        } else {
                            commentsText = "";
                        }
                        return commentsText;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, final String data) {
            if (data != null && !TextUtils.isEmpty(data)) {
                mCommentsTextView.setText(Html.fromHtml(data), TextView.BufferType.EDITABLE);
            } else {
                mCommentsTextView.setVisibility(View.GONE);
                findViewById(R.id.line3).setVisibility(View.GONE);
            }
        }
    }
}