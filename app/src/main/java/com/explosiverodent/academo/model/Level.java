package com.explosiverodent.academo.model;

public class Level {

    private int position;
    private String title;
    private String difficulty;
    private int rawResourceId;
    private int bestScore;
    private String maxRank;
    private String lastAttemptDate;

    private int attemptsCount;
    private int questionsCount;

    public Level(int position, String title, String difficulty, int rawResourceId) {
        this.position = position;
        this.title = title;
        this.difficulty = difficulty;
        this.rawResourceId = rawResourceId;
        this.bestScore = 0;
        this.maxRank = "";
        this.lastAttemptDate = "";
        this.attemptsCount = 0;
        this.questionsCount = 0;
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

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(String maxRank) {
        this.maxRank = maxRank;
    }

    public String getLastAttemptDate() {
        return lastAttemptDate;
    }

    public void setLastAttemptDate(String lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }
}