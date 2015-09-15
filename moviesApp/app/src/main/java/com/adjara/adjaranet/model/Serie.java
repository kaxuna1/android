package com.adjara.adjaranet.model;

/**
 * Created by KGelashvili on 7/15/2015.
 */
public class Serie extends Movie {
    public int lastEp;
    public int lastSes;
    public Serie(String id, String title_en, String link, String poster, String imdb, String imdb_id, String release_date, String description, String duration, String lang) {
        super(id, title_en, link, poster, imdb, imdb_id, release_date, description, duration, lang);
    }
    public Serie(){

    }

    public int getLastEp() {
        return lastEp;
    }

    public void setLastEp(int lastEp) {
        this.lastEp = lastEp;
    }

    public int getLastSes() {
        return lastSes;
    }

    public void setLastSes(int lastSes) {
        this.lastSes = lastSes;
    }
}
