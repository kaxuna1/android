package com.kgelashvili.moviesapp.model;

/**
 * Created by vakhtanggelashvili on 8/12/15.
 */
public class ColectionModel {
    private String name;
    private String id;
    private String cnt;

    public ColectionModel(String name, String id,String cnt) {
        this.name = name;
        this.id = id;
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
