package com.kgelashvili.moviesapp.Classes;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.kgelashvili.moviesapp.model.Actor;
import com.kgelashvili.moviesapp.model.ColectionModel;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.MovieAndSerie;
import com.kgelashvili.moviesapp.model.Serie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by KGelashvili on 7/10/2015.
 */
public class MovieServices {
    public ArrayList<Movie> getMainMovies(String offset,String language,String startYear,String endYear,String keyWord,String janrebi){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        //offset=0,language=false,startYear=1900,endYear=2015
        String url = "http://adjaranet.com/Search/SearchResults?ajax=1&"+janrebi+"display=30&startYear="+startYear+"&endYear="+endYear +
                "&offset="+offset+"&isnew=0&keyword="+keyWord+"&needtags=0&orderBy=date&order%5Border%5D=data&order%5Bdata%5D=published&language="+language +
                "&country=false&game=0&videos=0&xvideos=0&xphotos=0&trailers=0&episode=0&tvshow=0&flashgames=0";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONObject jsonObject=new JSONObject(rawMoviesData);
            JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);

                Movie movie=new Movie(
                        movieJ.getString("id"),
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

    public ArrayList<Serie> getMainSeries(String offset,String language,String startYear,String endYear,String keyWord,String janrebi){
        ArrayList<Serie> Series=new ArrayList<Serie>();
        //offset=0,language=false,startYear=1900,endYear=2015
        String url = "http://adjaranet.com/Search/SearchResults?ajax=1"+janrebi+"&display=15&startYear="+startYear+"&endYear="+endYear +
                "&offset="+offset+"&isnew=0&keyword="+keyWord+"&needtags=0&orderBy=date&order%5Border%5D=data&order%5Bdata%5D=published&language="+language +
                "&country=false&game=0&videos=0&xvideos=0&xphotos=0&trailers=0&episode=1&tvshow=0&flashgames=0";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONObject jsonObject=new JSONObject(rawMoviesData);
            JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                Serie serie=new Serie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        movieJ.getString("imdb_id"),
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        movieJ.getString("duration"),
                        "");
                Series.add(serie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return Series;
    }

    public ArrayList<Movie> getLatestGeoMovies(){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        String url = "http://adjaranet.com/cache/cached_home_geomovies.php?type=geomovies&order=new&period=day&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
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

    public ArrayList<Movie> getPremierMovies(){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        String url = "http://adjaranet.com/cache/cached_home_premiere.php?type=premiere&order=new&period=week&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);

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

    public ArrayList<Serie> getNewEpisodes(){
        ArrayList<Serie> Movies=new ArrayList<Serie>();
        String url = "http://adjaranet.com/cache/cached_home_episodes.php?type=episodes&order=new&period=week&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                Serie movie=new Serie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        "",
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        "",
                        "");
                Movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return Movies;
    }

    public ArrayList<Serie> getGeoEpisodes(){
        ArrayList<Serie> Movies=new ArrayList<Serie>();
        String url = "http://adjaranet.com/Home/ajax_home?ajax=1&type=geomovies&order=top&period=null&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                Serie movie=new Serie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        "",
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        "",
                        "");
                Movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return Movies;
    }

    public ArrayList<Movie> getNewAddedMovies(){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        String url = "http://adjaranet.com/cache/cached_home_movies.php?type=movies&order=new&period=week&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);

            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);

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

    public ArrayList<Actor> getMovieActors(String movieId){
        ArrayList<Actor> actors=new ArrayList<Actor>();
        String url = "http://adjaranet.com/req/jsondata/req.php?id="+movieId+"&reqId=getLangAndHd";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawData=networkDAO.request(url);

            JSONObject movieData=new JSONObject(rawData);
            JSONObject actorsObject=movieData.getJSONObject("cast");
            Iterator<?> keys = actorsObject.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                actors.add(new Actor(key,actorsObject.getString(key)) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return actors;
    }

    public ArrayList<Actor> getSerieActors(String serieId){
        ArrayList<Actor> actors=new ArrayList<Actor>();
        String url = "http://adjaranet.com/req/jsondata/req.php?id="+serieId+"&reqId=getInfo";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawData=networkDAO.request(url);
            JSONObject movieData=new JSONObject(rawData);
            JSONObject actorsObject=movieData.getJSONObject("cast");
            Iterator<?> keys = actorsObject.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                actors.add(new Actor(key,actorsObject.getString(key)) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return actors;
    }

    public JSONObject getSerieDataJson(String serieId){
        String url = "http://adjaranet.com/req/jsondata/req.php?id="+serieId+"&reqId=getInfo";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawData=networkDAO.request(url);
            JSONObject movieData=new JSONObject(rawData);
            return movieData;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    public String getMovieQuality(String movieId){
        String quality="";

        String url = "http://adjaranet.com/req/jsondata/req.php?id="+movieId+"&reqId=getLangAndHd";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawData=networkDAO.request(url);

            JSONObject movieData=new JSONObject(rawData);
            JSONObject object0=movieData.getJSONObject("0");
            quality=object0.getString("quality");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return quality;
    }

    public String getMovieLangs(String movieId){
        String langs="";

        String url = "http://adjaranet.com/req/jsondata/req.php?id="+movieId+"&reqId=getLangAndHd";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawData=networkDAO.request(url);
            JSONObject movieData=new JSONObject(rawData);
            JSONObject object0=movieData.getJSONObject("0");
            langs=object0.getString("lang");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return langs;
    }

    public ArrayList<Movie> getRelateMovies(String movieId){
        ArrayList<Movie> Movies=new ArrayList<Movie>();
        String url = "http://adjaranet.com/Movie/BuildSliderRelated?ajax=1&movie_id="+movieId+"&isepisode=0&type=related&order=top&period=day&limit=25";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray movies=new JSONArray(rawMoviesData);
            //JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                Movie movie=new Movie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        "",
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        "",
                        "");
                Movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return Movies;
    }

    public ArrayList<MovieAndSerie> getMainGeo(String offset,String janrebi){
        ArrayList<MovieAndSerie> movieAndSeries=new ArrayList<MovieAndSerie>();
        //offset=0,language=false,startYear=1900,endYear=2015
        String url = "http://adjaranet.com/Search/SearchResults?ajax=1&"+janrebi+"display=15" +
                "&startYear=1900&endYear=2015&offset="+offset+"&isnew=0&needtags=0" +
                "&orderBy=date&order%5Border%5D=desc&order%5Bdata%5D=movies" +
                "&order%5Bmeta%5D=desc&language=false&country=false&game=0" +
                "&georgians=1&episode=0&trailers=0&tvshow=0&videos=0" +
                "&xvideos=0&xphotos=0&flashgames=0";
        Log.d("geoMoviesUrl",url);
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONObject jsonObject=new JSONObject(rawMoviesData);
            JSONArray movies=jsonObject.getJSONArray("data");
            for (int i=0;i<movies.length();i++){
                JSONObject movieJ=movies.getJSONObject(i);
                MovieAndSerie movieAndSerie=new MovieAndSerie(movieJ.getString("id"),
                        movieJ.getString("title_en"),
                        movieJ.getString("link"),
                        movieJ.getString("poster"),
                        movieJ.getString("imdb"),
                        movieJ.getString("imdb_id"),
                        movieJ.getString("release_date"),
                        movieJ.getString("description"),
                        movieJ.getString("duration"),
                        "");
                movieAndSeries.add(movieAndSerie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return movieAndSeries;
    }

    public ArrayList<ColectionModel> getColections(){
        ArrayList<ColectionModel> Colections=new ArrayList<ColectionModel>();
        //offset=0,language=false,startYear=1900,endYear=2015
        String url = "http://adjaranet.com/req/jsondata/req.php?reqId=getCollections";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONObject jsonObject=new JSONObject(rawMoviesData);
            Iterator<?> keys = jsonObject.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                JSONObject collection=jsonObject.getJSONObject(key);
                Colections.add(new ColectionModel(collection.getString("name"),key,collection.getString("cnt")) );
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return Colections;

    }
}
