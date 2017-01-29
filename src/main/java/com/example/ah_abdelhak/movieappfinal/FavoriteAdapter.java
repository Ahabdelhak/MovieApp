package com.example.ah_abdelhak.movieappfinal;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ah_abdelhak.movieappfinal.data.MovieContract;

/**
 * put grid with movie-posters "byte array" from database.
 */

public class FavoriteAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_view);
        }
    }

    public FavoriteAdapter(Context context, Cursor cur, int flags) {
        super(context, cur, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.grid_view;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageView imgview = viewHolder.imageView;

        byte[] b = cursor.getBlob(MovieContract.MovieEntry.COL_POSTER_IMAGE);
        imgview.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
    }
}