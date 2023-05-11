package com.example.crossdle.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crossdle.R;
import com.example.crossdle.game.Board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game that has been played in the past by a user and holds all relevant information
 * so that a user can view this game data in the future.
 */
public class HistoryItem implements Serializable {
    private String gameId;
    private String time;
    private int letters;
    private int attempts;
    private List<String> layout;
    private List<String> colourLayout;

    public HistoryItem(String gameId, String time, int letters, int attempts,
                       List<String> layout, List<String> colourLayout) {
        this.gameId = gameId;
        this.time = time;
        this.letters = letters;
        this.attempts = attempts;
        this.layout = layout;
        this.colourLayout = colourLayout;
    }

    public HistoryItem() {}

    public String getGameId() { return gameId; }
    public String getTime() {
        return time;
    }
    public int getLetters() {
        return letters;
    }
    public int getAttempts() { return attempts; }
    public List<String> getLayout() { return layout; }
    public List<String> getColourLayout() { return colourLayout; }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLetters(int letters) {
        this.letters = letters;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setLayout(List<String> layout) {
        this.layout = layout;
    }

    public void setColourLayout(List<String> colourLayout) {
        this.colourLayout = colourLayout;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public char[][] toCharArr(List<String> strings) {
        char[][] charArr = new char[6][6];
        int count = 0;
        String str = "";
        for (int k = 0; k < strings.size(); k++) {
            str+=strings.get(k);
        }
        char[] strToChar = str.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            for (int j = 0; j < charArr[0].length; j++) {
                charArr[i][j] = strToChar[count];
                count++;
            }
        }
        return charArr;
    }
}