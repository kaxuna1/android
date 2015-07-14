package com.kgelashvili.moviesapp.Classes;

import android.util.Log;

import com.kgelashvili.moviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by KGelashvili on 7/10/2015.
 */
public class MovieServices {
    public ArrayList<Movie> getMainMovies(String offset,String language,String startYear,String endYear,String keyWord){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        //offset=0,language=false,startYear=1900,endYear=2015
        String url = "http://adjaranet.com/Search/SearchResults?ajax=1&display=15&startYear="+startYear+"&endYear="+endYear +
                "&offset="+offset+"&isnew=0&keyword="+keyWord+"&needtags=0&orderBy=date&order%5Border%5D=data&order%5Bdata%5D=published&language="+language +
                "&country=false&game=0&videos=0&xvideos=0&xphotos=0&trailers=0&episode=0&tvshow=0&flashgames=0";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            Log.d("kaxa", rawMoviesData);
            JSONObject jsonObject=new JSONObject(rawMoviesData);
            JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                Log.d("kaxa", movieJ.getString("poster"));
                Log.d("lang",movieJ.getString("lang"));
                Movie movie=new Movie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        movieJ.getString("imdb_id"),
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        movieJ.getString("duration"),
                        movieJ.getString("lang"));
                Movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return Movies;
    }
}