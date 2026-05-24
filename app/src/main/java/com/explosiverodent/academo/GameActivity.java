package com.explosiverodent.academo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    }

    private void loadLevel() {
        userId = getIntent().getIntExtra("USER_ID", -1);

        String levelTitle = getIntent().getStringExtra("LEVEL_TITLE");
        if (levelTitle != null) {
            txtLevelTitle.setText(levelTitle.toUpperCase());
        }

        int rawResourceId = getIntent().getIntExtra("LEVEL_RESOURCE", -1);
        if (rawResourceId == -1) {
            finish();
            return;
        }

        questionList = JsonReader.loadQuestions(this, rawResourceId);
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

    private void checkAnswer(String playerAnswer) {
        if (currentQuestionIndex >= questionList.size()) return;

        Question currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        if (playerAnswer.equalsIgnoreCase(correctAnswer)) {
            correctAnswersCount++;
        }

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

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("CORRECT_COUNT", correctAnswersCount);
            intent.putExtra("WRONG_COUNT", wrongAnswersCount);

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
}