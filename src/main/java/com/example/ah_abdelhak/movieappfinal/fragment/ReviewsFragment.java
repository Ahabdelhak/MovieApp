package com.example.ah_abdelhak.movieappfinal.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ah_abdelhak.movieappfinal.Helper;
import com.example.ah_abdelhak.movieappfinal.Model.Movieitem;
import com.example.ah_abdelhak.movieappfinal.Model.Review;
import com.example.ah_abdelhak.movieappfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ReviewsFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private int mMovie_id;
    private boolean mReviewsLoaded;
    private LinearLayout mLinearLayout;
    private LinearLayout mReviewLinearLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        mReviewsLoaded = false;

        Bundle args = getArguments();
        if (args != null) {
            mMovie_id = args.getInt(Movieitem.COLUMN_NAME_MOVIE_ID);
        }
        mLinearLayout = (LinearLayout)rootView.findViewById(R.id.fragment_reviews);
        mReviewLinearLayout = (LinearLayout)rootView.findViewById(R.id.fragment_reviews_list);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        boolean movie_id_is_valid = (mMovie_id != 0);
        boolean internet_connection_Valid = Helper.hasNetworkConnection(getActivity());

        if (!mReviewsLoaded && movie_id_is_valid && internet_connection_Valid) {
            String strMovie_id = Integer.toString(mMovie_id);

            AsyncTask<String, Void, List<Review>> ReviwTask = new FetchReviw();
            ReviwTask.execute(strMovie_id);
        }
    }

    public class FetchReviw extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... params) {
            return loadReviewList(params[0]);
        }

        private List<Review> loadReviewList(String idMovie) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<Review> reviewList = null;

            String reviewJsonStr = null;

            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APIKEY_PARAM = "api_key";

                final String REVIEW_PATH = "reviews";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(idMovie)
                        .appendPath(REVIEW_PATH)
                        .appendQueryParameter(APIKEY_PARAM, "cae57cf1e4826b64e94a38d17bc8c044")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
                reviewList = getReviewDataFromJson(reviewJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return reviewList;
        }

        private List<Review> getReviewDataFromJson(String reviewJsonStr) throws JSONException {

            final String MDB_LIST = "results";
            final String MDB_ID = "id";
            final String MDB_AUTHOR = "author";
            final String MDB_CONTENT = "content";
            final String MDB_URL = "url";

            try {
                JSONObject trailerJson = new JSONObject(reviewJsonStr);
                JSONArray trailerArray = trailerJson.getJSONArray(MDB_LIST);

                List<Review> reviewList = new ArrayList<>();

                for(int i = 0; i < trailerArray.length(); i++) {
                    JSONObject trailer = trailerArray.getJSONObject(i);

                    String id = trailer.getString(MDB_ID);
                    String author = trailer.getString(MDB_AUTHOR);
                    String content = trailer.getString(MDB_CONTENT);
                    String url = trailer.getString(MDB_URL);

                    Review rev = new Review(id, author, content, url);

                    reviewList.add(rev);
                }

                return reviewList;

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
            super.onPostExecute(reviewList);

            if (reviewList == null)
                return;

            // add reviews to linear layout
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (Review r: reviewList) {
                View reviewView = inflater.inflate(R.layout.review_item, mReviewLinearLayout, false);
                TextView Review_Author = (TextView)reviewView.findViewById(R.id.review_author);
                TextView ReviewContent = (TextView)reviewView.findViewById(R.id.review_content);

                Review_Author.setText(r.getAuthor());
                ReviewContent.setText(r.getContent());

                mReviewLinearLayout.addView(reviewView);
            }

            if (!reviewList.isEmpty()) {
                mLinearLayout.setVisibility(View.VISIBLE);
            }

            mReviewsLoaded = true;
        }
    }
}