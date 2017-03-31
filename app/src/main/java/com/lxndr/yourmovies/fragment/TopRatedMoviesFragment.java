package com.lxndr.yourmovies.fragment;

import com.lxndr.yourmovies.model.Movie;
import com.lxndr.yourmovies.utilities.MoviesJSONUtils;
import com.lxndr.yourmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by alex on 23/03/2017.
 */

public class TopRatedMoviesFragment extends AbstractMoviesFragment {


    @Override
    public void onMoviesRefresh() {
        page = 1;
        loadMovieData(page);
    }

    @Override
    public List<Movie> onBackgroundLoad(Integer... params) {
        if (params.length != 1) {
            return null;
        }

        int page = params[0];

        URL moviesRequestUrl = NetworkUtils.buildTopRatedMoviesURL(page);

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            List<Movie> moviesData = MoviesJSONUtils
                    .getMoviesFromJson(jsonMoviesResponse);

            return moviesData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hasScrollListener() {
        return true;
    }
}
