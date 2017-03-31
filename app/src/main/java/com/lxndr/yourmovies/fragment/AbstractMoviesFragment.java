package com.lxndr.yourmovies.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lxndr.yourmovies.DetailActivity;
import com.lxndr.yourmovies.MyImageAdapter;
import com.lxndr.yourmovies.R;
import com.lxndr.yourmovies.model.Movie;

import java.util.List;

/**
 * Created by alex on 23/03/2017.
 */

public abstract class AbstractMoviesFragment extends Fragment implements
        MyImageAdapter.MovieAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener, IMovies  {

    private static final String TAG = AbstractMoviesFragment.class.getSimpleName();
    protected static final String MOVIES_KEY = "movies";
    protected static final String MOVIE_KEY = "movie";
    protected static final String SCROLL_ITEM_KEY = "scroll_item";
    protected static final String PAGE_KEY = "page";

    private SwipeRefreshLayout mMoviesSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    protected MyImageAdapter mMoviesImageAdapter;
    private Snackbar snackbar;
    protected int page = 1;
    private int offset = 0;
    protected int scrollItemIndex = 1;

    private FetchMoviesTask task;

    protected final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutManager.setSpanCount(getContext().getResources().getInteger(R.integer.grid_items));

        mMoviesImageAdapter = new MyImageAdapter(getContext(), this);

        // restore sorted Criteria
        if ( savedInstanceState != null ) {

            // restore page
            if (savedInstanceState.containsKey(PAGE_KEY) ) {
                page = savedInstanceState.getInt(PAGE_KEY);
            }

            // restore movies
            if (savedInstanceState.containsKey(MOVIES_KEY) ) {
                List<Movie> ms = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mMoviesImageAdapter.setData(ms);
            }

            if (savedInstanceState.containsKey(SCROLL_ITEM_KEY)) {
                scrollItemIndex = savedInstanceState.getInt(SCROLL_ITEM_KEY);
            }
        } else {
            page = 1;
        }
    }

    RecyclerView.OnScrollListener l = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount && totalItemCount != 0) {
                //End of list
                if ((task != null && task.getStatus() == AsyncTask.Status.FINISHED)){
                    // End has been reached
                    page++;
                    loadMovieData(page);
                    // Log.d(TAG, firstVisibleItem + "-" + visibleItemCount + "-" + totalItemCount + "-" + "%d, %d, %d, End of list has been triggered");
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_KEY, mMoviesImageAdapter.getData());
        outState.putInt(SCROLL_ITEM_KEY, layoutManager.findFirstVisibleItemPosition());
        outState.putInt(PAGE_KEY, page);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.movies_grid_view, container, false);

        mMoviesSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.srl_main);

        mMoviesSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview_movies);

        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.setLayoutManager(layoutManager);

        if (hasScrollListener()) {
            mRecyclerView.addOnScrollListener(l);
        }

        mRecyclerView.setAdapter(mMoviesImageAdapter);

        if (mMoviesImageAdapter != null && mMoviesImageAdapter.getItemCount() < 1) {
            loadMovieData(page);
        } else {
            mRecyclerView.smoothScrollToPosition(scrollItemIndex);
            task = new FetchMoviesTask();
            task.cancel(true);
        }

        return root;
    }

    /*
        * OnRefreshListener
         */
    @Override
    public void onRefresh() {
        loadMovieData(page);
    }

    @Override
    public void onClick(Movie movie) {

        Intent i = new Intent(getContext(), DetailActivity.class);
        i.putExtra(MOVIE_KEY, movie);
        startActivity(i);
    }

    protected void loadMovieData(int page) {
        if (task != null) {
            task.cancel(true);
        }
        task =  new FetchMoviesTask();
        task.execute(page);
    }

    private void showErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        snackbar = Snackbar
                .make(mMoviesSwipeRefreshLayout, R.string.err_no_connection, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.snackbar_action_retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMovieData(page);
                    }
                }).setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMoviesSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Movie> doInBackground(Integer... params) {

            return onBackgroundLoad(params);
        }

        @Override
        protected void onPostExecute(List<Movie> moviesData) {
            mMoviesSwipeRefreshLayout.setRefreshing(false);
            if (moviesData != null) {
                Log.d(TAG, moviesData.toString());
                if (page == 1) {
                    mMoviesImageAdapter.setData(moviesData);
                } else {
                    mMoviesImageAdapter.appendData(moviesData);
                }
            } else {
                showErrorMessage();
            }
        }
    }
}