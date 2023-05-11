package com.example.crossdle.app.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.crossdle.R;
import com.example.crossdle.app.HistoryItem;
import com.example.crossdle.app.fragment.HistoryItemFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


/**
 * HistoryActivity is the activity where the players history is displayed.
 * It contains functions that manages HistoryItem objects.
 */
public class HistoryActivity extends FragmentActivity {

    private final ArrayList<HistoryItem> items = new ArrayList<>();

    private TextView gameView;
    private TextView streakView;
    private TextView winView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        gameView = findViewById(R.id.history_textView_games);
        streakView = findViewById(R.id.history_textView_streak);
        winView = findViewById(R.id.history_textView_win);
        viewPager = findViewById(R.id.history_viewPager);
    }

    @Override
    protected void onStart() {
        //Renews the history every time its visited.
        super.onStart();
        readStats();
        readHistory(() -> {
            FragmentStateAdapter adapter = new HistoryPagerAdapter(this);
            viewPager.setAdapter(adapter);
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() <= 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void readHistory(Runnable onComplete) {
        //Reads HistoryItem objects from Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("history").document(user.getUid());

        docRef.collection("games")
              .orderBy("gameId", Query.Direction.DESCENDING)
              .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    HistoryItem history = document.toObject(HistoryItem.class);
                    items.add(history);
                }
                onComplete.run();
            } else {
                Log.d("W", "Error getting documents: ", task.getException());
            }
        });
    }


    public void readStats() {
        //Reads players statistics from FireStore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference historyRef = db.collection("history").document(user.getUid());
        historyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    int games = document.getLong("board_count").intValue();
                    gameView.setText(String.valueOf(games));

                    int streak = document.getLong("streak").intValue();
                    streakView.setText(String.valueOf(streak));

                    int wins = document.getLong("wins").intValue();
                    winView.setText(String.format("%.2f", wins * 100.0 / games) + "%");
                }
            }
        });
    }

    private class HistoryPagerAdapter extends FragmentStateAdapter {
        //A View Pager where History is displayed.

        public HistoryPagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return HistoryItemFragment.newInstance(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}

