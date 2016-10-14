package com.company;

/**
 * Created by john.tumminelli on 10/4/16.
 */
public class Hurricane {
    int id;
    String name;
    String location;
    int category;
    String image;
    String author;



    public Hurricane(String name, String location, int category, String image, String author , int id) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.category = category;
        this.image = image;
        this.author = author;

    }

    public Hurricane() {
    }
}