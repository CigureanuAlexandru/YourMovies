package com.lxndr.yourmovies.utilities;

import com.lxndr.yourmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 01/02/2017.
 */

public final class TrailersJSONUtils {

    public static List<Trailer> getTrailersFromJson(String trailersJsonStr)
            throws JSONException {

        final String TRAILERS_ARRAY = "results";

        final String OWM_MESSAGE_CODE = "cod";

        final String KEY_SITE = "site";
        final String KEY_SITE_KEY = "key";



        List<Trailer> trailers = new ArrayList<Trailer>();


        JSONObject moviesJson = new JSONObject(trailersJsonStr);

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

        JSONArray trailersArray = moviesJson.getJSONArray(TRAILERS_ARRAY);

        for (int i = 0; i < trailersArray.length(); i++) {
            /* These are the values that will be collected */

            String key = null;
            String site = null;

            /* Get the JSON object representing the day */
            JSONObject trailerJson = trailersArray.getJSONObject(i);



            if (trailerJson.has(KEY_SITE)) {
                site = trailerJson.getString(KEY_SITE);
            }
            if (trailerJson.has(KEY_SITE_KEY)) {
                key = trailerJson.getString(KEY_SITE_KEY);
            }

            Trailer t = new Trailer(site, key);
            trailers.add(t);
        }

        return trailers;
    }

}
