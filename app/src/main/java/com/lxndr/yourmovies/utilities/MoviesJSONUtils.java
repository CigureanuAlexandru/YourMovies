package com.lxndr.yourmovies.utilities;

import com.lxndr.yourmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 01/02/2017.
 */

public final class MoviesJSONUtils {

    public static List<Movie> getMoviesFromJson(String moviesJsonStr)
            throws JSONException {

        final String MOVIES_ARRAY = "results";

        final String OWM_MESSAGE_CODE = "cod";

        final String KEY_ID = "id";
        final String KEY_IMAGE_PATH = "poster_path";
        final String KEY_TITLE = "title";
        final String KEY_OVERVIEW = "overview";
        final String KEY_RATING = "vote_average";
        final String KEY_RELEASE_DATE = "release_date";
        final String KEY_POPULARITY = "popularity";



        List<Movie> movies = new ArrayList<Movie>();


        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (moviesJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = moviesJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_ARRAY);

        for (int i = 0; i < moviesArray.length(); i++) {
            /* These are the values that will be collected */
            int remoteId;
            String imagePath = null;
            String title = null;
            String overview = null;
            double rating = 0d;
            String releaseDate = null;
            double popularity = 0d;

            /* Get the JSON object representing the day */
            JSONObject movieJson = moviesArray.getJSONObject(i);

            if (movieJson.has(KEY_ID)) {
                remoteId = movieJson.getInt(KEY_ID);
            } else {
                continue;
            }

            if (movieJson.has(KEY_IMAGE_PATH)) {
                imagePath = movieJson.getString(KEY_IMAGE_PATH);
            }
            if (movieJson.has(KEY_TITLE)) {
                title = movieJson.getString(KEY_TITLE);
            }
            if (movieJson.has(KEY_OVERVIEW)) {
                overview = movieJson.getString(KEY_OVERVIEW);
            }
            if (movieJson.has(KEY_RATING)) {
                rating = movieJson.getDouble(KEY_RATING);
            }
            if (movieJson.has(KEY_RELEASE_DATE)) {
                releaseDate = movieJson.getString(KEY_RELEASE_DATE);
            }
            if (movieJson.has(KEY_POPULARITY)) {
                popularity = movieJson.getDouble(KEY_POPULARITY);
            }

            Movie m = new Movie(remoteId, imagePath, title, overview, rating, releaseDate, popularity, (byte) 0);
            movies.add(m);
        }

        return movies;
    }

}
