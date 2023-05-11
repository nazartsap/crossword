package com.example.crossdle.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

/***
 * Represents the model in the MVC architecture for a board. Holds the cells of data pertaining
 * to the game state. This is the main interface for interacting with most things game related.
 */
public class Board implements Serializable
{
    public static final int DEFAULT_SIZE = 6;
    public static final int DEFAULT_ATTEMPTS = 20;

    public static final char EMPTY_LAYOUT_CELL = ' ';
    public static final char[][] TEST_LAYOUT = new char[][] {
        new char[] { ' ', 'T', ' ', ' ', ' ', ' ', },
        new char[] { ' ', 'E', ' ', 'S', ' ', ' ', },
        new char[] { ' ', 'S', 'E', 'N', 'D', ' ', },
        new char[] { ' ', 'T', ' ', 'A', ' ', ' ', },
        new char[] { ' ', ' ', ' ', 'K', ' ', ' ', },
        new char[] { ' ', ' ', ' ', 'E', ' ', ' '  },
    };

    private final BoardView view;
    private final Cell[][] data;
    private final int size;

    private int attemptsRemaining = DEFAULT_ATTEMPTS;
    private boolean active = true;
    private Selection selection = new Selection();

    private transient Runnable onWin;
    private transient Runnable onLose;

    // For Reference //
    private static final String[] COORDINATE_SYSTEM =
    {
        "[0, 0]", "[1, 0]",
        "[0, 1]", "[1, 1]"
    };

    public Board(BoardView view, int size, char... data) {
        this.view = view;
        this.size = size;
        this.data = wrapData(size, data);
    }

    public Board(BoardView view, char[] data) {
        this.view = view;
        this.size = DEFAULT_SIZE;
        this.data = wrapData(size, data);
    }

    public Board(BoardView view, char[][] data) {
        this.view = view;
        this.size = data.length;
        this.data = convertData(data);
    }

    public Cell getCell(int x, int y) {
        return data[y][x];
    }

