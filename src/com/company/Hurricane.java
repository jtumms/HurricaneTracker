package com.company;

/**
 * Created by john.tumminelli on 10/4/16.
 */
public class Hurricane {

    enum Category {
        ONE, TWO, THREE, FOUR, FIVE
    }
    String name;
    String location;
    Category category;
    String image;

    public Hurricane(String name, String location, Category category, String image, User user) {
        this.name = name;
        this.location = location;
        this.category = category;
        this.image = image;
    }
}
