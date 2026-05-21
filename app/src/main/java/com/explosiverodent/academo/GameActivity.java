package com.explosiverodent.academo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.explosiverodent.academo.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private TextView txtLevelTitle, txtQuestion;
    private LinearLayout containerMultipleChoice, containerTrueFalse, containerTextInput;
    private Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    private Button btnTrue, btnFalse;
    private EditText inputAnswer;
    private Button btnSubmitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        initViews();

        setupClickListeners();

        String levelTitle = getIntent().getStringExtra("LEVEL_TITLE");
        if (levelTitle != null) {
            txtLevelTitle.setText(levelTitle.toUpperCase());
        }

        readLevelData();
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

    private void setupClickListeners() {
        btnOpt1.setOnClickListener(v -> checkAnswer(btnOpt1.getText().toString()));
        btnOpt2.setOnClickListener(v -> checkAnswer(btnOpt2.getText().toString()));
        btnOpt3.setOnClickListener(v -> checkAnswer(btnOpt3.getText().toString()));
        btnOpt4.setOnClickListener(v -> checkAnswer(btnOpt4.getText().toString()));

        btnTrue.setOnClickListener(v -> checkAnswer("true"));
        btnFalse.setOnClickListener(v -> checkAnswer("false"));

        // Free Written Text Answer Button
        btnSubmitText.setOnClickListener(v -> {
            String playerText = inputAnswer.getText().toString().trim();
            if (!playerText.isEmpty()) {
                checkAnswer(playerText);
            } else {
                Toast.makeText(this, "Please type an answer first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAnswer(String playerAnswer) {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        if (playerAnswer.equalsIgnoreCase(correctAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! Answer was: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        currentQuestionIndex++;
        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int index) {
        if (index >= questionList.size()) {
            txtQuestion.setText("Level Complete!");
            containerMultipleChoice.setVisibility(View.GONE);
            containerTrueFalse.setVisibility(View.GONE);
            containerTextInput.setVisibility(View.GONE);
            return;
        }

        Question currentQuestion = questionList.get(index);
        txtQuestion.setText(currentQuestion.getText());

        showLayoutForType(currentQuestion.getType());

        if (currentQuestion.getType().equals("multiple_choice")) {
            List<String> opts = currentQuestion.getOptions();
            if (opts.size() >= 4) {
                btnOpt1.setText(opts.get(0));
                btnOpt2.setText(opts.get(1));
                btnOpt3.setText(opts.get(2));
                btnOpt4.setText(opts.get(3));
            }
        }
    }

    private void showLayoutForType(String type) {
        containerMultipleChoice.setVisibility(View.GONE);
        containerTrueFalse.setVisibility(View.GONE);
        containerTextInput.setVisibility(View.GONE);

        switch (type) {
            case "multiple_choice":
                containerMultipleChoice.setVisibility(View.VISIBLE);
                break;
            case "true_false":
                containerTrueFalse.setVisibility(View.VISIBLE);
                break;
            case "text_input":
                containerTextInput.setVisibility(View.VISIBLE);
                inputAnswer.setText("");
                break;
        }
    }

    void readLevelData() {
        String jsonFile = getIntent().getStringExtra("JSON_FILE");
        if (jsonFile == null) return;

        String jsonString = null;
        try {
            InputStream is = getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String type = obj.getString("type");
                String text = obj.getString("text");
                String correctAnswer = obj.getString("correctAnswer");

                List<String> options = new ArrayList<>();
                JSONArray optionsArray = obj.getJSONArray("options");
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }
                System.out.println(options);
                questionList.add(new Question(type, text, options, correctAnswer));
            }

            if (!questionList.isEmpty()) {
                displayQuestion(currentQuestionIndex);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            android.util.Log.e("QUIZ_ERROR", "Failed to load quiz data", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}