    public int getAttemptsTaken() {
        return DEFAULT_ATTEMPTS - attemptsRemaining;
    }
    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }

    /**
     * Gets the current selection.
     */
    public Selection getSelection() { return selection; }

    /**
     * Sets a flag allowing or disallowing the change of the board.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOnWin(Runnable runnable) {
        onWin = runnable;
    }
    public void setOnLose(Runnable runnable) {
        onLose = runnable;
    }

    /**
     * Checks if the board is fully complete and correct.
     */
    private boolean isComplete() {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                Cell cell = data[y][x];
                if (cell.isSet() && !cell.isCorrect()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Iterates through every cell on the board and applies a given action to it.
     */
    public void forEach(Consumer<Cell> onNextCell) {
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                onNextCell.accept(data[y][x]);
            }
        }
    }

    public void clickCell(int x, int y) {
        select(x, y);
        draw();
        drawKeyboard();
    }

    public void clickKey(char character) {
        if (!active) { return; }
        if (!getSelection().isSet()) { return; }
        animateAttempt(getSelection().getCurrent());
        getSelection().next(character);
        draw();
    }

    public void clickEnter() {
        if (!active) { return; }
        if (!selection.isSet()) {
            return;
        }
        guess();
        draw();
    }

    public void clickBack() {
        if (!active) { return; }
        if (!selection.isSet()) { return; }
        selection.prev();
        draw();
    }

    /**
     * Attempts to make a guess at the currently selected word. The word must be completely
     * filled out, and it must be a valid word in the dictionary.
     */
    public void guess() {
        Word word = selection.getWord();

        if (!word.isFilled()) {
            animateInvalidWord(word);
            return;
        }

        if (!word.isAttemptValid()) {
            animateInvalidWord(word);
            return;
        }

        selection.confirm();

        if (isComplete()) {
            win();
        }

        attemptsRemaining -= 1;

        if (attemptsRemaining <= 0) {
            lose();
        }
    }

    /**
     * Selects the word for the cell at given coordinates or deselects if there is no set cell
     * at coordinates.
     */
    public void select(int x, int y) {
        if (!active) { return; }

        Cell cell = getCell(x, y);

        if (selection.containsCell(cell)) {
            selection.setCurrent(cell);
            return;
        }

        selection.reset();

        if (cell.isSet()) {
            Word horizontalWord = cell.getWord(Word.Orientation.Horizontal);
            Word verticalWord = cell.getWord(Word.Orientation.Vertical);
            selection.update(horizontalWord.getSize() > 1 ? horizontalWord : verticalWord);
        }
    }

    private void win() {
        if (onWin == null) { return; }
        onWin.run();
    }

    private void lose() {
        if (onLose == null) { return; }
        onLose.run();
    }

    /**
     * Converts raw char data to cell data.
     */
    private Cell[][] convertData(char[][] rawData) {
        Cell[][] data = new Cell[rawData.length][rawData.length];

        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                data[y][x] = new Cell(Character.toUpperCase(rawData[y][x]), x, y);
            }
        }

        linkCells(data);

        return data;
    }

    /**
     * Wraps and converts raw char data to cell data.
     */
    private Cell[][] wrapData(int size, char[] rawData) {
        Queue<Character> queue = new LinkedList();

        for (char character : rawData) {
            queue.add(character);
        }

        Cell[][] data = new Cell[size][size];
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                if (!queue.isEmpty()) {
                    data[y][x] = new Cell(Character.toUpperCase(queue.poll()), x, y);
                }
            }
        }

        linkCells(data);

        return data;
    }

    /**
     * Links all cells with neighbouring cells using given cell data.
     */
    private void linkCells(Cell[][] data) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int nextIndex;
                Cell up = null;
                Cell right = null;
                Cell down = null;
                Cell left = null;

                nextIndex = y - 1;
                if (nextIndex >= 0) {
                    up = data[nextIndex][x];
                }

                nextIndex = x + 1;
                if (nextIndex < size) {
                    right = data[y][nextIndex];
                }

                nextIndex = y + 1;
                if (nextIndex < size) {
                    down = data[nextIndex][x];
                }

                nextIndex = x - 1;
                if (nextIndex >= 0) {
                    left = data[y][nextIndex];
                }

                data[y][x].setNeighbours(up, right, down, left);
            }
        }
    }

    public void draw() {
        if (view == null) { return; }
        view.drawBoard(data);
        view.updateAttempts(attemptsRemaining);
    }

    public void drawKeyboard() {
        if (view == null) { return; }
        if (selection.getWord() == null) { return; }
        view.drawKeyboard(selection.getWord().getCells());
    }

    public void animateAttempt(Cell cell) {
        if (view == null) { return; }
        if (cell == null) { return; }
        view.animateCellAttempt(cell);
    }

    public void animateInvalidWord(Word word) {
        if (view == null) { return; }
        if (word == null) { return; }
        for (Cell cell : word.getCells()) {
            view.animateCellInvalid(cell);
        }
    }

    public static char[][] generateEmptyLayout(int size) {
        char[][] board = new char [size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = EMPTY_LAYOUT_CELL;
            }
        }

        return board;
    }

    public List<String> toDatabaseValues() {
        List<String> values = new ArrayList();
        forEach(cell -> {
            values.add(String.valueOf(cell.getValue()));
        });
        return values;
    }

    public List<String> toDatabaseLayout() {
        List<String> values = new ArrayList();
        forEach(cell -> {
            values.add(String.valueOf(cell.getData()));
        });
        return values;
    }

    public static char[][] listToChar(List<String> list){
        char[][] arr = new char[6][6];
        int count = 0;
        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[y].length; x++) {
                arr[y][x] = list.get(count).charAt(0);
                count+=1;
            }
        }
        return arr;
    }
    public static List<String> charToList(char[][] data) {
        List<String> list = new ArrayList<>();
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                list.add(Character.toString(data[y][x]));
            }
        }
        return list;
    }

}
