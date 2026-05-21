package com.explosiverodent.academo.model;

import java.util.List;

public class Question {
    private String type;
    private String text;
    private List<String> options;
    private String correctAnswer;

    public Question(String type, String text, List<String> options, String correctAnswer) {
        this.type = type;
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
}