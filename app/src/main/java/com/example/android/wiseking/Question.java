package com.example.android.wiseking;

import java.util.ArrayList;
import java.util.List;


public class Question {
    private String question;
    private int answer;
    private List<String> options = new ArrayList<>();

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
