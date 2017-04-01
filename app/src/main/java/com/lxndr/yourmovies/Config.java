package com.lxndr.yourmovies;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by alex on 01/02/2017.
 */


public class Config {

    // Declare the sort constants
    public static final int SORT_BY_POPULARITY = 0;
    public static final int SORT_BY_TOP_RATED = 1;
    public static final int SORT_BY_FAVOURITES = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_BY_POPULARITY, SORT_BY_TOP_RATED, SORT_BY_FAVOURITES})
    public @interface SortCriteria {}
}
