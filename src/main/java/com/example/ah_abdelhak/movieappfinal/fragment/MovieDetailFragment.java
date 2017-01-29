package com.example.ah_abdelhak.movieappfinal.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ah_abdelhak.movieappfinal.Model.Movieitem;
import com.example.ah_abdelhak.movieappfinal.R;
import com.example.ah_abdelhak.movieappfinal.Helper;
import com.example.ah_abdelhak.movieappfinal.data.MovieContract;
import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private int mMovie_id;
    private ImageView imgview_poster;

    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private double mVoteAverage;
    private String mOverview;


    public MovieDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle args = getArguments();

        TextView tv_title = (TextView)rootView.findViewById(R.id.title);
        imgview_poster = (ImageView)rootView.findViewById(R.id.posterimg);
        TextView tv_release_date = (TextView)rootView.findViewById(R.id.release_date);
        TextView tv_vote_average = (TextView)rootView.findViewById(R.id.vote_average);
        TextView tv_overview = (TextView)rootView.findViewById(R.id.overview);
        Button saveButton = (Button)rootView.findViewById(R.id.button_save_favorite);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAsFavorite();
            }
        });

        if (args != null) {
            mMovie_id = args.getInt(Movieitem.COLUMN_NAME_MOVIE_ID);

            mTitle = args.getString(Movieitem.COLUMN_NAME_TITLE);
            mReleaseDate = args.getString(Movieitem.COLUMN_NAME_RELEASE_DATE);
            mVoteAverage = args.getDouble(Movieitem.COLUMN_NAME_VOTE_AVERAGE);
            mOverview = args.getString(Movieitem.COLUMN_NAME_OVERVIEW);

            mPosterPath = args.getString(Movieitem.COLUMN_NAME_POSTER_PATH);
            String url = Helper.buildMPosterURI(mPosterPath);
            Picasso.with(getActivity()).load(url).into(imgview_poster);

            tv_title.setText(mTitle);
            tv_release_date.setText(mReleaseDate);
            tv_vote_average.setText(Double.toString(mVoteAverage));
            tv_overview.setText(mOverview);
        }


        Bundle bundle = new Bundle();
        bundle.putInt(Movieitem.COLUMN_NAME_MOVIE_ID, mMovie_id);

        TrailersFragment trailers_fragment = new TrailersFragment();
        trailers_fragment.setArguments(bundle);

        ReviewsFragment review_fragment = new ReviewsFragment();
        review_fragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_trailer_container, trailers_fragment)
                .replace(R.id.fragment_detail_review_container, review_fragment)
                .commit();

        return rootView;
    }

    public long SetAsFavorite() {
        long movieId;

        Cursor favoriteCur = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(mMovie_id)},
                null
        );

        if (favoriteCur.moveToFirst()) {
            Toast.makeText(getContext(), "Movie already in Favorite", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "movie already in db");

            int movieIdIndex= favoriteCur.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            movieId = favoriteCur.getLong(movieIdIndex);
        }
        else {
            Toast.makeText(getContext(), "Movie Marked As Favorite Successfully", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Movie Marked As Favorite");

            ContentValues contentValueM = new ContentValues();

            contentValueM.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie_id);
            contentValueM.put(MovieContract.MovieEntry.COLUMN_TITLE, mTitle);
            contentValueM.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            contentValueM.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mOverview);
            contentValueM.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
            contentValueM.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mPosterPath);


            BitmapDrawable image = (BitmapDrawable) imgview_poster.getDrawable();
            Bitmap bitmap = image.getBitmap();
            byte[] array = Helper.getBitmapToByteArray(bitmap);

            contentValueM.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE, array);

            Uri insertUri = getActivity().getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI, contentValueM);

            movieId = ContentUris.parseId(insertUri);
        }

        favoriteCur.close();
        return movieId;
    }

}