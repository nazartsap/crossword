package com.example.crossdle.game;

import com.example.crossdle.bank.WordDictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a word on the board in the game state. A word is comprised of a group of adjacent
 * cells in a horizontal or vertical direction.
 */
public class Word implements Serializable
{
    public enum Orientation
    {
        Horizontal,
        Vertical
    }

    private Cell start;
    private Orientation orientation;

    public Word(Cell start, Orientation orientation) {
        this.start = start;
        this.orientation = orientation;
    }

    /**
     * Checks if all the attempt letters of cells in this word form a valid word
     * in the dictionary.
     */
    public boolean isAttemptValid() {
        return WordDictionary.cached.contains(toAttemptString());
    }

    /**
     * Checks if all the cells have an attempt letter assigned.
     */
    public boolean isFilled() {
        for (Cell cell : getCells()) {
            if (!cell.isAttempted()) {
                return false;
            }
        }

        return true;
    }

    public int getSize() {
        return getCells().length;
    }

    /**
     * Gets the head of the word.
     */
    public Cell getStart() {
        return start;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Gets all the cells that make up the word.
     */
    public Cell[] getCells() {
        Cell cell = start;
        List<Cell> cells = new ArrayList();
        do
        {
            cells.add(cell);
            cell = cell.getNeighbour(orientation, true);
        } while (cell != null && cell.isSet());

        Cell[] cellArray = new Cell[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            cellArray[i] = cells.get(i);
        }

        return cellArray;
    }

    /**
     * Checks if the word contains a cell.
     */
    public boolean containsCell(Cell cell) {
        boolean found = false;
        for (Cell wordCell : getCells()) {
            if (wordCell == cell) {
                found = true;
            }
        }

        return found;
    }

    /***
     * Checks if the word contains an incorrect cell with given character data.
     */
    public boolean containsIncorrect(char character) {
        boolean found = false;
        for (Cell cell : getCells()) {
            if (cell.getData() == character && !cell.isCorrect()) {
                found = true;
            }
        }

        return found;
    }

    @Override
    public String toString() {
        String string = "";
        for (Cell cell : getCells()) {
            string += cell.getData();
        }
        return string;
    }

    /**
     * Returns a string of joined cell attempts.
     */
    public String toAttemptString() {
        String string = "";
        for (Cell cell : getCells()) {
            string += cell.getAttempt();
        }
        return string;
    }
}
