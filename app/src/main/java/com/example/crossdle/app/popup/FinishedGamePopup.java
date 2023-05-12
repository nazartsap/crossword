package com.example.crossdle.app.popup;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crossdle.R;
import com.example.crossdle.app.activity.GameActivity;
import com.example.crossdle.app.activity.MainActivity;


public class FinishedGamePopup extends AppCompatActivity {

    private TextView timeView;

    private TextView attemptsView;


    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_game);
        timeView = findViewById(R.id.finished_textView_time);
        attemptsView = findViewById(R.id.finished_textView_attempts);
        titleView = findViewById(R.id.finished_textView_title);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int)(height*.6));

        Button buttonGame = findViewById(R.id.finished_button_game);
        buttonGame.setOnClickListener(this::onClickGame);

        Button buttonMenu = findViewById(R.id.finished_button_menu);
        buttonMenu.setOnClickListener(this::onClickMenu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String time = intent.getStringExtra("time_taken");
        String attempts = intent.getStringExtra("attempts_taken");
        String title = intent.getStringExtra("title");
        timeView.setText(time);
        attemptsView.setText(attempts);
        titleView.setText(title);

    }


    public void onClickGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.ARG_TYPE, true);
        startActivity(intent);
    }

    public void onClickMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}