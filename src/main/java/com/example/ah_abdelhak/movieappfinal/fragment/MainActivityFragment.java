package com.example.ah_abdelhak.movieappfinal.fragment;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.example.ah_abdelhak.movieappfinal.Model.Movieitem;
import com.example.ah_abdelhak.movieappfinal.MovieAdapter;
import com.example.ah_abdelhak.movieappfinal.R;
import com.example.ah_abdelhak.movieappfinal.Helper;
import com.example.ah_abdelhak.movieappfinal.FavoriteAdapter;
import com.example.ah_abdelhak.movieappfinal.data.MovieContract;

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

public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private GridView mGridView;
    private CursorAdapter mAdapter;

    // display details of first item automatically in tablet mode
    private boolean autoClickFirst = false;

    // is this the first start of this fragment?
    private boolean mFirstStart = true;

    public interface Callback {
        void onItemSelected(Bundle extras);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFirstStart) {
            refreshMovies();
            mFirstStart = false;
        }
    }

    private void refreshMovies() {
        String sort_by = Helper.getPreferredSortOrder(getActivity());

        if (sort_by.equals("favorite")) {
            AsyncTask<Void, Void, Cursor> task = new FetchFavorite();
            task.execute();
        }
        else {
            if (!Helper.hasNetworkConnection(getActivity()))
                return;

            AsyncTask<Void, Void, List<Movieitem>> MovieTask = new FetchMovie();
            MovieTask.execute();
        }
    }

    public void ChangeSortOrder() {
        if (mAdapter != null)
            mAdapter.swapCursor(null);
        refreshMovies();
    }

    public void setAutoClickFirst(boolean auto) {
        autoClickFirst = auto;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_item_layout, container, false);

        mGridView = (GridView)rootView.findViewById(R.id.grid);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cur = (Cursor) parent.getItemAtPosition(position);
                if (cur != null) {
                    Bundle extras = new Bundle();
                    extras.putInt(Movieitem.COLUMN_NAME_MOVIE_ID, cur.getInt(Movieitem.COLUMN_MOVIE_ID));
                    extras.putString(Movieitem.COLUMN_NAME_TITLE, cur.getString(Movieitem.COLUMN_TITLE));
                    extras.putString(Movieitem.COLUMN_NAME_RELEASE_DATE, cur.getString(Movieitem.COLUMN_RELEASE_DATE));
                    extras.putString(Movieitem.COLUMN_NAME_POSTER_PATH, cur.getString(Movieitem.COLUMN_POSTER_PATH));
                    extras.putDouble(Movieitem.COLUMN_NAME_VOTE_AVERAGE, cur.getDouble(Movieitem.COLUMN_VOTE_AVERAGE));
                    extras.putString(Movieitem.COLUMN_NAME_OVERVIEW, cur.getString(Movieitem.COLUMN_OVERVIEW));

                    ((Callback) getActivity()).onItemSelected(extras);
                }
            }
        });

        return rootView;
    }

    public class FetchMovie extends AsyncTask<Void, Void, List<Movieitem>> {

        @Override
        protected List<Movieitem> doInBackground(Void... params) {
            return loadMovieList();
        }

        private List<Movieitem> loadMovieList() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            List<Movieitem> movieList = null;
            String movieJsonStr = null;

            String sort_by = Helper.getPreferredSortOrder(getActivity());

            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                final String VOTE_COUNT_PARAM = "vote_count.gte";
                final String MINIMUM_VOTE_COUNT = "100";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(APIKEY_PARAM, "cae57cf1e4826b64e94a38d17bc8c044")
                        .appendQueryParameter(VOTE_COUNT_PARAM, MINIMUM_VOTE_COUNT)
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
                movieList = getMovieDataFromJson(movieJsonStr);
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

            return movieList;
        }

        private List<Movieitem> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String MDB_LIST = "results";
            final String MDB_ID = "id";
            final String MDB_ORIGINAL_TITLE = "original_title";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_TITLE = "title";
            final String MDB_VOTE_AVERAGE = "vote_average";

            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

                List<Movieitem> movieList = new ArrayList<>();

                for(int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);

                    int id = movie.getInt(MDB_ID);
                    String original_title = movie.getString(MDB_ORIGINAL_TITLE);
                    String overview = movie.getString(MDB_OVERVIEW);
                    String release_date = movie.getString(MDB_RELEASE_DATE);
                    String poster_path = movie.getString(MDB_POSTER_PATH);
                    String title = movie.getString(MDB_TITLE);
                    double vote_average = movie.getDouble(MDB_VOTE_AVERAGE);
                    Movieitem MovieItem = new Movieitem(id,original_title, overview, release_date, poster_path ,
                            title, vote_average);

                    movieList.add(MovieItem);
                }

                return movieList;

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

                return null;
            }
        }

        @Override
        public void onPostExecute(List<Movieitem> movieList) {
            super.onPostExecute(movieList);

            if (movieList == null)
                return;

            // convert list of Movies to Cursor
            String columns[] = MovieContract.MovieEntry.MOVIE_COLUMS;

            MatrixCursor matrixCursor= new MatrixCursor(columns);
            getActivity().startManagingCursor(matrixCursor);

            int i = 0;
            for (Movieitem m: movieList) {
                i++;
                matrixCursor.addRow(new Object[]{i, m.getId(), m.getTitle(), m.getRelease_date(), m.getPoster_path(), "", m.getVote_average(), m.getOverview()});
            }

            // set the adapter to display the movies
            mAdapter = new MovieAdapter(getActivity(), null, 0);
            mGridView.setAdapter(mAdapter);
            mAdapter.swapCursor(matrixCursor);
            mAdapter.notifyDataSetChanged();

            if (mGridView.getCount() > 0 && autoClickFirst) {
                final int first = 0;
                mGridView.performItemClick(mAdapter.getView(first, null, null), first, mAdapter.getItemId(first));
            }
        }
    }


    public class FetchFavorite extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            return getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.MOVIE_COLUMS,
                    null,
                    null,
                    null
            );
        }


        @Override
        public void onPostExecute(Cursor movies) {
            super.onPostExecute(movies);

            mAdapter = new FavoriteAdapter(getActivity(), null, 0);
            mGridView.setAdapter(mAdapter);
            mAdapter.swapCursor(movies);

            if (mGridView.getCount() > 0 && autoClickFirst) {
                final int first = 0;
                mGridView.performItemClick(mAdapter.getView(first, null, null), first, mAdapter.getItemId(first));
            }
        }
    }

}