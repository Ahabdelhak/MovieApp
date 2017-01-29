package com.example.ah_abdelhak.movieappfinal.data;

/**
 * Created by ah_abdelhak on 11/21/2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.ah_abdelhak.movieappfinal";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";


    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POSTER_IMAGE = "poster_image";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";

        public static final String[] MOVIE_COLUMS = {
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_TITLE,
                COLUMN_RELEASE_DATE,
                COLUMN_POSTER_PATH,
                COLUMN_POSTER_IMAGE,
                COLUMN_VOTE_AVERAGE,
                COLUMN_OVERVIEW
        };

        public static final int COL_ID = 0;
        public static final int COL_MOVIE_ID = 1;
        public static final int COL_TITLE = 2;
        public static final int COL_RELEASE_DATE = 3;
        public static final int COL_POSTER_PATH = 4;
        public static final int COL_POSTER_IMAGE = 5;
        public static final int COL_VOTE_AVERAGE = 6;
        public static final int COL_OVERVIEW = 7;



        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;



        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}