package com.lxndr.yourmovies.utilities;

import com.lxndr.yourmovies.model.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 01/02/2017.
 */

public final class CommentsJSONUtils {

    public static List<Comment> getCommentsFromJson(String commentsJsonStr)
            throws JSONException {

        final String COMMENTS_ARRAY = "results";

        final String OWM_MESSAGE_CODE = "cod";

        final String KEY_ID = "id";
        final String KEY_AUTHOR = "author";
        final String KEY_CONTENT = "content";



        List<Comment> comments = new ArrayList<Comment>();


        JSONObject commentsJson = new JSONObject(commentsJsonStr);

        /* Is there an error? */
        if (commentsJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = commentsJson.getInt(OWM_MESSAGE_CODE);

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

        JSONArray commentsArray = commentsJson.getJSONArray(COMMENTS_ARRAY);

        for (int i = 0; i < commentsArray.length(); i++) {
            /* These are the values that will be collected */
            String id;
            String author = null;
            String content = null;

            /* Get the JSON object representing the day */
            JSONObject movieJson = commentsArray.getJSONObject(i);

            if (movieJson.has(KEY_ID)) {
               id = movieJson.getString(KEY_ID);
            } else {
                continue;
            }

            if (movieJson.has(KEY_AUTHOR)) {
                author = movieJson.getString(KEY_AUTHOR);
            }
            if (movieJson.has(KEY_CONTENT)) {
                content = movieJson.getString(KEY_CONTENT);
            }

            Comment c = new Comment(id, author, content);
            comments.add(c);
        }

        return comments;
    }

}
