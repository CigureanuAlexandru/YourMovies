package com.lxndr.yourmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by alex on 22/03/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.lxndr.yourmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_REMOTE_ID = "remote_id";

        public static final String COLUMN_IMAGE_PATH = "image_path";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_OVERVIEW = "view";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";
    }
}
