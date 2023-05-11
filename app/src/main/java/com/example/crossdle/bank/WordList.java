package com.example.crossdle.bank;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Word list data holder class. Used to hold deserialized json data.
 */
public class WordList {
    @SerializedName("data")
    private ArrayList<String> words;

    public String get(int index) {
        return words.get(index);
    }

    public int size() {
        return words.size();
    }

    public String[] toArray() {
        return words.stream().toArray(String[]::new);
    }
}