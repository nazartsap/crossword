package com.example.crossdle.game;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.crossdle.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents the view in MVC architecture for the board. Controls drawing the current state
 * to the user given the data that it is sent.
 */
public class BoardView implements Serializable {
    private transient Supplier<View> viewHandler;
    private transient Supplier<View> viewKeyboardHandler;

    private static int DEFAULT_KEYBOARD_COLOR = Color.parseColor("#777777");
    private static int DARK_KEYBOARD_COLOR = Color.parseColor("#444444");

    private static final int[][] LAYOUT = new int[][]
    {
        { R.id.board_cell_0_0, R.id.board_cell_1_0, R.id.board_cell_2_0, R.id.board_cell_3_0, R.id.board_cell_4_0, R.id.board_cell_5_0, },
        { R.id.board_cell_0_1, R.id.board_cell_1_1, R.id.board_cell_2_1, R.id.board_cell_3_1, R.id.board_cell_4_1, R.id.board_cell_5_1, },
        { R.id.board_cell_0_2, R.id.board_cell_1_2, R.id.board_cell_2_2, R.id.board_cell_3_2, R.id.board_cell_4_2, R.id.board_cell_5_2, },
        { R.id.board_cell_0_3, R.id.board_cell_1_3, R.id.board_cell_2_3, R.id.board_cell_3_3, R.id.board_cell_4_3, R.id.board_cell_5_3, },
        { R.id.board_cell_0_4, R.id.board_cell_1_4, R.id.board_cell_2_4, R.id.board_cell_3_4, R.id.board_cell_4_4, R.id.board_cell_5_4, },
        { R.id.board_cell_0_5, R.id.board_cell_1_5, R.id.board_cell_2_5, R.id.board_cell_3_5, R.id.board_cell_4_5, R.id.board_cell_5_5, }
    };

    private static Map<Character, Integer> LAYOUT_KEYBOARD;

    public BoardView() {
        LAYOUT_KEYBOARD = new HashMap();
        LAYOUT_KEYBOARD.put('Q', R.id.keyboard_button_q);
        LAYOUT_KEYBOARD.put('W', R.id.keyboard_button_w);
        LAYOUT_KEYBOARD.put('E', R.id.keyboard_button_e);
        LAYOUT_KEYBOARD.put('R', R.id.keyboard_button_r);
        LAYOUT_KEYBOARD.put('T', R.id.keyboard_button_t);
        LAYOUT_KEYBOARD.put('Y', R.id.keyboard_button_y);
        LAYOUT_KEYBOARD.put('U', R.id.keyboard_button_u);
        LAYOUT_KEYBOARD.put('I', R.id.keyboard_button_i);
        LAYOUT_KEYBOARD.put('O', R.id.keyboard_button_o);
        LAYOUT_KEYBOARD.put('P', R.id.keyboard_button_p);
        LAYOUT_KEYBOARD.put('A', R.id.keyboard_button_a);
        LAYOUT_KEYBOARD.put('S', R.id.keyboard_button_s);
        LAYOUT_KEYBOARD.put('D', R.id.keyboard_button_d);
        LAYOUT_KEYBOARD.put('F', R.id.keyboard_button_f);
        LAYOUT_KEYBOARD.put('G', R.id.keyboard_button_g);
        LAYOUT_KEYBOARD.put('H', R.id.keyboard_button_h);
        LAYOUT_KEYBOARD.put('J', R.id.keyboard_button_j);
        LAYOUT_KEYBOARD.put('K', R.id.keyboard_button_k);
        LAYOUT_KEYBOARD.put('L', R.id.keyboard_button_l);
        LAYOUT_KEYBOARD.put('Z', R.id.keyboard_button_z);
        LAYOUT_KEYBOARD.put('X', R.id.keyboard_button_x);
        LAYOUT_KEYBOARD.put('C', R.id.keyboard_button_c);
        LAYOUT_KEYBOARD.put('V', R.id.keyboard_button_v);
        LAYOUT_KEYBOARD.put('B', R.id.keyboard_button_b);
        LAYOUT_KEYBOARD.put('N', R.id.keyboard_button_n);
        LAYOUT_KEYBOARD.put('M', R.id.keyboard_button_m);
    }

    public void setViewHandler(Supplier<View> getView) {
        this.viewHandler = getView;
    }
    public void setViewKeyboardHandler(Supplier<View> getView) {
        this.viewKeyboardHandler = getView;
    }

    public void drawBoard(Cell[][] cells) {
        View view = viewHandler.get();
        if (view == null) { return; }
        for (int y = 0; y < LAYOUT.length; y++) {
            for (int x = 0; x < LAYOUT[y].length; x++) {
                Cell cell = cells[y][x];
                cell.draw(view.findViewById(LAYOUT[y][x]));
            }
        }
    }

    public void drawKeyboard(Cell[] cells) {
        View view = viewKeyboardHandler.get();

        for (char character : LAYOUT_KEYBOARD.keySet()) {
            View keyView = view.findViewById(LAYOUT_KEYBOARD.get(character));
            keyView.setBackgroundColor(DEFAULT_KEYBOARD_COLOR);
        }

        for (Cell cell : cells) {
            char value = cell.getValue();
            if (value == Character.MIN_VALUE) { continue; }
            View keyView = view.findViewById(LAYOUT_KEYBOARD.get(value));
            Cell.State state = cell.getState();
            if (state != Cell.State.WRONG) {
                keyView.setBackgroundColor(state.getColor());
            } else {
                keyView.setBackgroundColor(DARK_KEYBOARD_COLOR);
            }
        }
    }

    public void updateAttempts(int attempts) {
        View view = viewHandler.get();
        if (view == null) { return; }
        TextView viewAttempts = view.findViewById(R.id.board_textView_attempts);
        viewAttempts.setText(String.valueOf(attempts));
    }

    public void animateCellAttempt(Cell cell) {
        View view = viewHandler.get();
        if (view == null) { return; }
        cell.animateAttempt(view.findViewById(LAYOUT[cell.getY()][cell.getX()]));
    }

    public void animateCellInvalid(Cell cell) {
        View view = viewHandler.get();
        if (view == null) { return; }
        cell.animateInvalid(view.findViewById(LAYOUT[cell.getY()][cell.getX()]));
    }
}
