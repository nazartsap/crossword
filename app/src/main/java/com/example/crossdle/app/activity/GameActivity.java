package com.example.crossdle.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.crossdle.app.HistoryItem;
import com.example.crossdle.app.fragment.BoardFragment;
import com.example.crossdle.R;
import com.example.crossdle.app.fragment.KeyboardFragment;
import com.example.crossdle.app.popup.FinishedGamePopup;
import com.example.crossdle.bank.WordBase;
import com.example.crossdle.game.Board;
import com.example.crossdle.game.BoardView;
import com.example.crossdle.bank.WordDictionary;
import com.example.crossdle.game.LayoutGenerator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *  GameActivity is the activity where the game is played from.
 *  It contains functions relating to game logic and the board.
 */
public class GameActivity extends AppCompatActivity {

    //Stores information regarding whether this is a daily or random puzzle.
    public static final String ARG_TYPE = "ARG_TYPE";

    private Board board;
    private BoardFragment boardFragment;
    private KeyboardFragment keyboardFragment;
    private MediaPlayer mediaPlayer;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private long startTime;
    private String timeTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        WordBase.load(this);
        WordDictionary.load(this);
        Intent intent2 = getIntent();
        String themeData = intent2.getStringExtra("theme");
        LinearLayout linearLayout = findViewById(R.id.linear_layout_game);

        if (themeData != null) {
            switch (themeData) {
                case "Ocean":
                    linearLayout.setBackgroundResource(R.drawable.gradient_list_ocean);
                    break;
                case "Emerald Forest":
                    linearLayout.setBackgroundResource(R.drawable.gradient_list_emerald_green);
                    break;
                case "Strawberry":
                    linearLayout.setBackgroundResource(R.drawable.gradient_list_strawberry);
                    break;
                default:
                    linearLayout.setBackgroundResource(R.drawable.gradient_list);
                    break;
            }
        }

        Intent intent = getIntent();
        boolean type = intent.getBooleanExtra(ARG_TYPE, false);

        //Sets the board given the game mode.
        if (type) {
            TextView titleView = findViewById(R.id.game_textView_title);
            titleView.setText(getResources().getString(R.string.game_random_title));
            try {
                createBoard(LayoutGenerator.returnBoard());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            GameActivity.getDailyBoard(this::createBoard);
        }

        startTime = System.currentTimeMillis();

        mediaPlayer = MediaPlayer.create(this, R.raw.game_song);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void createBoard(char[][] layout) {
        // Initializes a Board.
        BoardView view = new BoardView();
        board = new Board(view, layout);

        keyboardFragment = KeyboardFragment.frame(getSupportFragmentManager(), R.id.game_fragmentView_keyboard, board);
        boardFragment = BoardFragment.frame(getSupportFragmentManager(), R.id.game_fragmentView_board, board);

        board.setOnWin(this::win);
        board.setOnLose(this::lose);

        view.setViewHandler(boardFragment::getView);
        view.setViewKeyboardHandler(keyboardFragment::getView);
    }

    @Override
    protected void onPause() {
        //Stops the music on pause.
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void win() {
        //Handles the functions after the user wins.
        writeBoardToDatabase();
        int duration = 2000;
        View view = boardFragment.getView();
        animateWin(view, duration);
        view.postDelayed(() -> {
            startFinishedGame("ТЫ ВЫИГРАЛ!");
        }, (long)(duration * 0.7));
    }

    public void lose() {
        //Handles the functions after the user loses.
        writeBoardToDatabase();
        int duration = 2000;
        View view = boardFragment.getView();
        animateLose(view, duration);

        view.postDelayed(() -> startFinishedGame("Game Over!"), (long)(duration * 0.7));
    }

    private void startFinishedGame(String title) {
        //Upon game completion load pop-up.
        Intent intent = new Intent(this, FinishedGamePopup.class);
        intent.putExtra("time_taken", timeTaken);
        intent.putExtra("attempts_taken", String.valueOf(board.getAttemptsTaken()));
        intent.putExtra("title", title);
        intent.putExtra("board", title);
        startActivity(intent);
    }

    private void animateWin(View view, int duration) {
        //Animations upon winning
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.mixed_anim);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    private void animateLose(View view, int duration) {
        //Animations upon losing
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.mixed_anim);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Keyboard functionality for android emulators.
         if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                board.clickBack();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                board.clickEnter();
                return true;
            } else {
                char character = (char)event.getUnicodeChar(event.getMetaState());
                if (Character.isLetter(character)) {
                    board.clickKey((Character.toUpperCase(character)));
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void writeBoardToDatabase() {
        //Writes a history item object into Firestore.
        long gameLength = System.currentTimeMillis() - startTime;
        timeTaken = String.valueOf(gameLength/1000.0);
        DocumentReference historyRef = db.collection("history").document(user.getUid());
        Map<String, Object> count = new HashMap<>();
        count.put("board_count", FieldValue.increment(1));
        if(board.getAttemptsRemaining() > 0){
            count.put("streak", FieldValue.increment(1));
            count.put("wins", FieldValue.increment(1));
        }else{
            historyRef.update("streak", 0);
        }

        historyRef.set(count, SetOptions.merge());
        List<String> list = board.toDatabaseLayout();
        int correctLetters = correctLetters(list, board.toDatabaseValues());
        historyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String boardCount = (String.valueOf(document.get(("board_count"))));
                    HistoryItem history = new HistoryItem(boardCount, timeTaken, correctLetters, board.getAttemptsTaken(), list, board.toDatabaseValues());
                    historyRef.collection("games")
                            .document(boardCount)
                            .set(history)
                            .addOnSuccessListener(aVoid -> Log.d("W", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("W", "Error writing document", e));
                } else {
                    Log.d("W", "No such document");
                }

            } else {
                Log.d("W", "get failed with ", task.getException());
            }
        });
    }

    public static int correctLetters(List<String> list1, List<String> list2){
        //Returns the count of how many correct letters were guessed.
        int count = 0;
        for(int i = 0;i<list1.size();i++){
            if(list1.get(i).equals(list2.get(i))){
                count+=1;
            }
        }
        return count;
    }

    public static void getDailyBoard(Consumer<char[][]> onComplete){
        //Loads a daily board from Firestore that is changed every 24 hours.
        long secondsNow = System.currentTimeMillis()/1000L;
        long secondsInADay= 86400L;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dailyRef = db.collection("daily").document("daily");
        dailyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    long dailyTime = (long) document.get("time");
                    if(secondsNow-dailyTime>secondsInADay){
                        Map<String, Object> daily = new HashMap<>();
                        try {
                            char[][] layout = LayoutGenerator.returnBoard();
                            daily.put("daily", Board.charToList(layout));
                            daily.put("time", System.currentTimeMillis()/1000L);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dailyRef.set(daily).addOnSuccessListener(aVoid -> Log.d("W", "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w("W", "Error writing document", e));
                    }else{

                        dailyRef.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document1 = task1.getResult();
                                if (document1.exists()) {
                                    Log.d("W", "DocumentSnapshot data: " + document1.getData());
                                    List<String> list = (List<String>) document1.getData().get("daily");
                                    onComplete.accept(Board.listToChar(list));
                                } else {
                                    Log.d("W", "No such document");
                                }
                            } else {
                                Log.d("W", "get failed with ", task1.getException());
                            }
                        });
                    }
                }
            }
        });
    }
}