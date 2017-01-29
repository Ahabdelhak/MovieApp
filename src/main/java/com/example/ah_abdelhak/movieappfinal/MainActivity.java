package com.example.ah_abdelhak.movieappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ah_abdelhak.movieappfinal.fragment.MovieDetailFragment;
import com.example.ah_abdelhak.movieappfinal.fragment.MainActivityFragment;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private static final String Detail_FragmentTAG = "DF_TAG";

    private String mSort;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSort = Helper.getPreferredSortOrder(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new MovieDetailFragment(), Detail_FragmentTAG)
                        .commit();
            }
            else {
                mTwoPane = false;
                getSupportActionBar().setElevation(0f);
            }
        }

        MainActivityFragment mf = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        mf.setAutoClickFirst(mTwoPane);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sort = Helper.getPreferredSortOrder(this);

        if (sort != null && !sort.equals(mSort)) {
            MainActivityFragment mf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if (mf != null)
                mf.ChangeSortOrder();

            mSort = sort;
        }


    }

    @Override
    public void onItemSelected(Bundle bundle) {
        if (mTwoPane) {
            MovieDetailFragment detail_fragment = new MovieDetailFragment();
            detail_fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detail_fragment, Detail_FragmentTAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, MovieDetail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}