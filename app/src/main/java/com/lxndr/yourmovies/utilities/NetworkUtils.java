/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lxndr.yourmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.lxndr.yourmovies.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 * http://api.themoviedb.org/3/movie/popular?api_key=KEY&page=4
 * 3 - API version
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();



    private static final String BASE_URL =
            "http://api.themoviedb.org/3/movie";

    final static String KEY_PARAM = "api_key";
    final static String PAGE_PARAM = "page";
    public static final String TOP_RATED_PATH = "/top_rated";
    public static final String POPULAR_PATH = "/popular";
    public static final String VIDEOS_PATH = "/videos";
    public static final String REVIEWS_PATH = "/reviews";

    public static URL buildTopRatedMoviesURL(final int page) {
        return buildUrl(BASE_URL + TOP_RATED_PATH, page);
    }

    public static URL buildPopularMoviesURL(final int page) {
        return buildUrl(BASE_URL + POPULAR_PATH, page);
    }

    public static URL buildVideosURL(final String movieId) {
        return buildUrl(BASE_URL + "/" + movieId + VIDEOS_PATH);
    }

    public static URL buildCommentsURL(final String movieId) {
        return buildUrl(BASE_URL + "/" + movieId + REVIEWS_PATH);
    }

    public static URL buildUrl(String baseURL, final int page) {
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(KEY_PARAM, Config.MOVIES_DB_API_KEY)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrl(String baseURL) {
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(KEY_PARAM, Config.MOVIES_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}