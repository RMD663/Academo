package com.explosiverodent.academo.utils;

public class RankingCalculator {

    public static int calculateScore(int corrects, int wrongs, long totalDurationMillis) {
        int score = corrects * 100;
        score -= (wrongs * 40);

        float totalSeconds = totalDurationMillis / 1000f;
        score -= (int) (totalSeconds * 1.5f);

        return Math.max(0, score);
    }

    public static String calculateRank(int corrects, int wrongs, long totalDurationMillis, String difficulty) {
        int totalQuestions = corrects + wrongs;
        if (totalQuestions == 0) return "D";

        float difficultyMultiplier;
        if (difficulty == null) difficulty = "Easy";

        switch (difficulty.trim().toLowerCase()) {
            case "hard":
                difficultyMultiplier = 1.5f;
                break;
            case "medium":
                difficultyMultiplier = 1.2f;
                break;
            case "easy":
            default:
                difficultyMultiplier = 1.0f;
                break;
        }

        float maxPossibleScore = totalQuestions * 100f;
        int rawScore = calculateScore(corrects, wrongs, totalDurationMillis);

        float finalScore = rawScore * difficultyMultiplier;
        float adjustedMaxScore = maxPossibleScore * difficultyMultiplier;

        float performanceRatio = finalScore / adjustedMaxScore;

        if (performanceRatio >= 0.95f && wrongs == 0) return "SS";
        if (performanceRatio >= 0.85f) return "S";
        if (performanceRatio >= 0.70f) return "A";
        if (performanceRatio >= 0.55f) return "B";
        if (performanceRatio >= 0.40f) return "C";
        return "D";
    }
}