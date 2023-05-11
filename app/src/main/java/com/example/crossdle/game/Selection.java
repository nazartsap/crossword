package com.example.crossdle.game;

import java.io.Serializable;

/***
 * Represents a selection in the game state. A selection can be targeting nothing (null) or a
 * word. If it is targeting a word a current cell value is stored from that word representing the
 * current letter position of the selection.
 */
public class Selection implements Serializable
{
    private Word word;
    private Cell current;

    public Word getWord() { return word; }
    public Cell getCurrent() { return current; }

    public void setCurrent(Cell cell) {
        if (current != null) { current.setActive(false); }
        if (cell == null) { return; }
        current = cell;
        current.setActive(true);
    }

    public boolean containsCell(Cell cell) {
        if (word == null) {
            return false;
        }

        return word.containsCell(cell);
    }

    public boolean isSet() { return word != null; }

    /**
     * Confirms attempts of current selection by updating the values of the cells in selection
     * to the letters made in attempts of cells.
     */
    public void confirm() {
        if (word == null) { return; }

        for (Cell cell : word.getCells()) {
            cell.acceptAttempt();
        }

        update(null);
    }

    /**
     * Clears attempts of current selection.
     */
    public void reset() {
        if (word == null) { return; }

        for (Cell cell : word.getCells()) {
            cell.clearAttempt();
        }

        update(null);
    }

    /**
     * Set current cell attempt to a value and move to next cell in selection.
     */
    public void next(char value) {
        Cell destination = current.getNeighbour(word.getOrientation(), true);
        current.setAttempt(value);
        if (destination == null || !destination.isSet()) {
            return;
        }
        setCurrent(destination);
    }

    /**
     * Unset current cell and previous cell and move to previous cell in selection.
     */
    public void prev() {
        Cell destination = current.getNeighbour(word.getOrientation(), false);
        current.setAttempt(Character.MIN_VALUE);
        if (destination == null || !destination.isSet()) {
            return;
        }
        setCurrent(destination);
        current.setAttempt(Character.MIN_VALUE);
    }

    /**
     * Set selection to a new word.
     */
    public void update(Word word) {
        selectCells(false);
        this.word = word;
        if (isSet()) {
            setCurrent(this.word.getStart());
        } else {
            setCurrent(null);
        }
        selectCells(true);
    }

    /**
     * Sets all cells in current word to selected.
     */
    private void selectCells(boolean selected) {
        if (!isSet()) { return; }
        for (Cell cell : word.getCells()) {
            cell.setSelected(selected);
        }
    }
}
