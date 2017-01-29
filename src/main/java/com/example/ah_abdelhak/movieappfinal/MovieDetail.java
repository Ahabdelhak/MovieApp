package com.example.ah_abdelhak.movieappfinal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ah_abdelhak.movieappfinal.fragment.MovieDetailFragment;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            MovieDetailFragment Detail_Fragment = new MovieDetailFragment();
            Detail_Fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, Detail_Fragment)
                    .commit();
        }
    }
}