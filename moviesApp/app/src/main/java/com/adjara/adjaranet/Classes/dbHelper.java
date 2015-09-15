package com.adjara.adjaranet.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adjara.adjaranet.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KGelashvili on 7/13/2015.
 */
public class dbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;

    public static final String DATABASE_NAME="MoviesApp",
            TABLE_MOVIES="movies",KEY_ID="id",title_en="title_en",
            link="link",poster="poster",imdb="imdb",imdb_id="imdb_id",
            release_date="release_date",description="description",duration="duration",lang="lang";
    public static final String TABLE_MOVIETIME="movieTime",movieId="movieId",time="time";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MOVIES + "(" + KEY_ID + " TEXT," +
                title_en + " TEXT," + link + " TEXT," + poster + " TEXT," + imdb + " TEXT," + imdb_id + " TEXT,"
                + release_date + " TEXT," + description + " TEXT," + duration + " TEXT," + lang + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MOVIETIME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                movieId + " TEXT," + time + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIETIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);

        onCreate(db);
    }
    public Movie getMovie(String id){
        Movie movie=null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MOVIES+" where id='"+id+"'", null);

        if (cursor.moveToFirst()) {
            do {
                movie=new Movie (cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return movie;
    }

    public void createMovie(Movie movie){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_ID,movie.getMovieId());
        contentValues.put(title_en,movie.getTitle_en());
        contentValues.put(link,movie.getLink());
        contentValues.put(poster,movie.getPoster());
        contentValues.put(imdb,movie.getImdb());
        contentValues.put(imdb_id,movie.getImdb_id());
        contentValues.put(release_date,movie.getRelease_date());
        contentValues.put(description,movie.getDescription());
        contentValues.put(duration,movie.getDuration());
        contentValues.put(lang,movie.getLang());
        db.insert(TABLE_MOVIES,null,contentValues);
        db.close();
    }
    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MOVIES, KEY_ID + "=?", new String[]{String.valueOf(movie.getMovieId())});
        db.close();
    }

    public int getMoviesCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MOVIES, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

    /*public int updateMovie(Movie contact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.get_name());
        values.put(KEY_PHONE, contact.get_number());
        values.put(KEY_EMAIL, contact.get_email());
        values.put(KEY_IMG, contact.get_img().toString());

        int rowsAffected = db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[] { String.valueOf(contact.get_id()) });
        db.close();

        return rowsAffected;
    }*/

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<Movie>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MOVIES, null);

        if (cursor.moveToFirst()) {
            do {
                movies.add(new Movie (cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return movies;
    }


}
