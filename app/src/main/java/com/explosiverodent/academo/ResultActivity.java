package com.explosiverodent.academo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.model.User;

public class ResultActivity extends AppCompatActivity {

    private TextView txtPointsEarned, txtXpEarned, txtCorrectStat, txtWrongStat, txtCurrentLevel, txtLevelUpAlert;
    private Button btnBackHome;

    private UserDatabase userDatabase;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        userDatabase = new UserDatabase(this);

        initViews();
        processResults();
    }

    private void initViews() {
        txtPointsEarned = findViewById(R.id.txt_points_earned);
        txtXpEarned = findViewById(R.id.txt_xp_earned);
        txtCorrectStat = findViewById(R.id.txt_correct_stat);
        txtWrongStat = findViewById(R.id.txt_wrong_stat);
        txtCurrentLevel = findViewById(R.id.txt_current_level);
        txtLevelUpAlert = findViewById(R.id.txt_level_up_alert);
        btnBackHome = findViewById(R.id.btn_back_home);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void processResults() {
        userId = getIntent().getIntExtra("USER_ID", -1);
        int corrects = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int wrongs = getIntent().getIntExtra("WRONG_COUNT", 0);

        int pointsGained = corrects * 10;
        float xpGained = corrects * 15.0f;

        txtPointsEarned.setText("+" + pointsGained + " PTS");
        txtXpEarned.setText("+" + xpGained + " XP");
        txtCorrectStat.setText("Correct: " + corrects);
        txtWrongStat.setText("Wrong: " + wrongs);

        if (userId == -1) {
            Toast.makeText(this, "Error: User session lost.", Toast.LENGTH_SHORT).show();
            return;
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
                xpRequiredForNextLevel = currentLevel * 100.0f;
                leveledUp = true;
            }

            userDatabase.updateUserProgress(userId, currentPoints, currentXp, currentLevel);

            txtCurrentLevel.setText("Level: " + currentLevel + " (" + (int)currentXp + " / " + (int)xpRequiredForNextLevel + " XP)");

            if (leveledUp) {
                txtLevelUpAlert.setVisibility(View.VISIBLE);
            }
        }
    }
}