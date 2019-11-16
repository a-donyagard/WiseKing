package com.example.android.wiseking;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuestionManager {
    private static final String TAG = "QuestionManager";
    private List<Question> questions = new ArrayList<>();

    public QuestionManager(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("questions.json");
            String json = null;
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
            parseJson(json);
        } catch (IOException e) {
            Log.e(TAG, "QuestionManager: " + e.toString());
        }
    }

    private void parseJson(String json) {
        try {
            JSONArray questionsJsonArray = new JSONArray(json);
            JSONObject questionJsonObject;
            for (int i = 0; i < questionsJsonArray.length(); i++) {
                questionJsonObject = questionsJsonArray.getJSONObject(i);
                Question question = new Question();
                question.setAnswer(questionJsonObject.getInt("answer"));
                question.setQuestion(questionJsonObject.getString("question"));
                List<String> options = new ArrayList<>();
                JSONArray optionsJsonArray = questionJsonObject.getJSONArray("options");
                for (int j = 0; j < optionsJsonArray.length(); j++) {
                    options.add(optionsJsonArray.getString(j));
                }
                question.setOptions(options);
                this.questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Question getQuestion() {
        if (questions.isEmpty()) return null;

        Random random = new Random();
        int position = random.nextInt(questions.size());
        while (isAskedBefore(position)) {
            position = random.nextInt(questions.size());
        }
        askedQuestions.add(position);
        return questions.get(position);
    }

    private List<Integer> askedQuestions = new ArrayList<>();

    private boolean isAskedBefore(int position) {
        if (askedQuestions.size() == questions.size()) {
            askedQuestions.clear();
        }
        return askedQuestions.contains(position);
    }
}