package com.example.crossdle.app.activity;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.crossdle.R;
import com.example.crossdle.app.popup.SettingsPopup;

/**
 * The main menu Activity.
 * Contains the functions necessary to access other activities or features.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button[] buttons;

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;

    private boolean settingsOpen = false;

    String themeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.layout_main);

        Button settings = findViewById(R.id.button_main_settings);
        themeData = "Default";

        buttons = new Button[] {
                findViewById(R.id.button_main_daily_crossdle),
                findViewById(R.id.button_main_random_crossdle),
                findViewById(R.id.button_main_history),
                settings
        };

        for (Button button: buttons) {
            button.setOnClickListener(this);
        }

        settings.setOnClickListener(this::onOpenSettings);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }

    @Override
    protected void onResume() {
        //Starts animations and music when the app resumes.
        super.onResume();

        View layoutView = findViewById(R.id.layout_main);

        if (!settingsOpen) {
            int fadeDuration = 2000;
            animFadeIn(layoutView, fadeDuration);
            layoutView.postDelayed(() -> animSlideIn(findViewById(R.id.layout_main)), (long)(fadeDuration * 0.5));

            mediaPlayer = MediaPlayer.create(this, R.raw.menu_song);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else {
            settingsOpen = false;
        }
    }

    @Override
    protected void onPause() {
        //Stops te music on pause.
        super.onPause();

        if (!settingsOpen) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Sets the theme given a user choice.
        if (resultCode == SettingsPopup.RESULT_OK) {
            themeData = data.getStringExtra("theme");
            ConstraintLayout constraintLayout = findViewById(R.id.layout_main);

            if (themeData != null) {
                switch (themeData) {
                    case "Океан":
                        constraintLayout.setBackgroundResource(R.drawable.gradient_list_ocean);
                        break;
                    case "Лес":
                        constraintLayout.setBackgroundResource(R.drawable.gradient_list_emerald_green);
                        break;
                    case "Крастный":
                        constraintLayout.setBackgroundResource(R.drawable.gradient_list_strawberry);
                        break;
                    default:
                        constraintLayout.setBackgroundResource(R.drawable.gradient_list);
                        break;
                }
            } else {
                constraintLayout.setBackgroundResource(R.drawable.gradient_list);
                themeData = "Default";
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method is too assist the animation in the button fading into the main menu.
     */
    private void animFadeIn(View view, int duration) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * This method is too assist the animation in the button sliding into the main menu.
     */
    private void animSlideIn(View view) {
        //Sliding animations for menu buttons.
        int interval = 250;
        int duration = 1000;

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fui_slide_in_right);
            animation.setDuration(duration);
            view.postDelayed(() -> {
                buttons[index].startAnimation(animation);
                buttons[index].setVisibility(View.VISIBLE);
            }, (long) i * interval);
        }
    }

    /**
     * This method is too assist the animation in the button sliding out from the main menu.
     */
    private void animSlideOut(View view) {
        //Sliding animations for menu buttons.
        int interval = 250;
        int duration = 1000;

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fui_slide_out_left);
            animation.setDuration(duration);
            view.postDelayed(() -> {
                buttons[index].startAnimation(animation);
                view.postDelayed(() -> buttons[index].setVisibility(View.INVISIBLE), duration);
            }, (long) i * interval);
        }
    }

    @Override
    public void onClick(View view) {
        //Animations and sound effects for on click events.
        int timeout = 1500;
        animSlideOut(findViewById(R.id.layout_main));
        view.postDelayed(() -> changeActivity(view), timeout);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.button_sound_effect);
        mediaPlayer2.start();

    }

    /**
     * This method is Opens the settings pop-up.
     */
    public void onOpenSettings(View view) {
        //Opens the settings pop-up.
        settingsOpen = true;
        Intent intent = new Intent(this, SettingsPopup.class);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.button_sound_effect);
        mediaPlayer2.start();
        startActivityForResult(intent, 1);
    }

    /**
     * Starts other activities given user input.
     */
    private void changeActivity(View view) {
        if (view.getId() == R.id.button_main_daily_crossdle)  {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("theme", themeData);
            startActivity(intent);
        }
        if (view.getId() == R.id.button_main_random_crossdle) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(GameActivity.ARG_TYPE, true);
            intent.putExtra("theme", themeData);
            startActivity(intent);
        }
        if (view.getId() == R.id.button_main_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
    }
}