package com.explosiverodent.academo.jsonreader;

import android.content.Context;
import android.util.Log;

import com.explosiverodent.academo.model.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonReader {

    public static List<Question> loadQuestions(Context context, int rawResourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            return parseQuestionsJson(jsonString);

        } catch (Exception e) {
            Log.e("JSON_READER", "Failed to load questions from raw resource.", e);
            return new ArrayList<>();
        }
    }

    public static List<Question> loadQuestionsFromStream(Context context, InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            reader.close();
            inputStream.close();

            return parseQuestionsJson(jsonStringBuilder.toString());

        } catch (Exception e) {
            Log.e("JSON_READER", "Failed to load questions from custom InputStream.", e);
            return new ArrayList<>();
        }
    }

    private static List<Question> parseQuestionsJson(String jsonString) {
        List<Question> questionList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String type = object.optString("type", "");
                String text = object.optString("text", "");
                String correctAnswer = object.optString("correctAnswer", "");

                List<String> options = new ArrayList<>();
                if (object.has("options")) {
                    JSONArray optionsArray = object.getJSONArray("options");
                    for (int j = 0; j < optionsArray.length(); j++) {
                        options.add(optionsArray.getString(j));
                    }
                }

                Question question = new Question(type, text, options, correctAnswer);
                questionList.add(question);
            }
        } catch (Exception e) {
            Log.e("JSON_READER", "Error parsing JSON string structure.", e);
        }
        return questionList;
    }
}