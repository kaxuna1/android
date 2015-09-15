package com.adjara.adjaranet.model;

import com.orm.SugarRecord;

/**
 * Created by vakhtanggelashvili on 8/10/15.
 */
public class MovieSerieLastMomentModel extends SugarRecord<MovieSerieLastMomentModel> {
    public int time;
    public int season;
    public int serie;
    public String movieId;
    public MovieSerieLastMomentModel(String movieId){
        this.movieId=movieId;
    }
    public MovieSerieLastMomentModel(){

    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSerie() {
        return serie;
    }

    public void setSerie(int serie) {
        this.serie = serie;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
