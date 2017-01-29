package com.example.ah_abdelhak.movieappfinal.Model;

import com.example.ah_abdelhak.movieappfinal.data.MovieContract;

public class Movieitem {

    private int id;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private String title;
    private double vote_average;

    public Movieitem(int id, String original_title, String overview, String release_date, String poster_path,
                     String title, double vote_average) {
        this.id = id;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.title = title;
        this.vote_average = vote_average;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }


    public static final String COLUMN_NAME_MOVIE_ID = MovieContract.MovieEntry.COLUMN_MOVIE_ID;
    public static final String COLUMN_NAME_TITLE = MovieContract.MovieEntry.COLUMN_TITLE;
    public static final String COLUMN_NAME_RELEASE_DATE = MovieContract.MovieEntry.COLUMN_RELEASE_DATE;
    public static final String COLUMN_NAME_POSTER_PATH = MovieContract.MovieEntry.COLUMN_POSTER_PATH;
    public static final String COLUMN_NAME_VOTE_AVERAGE = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE;
    public static final String COLUMN_NAME_OVERVIEW = MovieContract.MovieEntry.COLUMN_OVERVIEW;

    public static final int COLUMN_MOVIE_ID = MovieContract.MovieEntry.COL_MOVIE_ID;
    public static final int COLUMN_TITLE = MovieContract.MovieEntry.COL_TITLE;
    public static final int COLUMN_RELEASE_DATE = MovieContract.MovieEntry.COL_RELEASE_DATE;
    public static final int COLUMN_POSTER_PATH = MovieContract.MovieEntry.COL_POSTER_PATH;
    public static final int COLUMN_VOTE_AVERAGE = MovieContract.MovieEntry.COL_VOTE_AVERAGE;
    public static final int COLUMN_OVERVIEW = MovieContract.MovieEntry.COL_OVERVIEW;


}