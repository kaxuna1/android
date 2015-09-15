package com.adjara.adjaranet.model;

import java.util.ArrayList;

/**
 * Created by KGelashvili on 7/16/2015.
 */
public class Season {
    private String name;
    private ArrayList<Episode> episodes;


    public Season(String name) {
        this.name = name;
        this.episodes=new ArrayList<Episode>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public void addEpisode(Episode episode){
        episodes.add(episode);
    }
    public Episode getEpisode(int i){
       return episodes.get(i);
    }
}
