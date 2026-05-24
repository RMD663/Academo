package com.explosiverodent.academo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.explosiverodent.academo.model.Question;
import com.explosiverodent.academo.jsonreader.JsonReader;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private int userId = -1;
    private int correctAnswersCount = 0;

    private int levelPosition = -1;
    private String levelDifficulty = "Easy";
    private long levelStartTime = 0;

    private TextView txtLevelTitle;
    private TextView txtQuestion;

    private LinearLayout containerMultipleChoice;
    private LinearLayout containerTrueFalse;
    private LinearLayout containerTextInput;

    private Button btnOpt1;
    private Button btnOpt2;
    private Button btnOpt3;
    private Button btnOpt4;

    private Button btnTrue;
    private Button btnFalse;

    private EditText inputAnswer;
    private Button btnSubmitText;

    private TextView txtTimer;
    private TextView txtScore;
    private TextView txtRank;

    private CountDownTimer countDownTimer;
    private long questionTimeLimitMs = 12000;
    private long secondsRemaining = 12;
    private int currentTotalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        initViews();
        setupClickListeners();
        loadLevel();

        if (questionList != null && !questionList.isEmpty()) {
            displayQuestion(currentQuestionIndex);
        } else {
            txtQuestion.setText("No questions available.");
            txtTimer.setText("--");
            txtRank.setText("-");
        }
    }

    private void initViews() {
        txtLevelTitle = findViewById(R.id.game_level_title);
        txtQuestion = findViewById(R.id.question_text);

        containerMultipleChoice = findViewById(R.id.container_multiple_choice);
        containerTrueFalse = findViewById(R.id.container_true_false);
        containerTextInput = findViewById(R.id.container_text_input);

        btnOpt1 = findViewById(R.id.btn_option_1);
        btnOpt2 = findViewById(R.id.btn_option_2);
        btnOpt3 = findViewById(R.id.btn_option_3);
        btnOpt4 = findViewById(R.id.btn_option_4);

        btnTrue = findViewById(R.id.btn_true);
        btnFalse = findViewById(R.id.btn_false);

        inputAnswer = findViewById(R.id.input_answer);
        btnSubmitText = findViewById(R.id.btn_submit_text);

        txtTimer = findViewById(R.id.game_timer_text);
        txtScore = findViewById(R.id.game_score_text);
        txtRank = findViewById(R.id.game_current_rank_text);
    }

    private void loadLevel() {
        userId = getIntent().getIntExtra("USER_ID", -1);
        levelPosition = getIntent().getIntExtra("LEVEL_POSITION", -1);
        levelDifficulty = getIntent().getStringExtra("LEVEL_DIFFICULTY");
        if (levelDifficulty == null) levelDifficulty = "Easy";

        String levelTitle = getIntent().getStringExtra("LEVEL_TITLE");
        if (levelTitle != null) {
            txtLevelTitle.setText(levelTitle.toUpperCase());
        }

        int rawResourceId = getIntent().getIntExtra("LEVEL_RESOURCE", -1);
        if (rawResourceId == -1) {
            finish();
            return;
        }

        switch (levelDifficulty.trim().toLowerCase()) {
            case "hard":
                questionTimeLimitMs = 6000;
                break;
            case "medium":
                questionTimeLimitMs = 9000;
                break;
            case "easy":
            default:
                questionTimeLimitMs = 12000;
                break;
        }
        secondsRemaining = questionTimeLimitMs / 1000;

        questionList = JsonReader.loadQuestions(this, rawResourceId);
        levelStartTime = System.currentTimeMillis();

        txtScore.setText("0000");
        txtRank.setText("S");
    }

    private void setupClickListeners() {
        btnOpt1.setOnClickListener(v -> checkAnswer(btnOpt1.getText().toString()));
        btnOpt2.setOnClickListener(v -> checkAnswer(btnOpt2.getText().toString()));
        btnOpt3.setOnClickListener(v -> checkAnswer(btnOpt3.getText().toString()));
        btnOpt4.setOnClickListener(v -> checkAnswer(btnOpt4.getText().toString()));

        btnTrue.setOnClickListener(v -> checkAnswer("true"));
        btnFalse.setOnClickListener(v -> checkAnswer("false"));

        btnSubmitText.setOnClickListener(v -> {
            String playerText = inputAnswer.getText().toString().trim();

            if (!playerText.isEmpty()) {
                hideKeyboard();
                checkAnswer(playerText);
            } else {
                Toast.makeText(this, "Type an answer first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(questionTimeLimitMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished / 1000;
                txtTimer.setText(secondsRemaining + "s");
            }

            @Override
            public void onFinish() {
                txtTimer.setText("0s");
                checkAnswer("");
            }
        }.start();
    }

    private void checkAnswer(String playerAnswer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (currentQuestionIndex >= questionList.size()) return;

        Question currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        if (playerAnswer.equalsIgnoreCase(correctAnswer)) {
            correctAnswersCount++;
            int basePoints = 100;
            int timeBonus = (int) (secondsRemaining * 10);
            currentTotalScore += (basePoints + timeBonus);

            txtScore.setText(String.format("%04d", currentTotalScore));
        }

        updateRealTimeRank();

        currentQuestionIndex++;
        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int index) {
        if (index >= questionList.size()) {
            txtQuestion.setText("LEVEL COMPLETE!");

            containerMultipleChoice.setVisibility(View.GONE);
            containerTrueFalse.setVisibility(View.GONE);
            containerTextInput.setVisibility(View.GONE);

            int totalQuestions = questionList.size();
            int wrongAnswersCount = totalQuestions - correctAnswersCount;
            long totalDurationMillis = System.currentTimeMillis() - levelStartTime;

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("CORRECT_COUNT", correctAnswersCount);
            intent.putExtra("WRONG_COUNT", wrongAnswersCount);
            intent.putExtra("LEVEL_POSITION", levelPosition);
            intent.putExtra("LEVEL_DIFFICULTY", levelDifficulty);
            intent.putExtra("TOTAL_DURATION", totalDurationMillis);

            intent.putExtra("FINAL_SCORE", currentTotalScore);
            intent.putExtra("FINAL_RANK", txtRank.getText().toString());

            intent.putExtra("USER_LEVEL", getIntent().getIntExtra("USER_LEVEL", 1));
            intent.putExtra("USER_XP", getIntent().getFloatExtra("USER_XP", 0.0f));
            startActivity(intent);

            finish();
            return;
        }

        inputAnswer.setText("");

        Question currentQuestion = questionList.get(index);
        txtQuestion.setText(currentQuestion.getText());

        showLayoutForType(currentQuestion.getType());

        if ("multiple_choice".equals(currentQuestion.getType())) {
            List<String> options = currentQuestion.getOptions();
            if (options != null && options.size() >= 4) {
                btnOpt1.setText(options.get(0));
                btnOpt2.setText(options.get(1));
                btnOpt3.setText(options.get(2));
                btnOpt4.setText(options.get(3));
            }
        }

        startTimer();
    }

    private void updateRealTimeRank() {
        if (questionList.isEmpty()) return;

        int questionsProcessed = currentQuestionIndex + 1;
        int wrongCount = questionsProcessed - correctAnswersCount;
        float successRate = (float) correctAnswersCount / questionsProcessed;

        if (wrongCount == 0 && successRate >= 0.9f) {
            txtRank.setText("S");
        } else if (successRate >= 0.75f) {
            txtRank.setText("A");
        } else if (successRate >= 0.5f) {
            txtRank.setText("B");
        } else if (successRate >= 0.3f) {
            txtRank.setText("C");
        } else {
            txtRank.setText("D");
        }
    }

    private void showLayoutForType(String type) {
        containerMultipleChoice.setVisibility(View.GONE);
        containerTrueFalse.setVisibility(View.GONE);
        containerTextInput.setVisibility(View.GONE);

        if (type == null) return;

        if ("multiple_choice".equals(type)) {
            containerMultipleChoice.setVisibility(View.VISIBLE);
        } else if ("true_false".equals(type)) {
            containerTrueFalse.setVisibility(View.VISIBLE);
        } else if ("text_input".equals(type)) {
            containerTextInput.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}