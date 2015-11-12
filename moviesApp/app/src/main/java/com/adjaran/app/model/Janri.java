package com.adjaran.app.model;

/**
 * Created by vakhtanggelashvili on 8/9/15.
 */
public class Janri {
    private String name;
    private String value;


    public Janri(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
