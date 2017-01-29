package com.example.ah_abdelhak.movieappfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;

public class Helper {

    //setOrder
    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_movie_key),
                context.getString(R.string.pref_sort_movie_default));
    }


     //Build movie poster url
    public static String buildMPosterURI(String relativePath) {
        final String IMAGE_SIZE = "w185";
        final String file = relativePath.substring(1);

        Uri builtUri = Uri.parse("http://image.tmdb.org/t/p").buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(file)
                .build();

        return builtUri.toString();
    }


    //check connecting to internet
    public static boolean hasNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager ConnevtManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = ConnevtManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //Transform Bitmap to byte-Array
    public static byte[] getBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}