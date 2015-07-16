package com.kgelashvili.moviesapp.model;

/**
 * Created by KGelashvili on 7/16/2015.
 */
public class Episode {
    private String link;
    private String lang;
    private String name;

    public Episode(String link, String lang, String name) {
        this.link = link;
        this.lang = lang;
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
