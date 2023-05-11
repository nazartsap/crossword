package com.example.crossdle.game;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.crossdle.R;

import java.io.Serializable;

/***
 * Represents a single cell on the game board. Has the capability to hold a value for its true
 * letter, the most recent letter in a guessed word, and a attempt letter for a word to be guessed.
 */
public class Cell implements Serializable
{
    public enum State
    {
        HIDDEN,
        WRONG,
        HINT,
        CORRECT;

        public int getColor() {
            switch (this) {
                default: return Cell.COLOR_HIDDEN;
                case WRONG: return Cell.COLOR_WRONG;
                case HINT: return Cell.COLOR_HINT;
                case CORRECT: return Cell.COLOR_CORRECT;
            }
        }
    }

    private static final int COLOR_EMPTY = Color.BLACK;
    private static final int COLOR_HIDDEN = Color.WHITE;
    private static final int COLOR_WRONG = Color.rgb(120, 124, 126);
    private static final int COLOR_HINT = Color.rgb(201, 180, 88);
    private static final int COLOR_CORRECT = Color.rgb(106, 170, 100);
    private static final int COLOR_SELECTED = Color.rgb(168, 209, 223);
    private static final int COLOR_ACTIVE = Color.rgb(0,255,255);

    private static final int SELECTED_STROKE = 7;
    private static final int SELECTED_RADIUS = 5;

    private final int x;
    private final int y;
    private char data;

    private char value;
    private char attempt;

    private boolean selected;
    private boolean active;

    private Neighbours neighbours;

    public Cell(char data, int x, int y) {
        this.data = data;
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public char getData() { return data; }
    public char getValue() { return this.value; }
    public char getAttempt() { return attempt; }

    /**
     * Gets a neighbouring cell in a given direction.
     */
    public Cell getNeighbour(Word.Orientation orientation, boolean next) {
        return neighbours.get(orientation, next);
    }

    /**
     * Gets the word that this cell is contained in, in a given direction.
     */
    public Word getWord(Word.Orientation orientation) {
        Cell cell = this;
        while (!cell.isHead(orientation)) {
            cell = cell.getNeighbour(orientation, false);
        }

        return new Word(cell, orientation);
    }

    /**
     * Gets current state depending on true data value compared to recent guess value.
     */
    public State getState() {
        if (this.value == data) {
            return State.CORRECT;
        } else if (wordsContainIncorrect(this.value)) {
            return State.HINT;
        } else if (this.value != Character.MIN_VALUE) {
            return State.WRONG;
        } else {
            return State.HIDDEN;
        }
    }

    public void setValue(char value) {
        this.value = value;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNeighbours(Cell up, Cell right, Cell down, Cell left) {
        this.neighbours = new Neighbours(up, right, down, left);
    }

    public void setAttempt(char value) { attempt = value; }
    public void clearAttempt() { attempt = Character.MIN_VALUE; }

    /**
     * Accepts an attempt setting this cell's most recent guess value to the attempt value.
     */
    public void acceptAttempt() {
        value = attempt;
        attempt = Character.MIN_VALUE;
    }

    public boolean isSet() { return data != Character.MIN_VALUE && !Character.isSpaceChar(data); }
    public boolean isCorrect() {
        return this.value == data;
    }

    /**
     * Checks if cell is currently attempted.
     */
    public boolean isAttempted() { return attempt != Character.MIN_VALUE; }

    /**
     * Checks if a cell is a head of a word in a given direction.
     */
    private boolean isHead(Word.Orientation orientation) {
        Cell neighbour = neighbours.get(orientation, false);
        return neighbour == null || !neighbour.isSet();
    }

    /**
     * Checks if either containing word contains an incorrect given character.
     */
    private boolean wordsContainIncorrect(char character) {
        return getWord(Word.Orientation.Horizontal).containsIncorrect(character)
                || getWord(Word.Orientation.Vertical).containsIncorrect(character);
    }

    /**
     * Draws the cell on a given view.
     */
    public void draw(TextView view) {
        if (isSet()) {
            view.setText(String.valueOf(attempt != Character.MIN_VALUE ? attempt : value));
            State state = getState();
            if (selected) {
                GradientDrawable background = new GradientDrawable();
                background.setColor(COLOR_HIDDEN);
                background.setCornerRadius(SELECTED_RADIUS);
                background.setStroke(SELECTED_STROKE, COLOR_SELECTED);
                if (attempt == Character.MIN_VALUE || attempt == value) {
                    background.setColor(state.getColor());
                }
                if (active) {
                    background.setColor(COLOR_SELECTED);
                    background.setStroke(SELECTED_STROKE, COLOR_ACTIVE);
                }
                view.setBackground(background);
            } else {
                view.setBackgroundColor(state.getColor());
            }
        } else {
            view.setBackgroundColor(COLOR_EMPTY);
        }
    }

    public void animateAttempt(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        view.startAnimation(animation);
    }

    public void animateInvalid(View view) {
        int repeats = 3;
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.blink_anim);
        animation.restrictDuration(animation.getDuration() * repeats);
        view.startAnimation(animation);
    }

    /**
     * Represents neighbours for the given cell in the 4 cardinal directions.
     */
    private class Neighbours implements Serializable
    {
        public final Cell up;
        public final Cell right;
        public final Cell down;
        public final Cell left;

        public Neighbours(Cell up, Cell right, Cell down, Cell left) {
            this.up = up;
            this.right = right;
            this.down = down;
            this.left = left;
        }

        public Cell get(Word.Orientation orientation, boolean next) {
            switch (orientation) {
                default: return next ? right : left;
                case Vertical: return next ? down : up;
            }
        }
    }
}
