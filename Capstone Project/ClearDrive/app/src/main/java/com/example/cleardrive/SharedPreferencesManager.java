package com.example.cleardrive;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static SharedPreferences sharedPreferences;

    // Method to get SharedPreferences instance
    private static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    // Method to save amountInCAD to SharedPreferences
    public static void saveAmountInCAD(Context context, float amountInCAD) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat("amountInCAD", amountInCAD);
        editor.apply();
    }

    // Method to retrieve amountInCAD from SharedPreferences
    public static float getAmountInCAD(Context context) {
        return getSharedPreferences(context).getFloat("amountInCAD", 0.0f);
    }

}