package com.lxndr.yourmovies.fragment;

import com.lxndr.yourmovies.model.Movie;

import java.util.List;

/**
 * Created by alex on 23/03/2017.
 */

public interface IMovies {

    void onMoviesRefresh();

    List<Movie> onBackgroundLoad(Integer... params);

    boolean hasScrollListener();
}
