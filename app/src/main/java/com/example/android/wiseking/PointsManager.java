package com.example.android.wiseking;

import android.content.Context;
import android.content.SharedPreferences;


public class PointsManager {
    private SharedPreferences sharedPreferences;

    public PointsManager(Context context) {
        sharedPreferences = context.getSharedPreferences("points", Context.MODE_PRIVATE);
    }

    public void savePoint(int point) {
        String points = sharedPreferences.getString("points", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!points.isEmpty()) {
            editor.putString("points", points + "," + String.valueOf(point));
        } else {
            editor.putString("points", String.valueOf(point));
        }
        editor.apply();
    }

    public int getBestRecord() {
        String points = sharedPreferences.getString("points", "");
        int bestRecord = 0;
        String[] pointsArray = points.split(",");
        for (int i = 0; i < pointsArray.length; i++) {
            int record = Integer.parseInt(pointsArray[i]);
            if (record > bestRecord) {
                bestRecord = record;
            }
        }
        return bestRecord;
    }
}
