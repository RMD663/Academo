package com.explosiverodent.academo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.model.User;
import com.explosiverodent.academo.utils.RankingCalculator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextView txtPointsEarned, txtXpEarned, txtCorrectStat, txtWrongStat, txtCurrentLevel, txtLevelUpAlert;
    private Button btnBackHome;

    private UserDatabase userDatabase;
    private int userId;

    private User updatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        userDatabase = new UserDatabase(this);

        initViews();
        processResults();
        setupNavigation();
    }

    private void initViews() {
        txtPointsEarned = findViewById(R.id.txt_points_earned);
        txtXpEarned = findViewById(R.id.txt_xp_earned);
        txtCorrectStat = findViewById(R.id.txt_correct_stat);
        txtWrongStat = findViewById(R.id.txt_wrong_stat);
        txtCurrentLevel = findViewById(R.id.txt_current_level);
        txtLevelUpAlert = findViewById(R.id.txt_level_up_alert);
        btnBackHome = findViewById(R.id.btn_back_home);
    }

    private void setupNavigation(){
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (updatedUser != null) {
                intent.putExtra("USER_ID", updatedUser.getId());
                intent.putExtra("USER_NAME", updatedUser.getUserName());
                intent.putExtra("USER_LEVEL", updatedUser.getLevel());
                intent.putExtra("USER_XP", updatedUser.getXp());
                intent.putExtra("USER_PICTURE", updatedUser.getProfilePicture());
            } else {
                intent.putExtra("USER_ID", userId);
            }

            startActivity(intent);
            finish();
        });
    }

    private void processResults() {
        userId = getIntent().getIntExtra("USER_ID", -1);
        int corrects = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int wrongs = getIntent().getIntExtra("WRONG_COUNT", 0);

        int levelPosition = getIntent().getIntExtra("LEVEL_POSITION", -1);
        String difficulty = getIntent().getStringExtra("LEVEL_DIFFICULTY");
        long totalDurationMillis = getIntent().getLongExtra("TOTAL_DURATION", 0);

        int levelScore = RankingCalculator.calculateScore(corrects, wrongs, totalDurationMillis);
        String finalRank = RankingCalculator.calculateRank(corrects, wrongs, totalDurationMillis, difficulty);

        int pointsGained = corrects * 10;
        float xpGained = corrects * 12.0f;

        if ("SS".equals(finalRank)) xpGained += 25.0f;
        else if ("S".equals(finalRank)) xpGained += 15.0f;
        else if ("A".equals(finalRank)) xpGained += 5.0f;

        txtPointsEarned.setText("+" + pointsGained + " PTS");
        txtXpEarned.setText("+" + (int) xpGained + " XP");
        txtCorrectStat.setText("Correct: " + corrects + " [" + finalRank + "]");
        txtWrongStat.setText("Wrong: " + wrongs + " (Score: " + levelScore + ")");

        if (userId == -1) {
            Toast.makeText(this, "Error: User session lost.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (levelPosition != -1) {
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            userDatabase.saveLevelRecord(userId, levelPosition, levelScore, finalRank, currentDate);
        }

        User user = userDatabase.getUserById(userId);
        if (user != null) {
            int currentPoints = user.getPoints() + pointsGained;
            float currentXp = user.getXp() + xpGained;
            int currentLevel = user.getLevel();

            boolean leveledUp = false;
            float xpRequiredForNextLevel = currentLevel * 100.0f;

            while (currentXp >= xpRequiredForNextLevel) {
                currentXp -= xpRequiredForNextLevel;
                currentLevel++;
                leveledUp = true;
                xpRequiredForNextLevel = currentLevel * 100.0f;
            }

            userDatabase.updateUserProgress(userId, currentPoints, currentXp, currentLevel);

            updatedUser = new User(userId, user.getUserName(), currentPoints, currentXp, currentLevel);
            updatedUser.setProfilePicture(user.getProfilePicture());

            txtCurrentLevel.setText("Level: " + currentLevel + " (" + (int)currentXp + " / " + (int)xpRequiredForNextLevel + " XP)");

            if (leveledUp) {
                txtLevelUpAlert.setVisibility(View.VISIBLE);
                txtLevelUpAlert.setText("LEVEL UP!");
            } else {
                txtLevelUpAlert.setVisibility(View.VISIBLE);
                txtLevelUpAlert.setText("RANK OBTAINED: " + finalRank);
            }
        }
    }
}