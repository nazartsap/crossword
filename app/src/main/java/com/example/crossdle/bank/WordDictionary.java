package com.example.crossdle.bank;
import android.content.Context;
import com.example.crossdle.app.FileHandler;
import java.util.HashMap;

/**
 * Holds dictionary data that can be used to check the validity of words against guesses
 * that the user makes.
 */
public class WordDictionary {
    private static final String FILE_NAME = "dictionary";
    public static WordDictionary cached;

    public HashMap<String, String> data;

    public boolean contains(String word) {
        return data.containsKey(word.toLowerCase());
    }

    public static WordDictionary load(Context context) {
        cached = FileHandler.openRawJson(context, FILE_NAME, WordDictionary.class);
        return cached;
    }
}