package com.adjaran.app.model;

import com.orm.SugarRecord;

/**
 * Created by vakhtanggelashvili on 8/10/15.
 */
public class HistoryModel extends SugarRecord {
    int type;
    public String movieId;
    public String title_en;
    public String link;
    public String poster;
    public String imdb;
    public String imdb_id;
    public String release_date;
    public String description;
    public String duration;
    public String lang;

    public HistoryModel(String id, String title_en, String link, String poster, String imdb,
                        String imdb_id, String release_date, String description, String duration, String lang) {
        this.movieId = id;
        this.title_en = title_en;
        this.link = link;
        this.poster = poster;
        this.imdb = imdb;
        this.imdb_id = imdb_id;
        this.release_date = release_date;
        this.description = description;
        this.duration = duration;
        this.lang=lang;
    }
    public HistoryModel(){

    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setId(String id) {
        this.movieId = id;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
