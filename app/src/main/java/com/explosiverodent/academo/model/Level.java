package com.explosiverodent.academo.model;

public class Level {

    private int position;
    private String title;
    private String difficulty;
    private int rawResourceId;

    public Level(int position,
                 String title,
                 String difficulty,
                 int rawResourceId) {

        this.position = position;
        this.title = title;
        this.difficulty = difficulty;
        this.rawResourceId = rawResourceId;
    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getRawResourceId() {
        return rawResourceId;
    }
}