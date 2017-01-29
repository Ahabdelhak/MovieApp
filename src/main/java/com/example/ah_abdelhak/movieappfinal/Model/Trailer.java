package com.example.ah_abdelhak.movieappfinal.Model;

public class Trailer {

    private String id;
    private String key;
    private String name;
    private String title;

    public Trailer(String id, String key, String name, String title) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}