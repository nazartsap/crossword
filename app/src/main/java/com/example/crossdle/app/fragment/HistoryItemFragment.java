package com.example.crossdle.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crossdle.R;
import com.example.crossdle.app.HistoryItem;
import com.example.crossdle.game.Board;
import com.example.crossdle.game.BoardView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This is a Fragment that holds that will holds/represents the History of the Game Activity.
 */
public class HistoryItemFragment extends Fragment {
    //This is a String that will be used as a "key" for intents.
    private static final String ARG_ITEM = "ARG_ITEM";

    // A HistoryItem object.
    private HistoryItem item;

    public HistoryItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (HistoryItem)getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_item, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView idView = view.findViewById(R.id.historyItem_textView_id);
        TextView timeView = view.findViewById(R.id.historyItem_textView_time);
        TextView wordsView = view.findViewById(R.id.historyItem_textView_words);
        TextView attemptsView = view.findViewById(R.id.historyItem_textView_attempts);

        idView.setText(item.getGameId());
        timeView.setText(item.getTime());
        wordsView.setText(String.valueOf(item.getLetters()));
        attemptsView.setText(String.valueOf(item.getAttempts()));
        setPreview(item.toCharArr(item.getLayout()));
    }

    /**
     * This method sets the preview for the user to see.
     * @param layout A 2D char array that is the layout of the BoardView.
     */
    private void setPreview(char[][] layout) {
        BoardView boardView = new BoardView();
        Board board = createBoard(boardView, layout);
        BoardFragment.frame(getChildFragmentManager(), R.id.historyItem_fragmentView_preview, board);
        boardView.setViewHandler(this::getView);
    }

    /**
     * This method recreates previous Crossdle Boards for the user to see.
     * @param boardView a BoardView object.
     * @param layout A 2D char array that is the layout of the BoardView.
     * @return a Board object.
     */
    private Board createBoard(BoardView boardView, char[][] layout) {
        Board board = new Board(boardView, layout);
        board.setActive(false);
        Queue values = new LinkedList(item.getColourLayout());
        board.forEach(cell -> cell.setValue(((String)values.poll()).charAt(0)));
        return board;
    }

    /**
     * This method creates an Instance of the History Item Fragment.
     */
    public static HistoryItemFragment newInstance(HistoryItem item) {
        HistoryItemFragment fragment = new HistoryItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }
}