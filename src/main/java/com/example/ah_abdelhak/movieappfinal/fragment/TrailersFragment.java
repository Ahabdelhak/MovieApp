package com.example.ah_abdelhak.movieappfinal.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import com.example.ah_abdelhak.movieappfinal.Model.Trailer;
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

public class TrailersFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private int mMovie_id;

    private boolean mTrailersLoaded = false;
    private LinearLayout mLinearLayout;
    private LinearLayout mTrailerLinearLayout;


    public TrailersFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);

        Bundle args = getArguments();

        if (args != null) {
            mMovie_id = args.getInt(Movieitem.COLUMN_NAME_MOVIE_ID);
        }

        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.fragment_trailers);
        mTrailerLinearLayout = (LinearLayout) rootView.findViewById(R.id.fragment_trailers_list);

        return rootView;
    }

    private void YoutubeVideo(String youtube_id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtube_id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + youtube_id));
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean movie_id_is_valid = (mMovie_id != 0);
        boolean internet_connection_Valid = Helper.hasNetworkConnection(getActivity());

        if (!mTrailersLoaded && movie_id_is_valid && internet_connection_Valid) {
            String strMovie_id = Integer.toString(mMovie_id);

            AsyncTask<String, Void, List<Trailer>> TrailerTask = new FetchTrailer();
            TrailerTask.execute(strMovie_id);
        }
    }



    public class FetchTrailer extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... params) {
            return loadTrailerList(params[0]);
        }

        private List<Trailer> loadTrailerList(String idMovie) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<Trailer> trailerList = null;

            String movieJsonStr = null;


            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APIKEY_PARAM = "api_key";

                final String TRAILER_PATH = "videos";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(idMovie)
                        .appendPath(TRAILER_PATH)
                        .appendQueryParameter(APIKEY_PARAM, "Add your API key here")
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
                movieJsonStr = buffer.toString();
                trailerList = getTrailerDataFromJson(movieJsonStr);
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
            return trailerList;
        }

        private List<Trailer> getTrailerDataFromJson(String trailerJsonStr) throws JSONException {

            final String MDB_LIST = "results";
            final String MDB_ID = "id";
            final String MDB_KEY = "key";
            final String MDB_NAME = "name";
            final String MDB_SITE = "site";

            try {
                JSONObject trailerJson = new JSONObject(trailerJsonStr);
                JSONArray trailerArray = trailerJson.getJSONArray(MDB_LIST);

                List<Trailer> trailerList = new ArrayList<>();

                for(int i = 0; i < trailerArray.length(); i++) {
                    JSONObject trailer = trailerArray.getJSONObject(i);

                    String id = trailer.getString(MDB_ID);
                    String key = trailer.getString(MDB_KEY);
                    String name = trailer.getString(MDB_NAME);
                    String site = trailer.getString(MDB_SITE);

                    Trailer tra = new Trailer(id, key, name, site);
                    trailerList.add(tra);
                }
                return trailerList;

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerList) {
            super.onPostExecute(trailerList);

            if (trailerList == null)
                return;

            // display the trailers in linear layout
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (Trailer t: trailerList) {
                View trailerView = inflater.inflate(R.layout.trailer_item, mTrailerLinearLayout, false);
                TextView Trailer_UTube = (TextView)trailerView.findViewById(R.id.trailer_textview);

                trailerView.setTag(t.getKey());
                trailerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YoutubeVideo(view.getTag().toString());
                    }
                });
                Trailer_UTube.setText(t.getName());

                mTrailerLinearLayout.addView(trailerView);
            }

            if (!trailerList.isEmpty()) {
                mLinearLayout.setVisibility(View.VISIBLE);

            }
            mTrailersLoaded = true;
        }
    }
}
