package com.explosiverodent.academo.model;

public class Level {
    private int position;
    private String title;
    private String difficulty;
    private String jsonFileName;

    public Level(int position, String title, String difficulty, String jsonFileName) {
        this.position = position;
        this.title = title;
        this.difficulty = difficulty;
    }

    public int getPosition() { return position; }
    public String getTitle() { return title; }
    public String getDifficulty() { return difficulty; }

    public String getJsonFileName() {
        return jsonFileName;
    }
}