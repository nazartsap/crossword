package com.example.crossdle.app.popup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.crossdle.R;
import com.example.crossdle.app.activity.MainActivity;

import java.util.List;

/**
 * This class contains methods/variables that support the settings popup in the main menu.
 */
public class SettingsPopup extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    // An AudioManager that supports the background music/sound effects of the app.
    AudioManager audioManager;

    // An int that references the current volume of the app.
    int currentVolume;

    // A Spinner object that holds the String array of colour themes for the game.
    Spinner themesSpinner;

    // A String array that holds the colour themes for the game.
    String[] themesArray;

    // An String that references the current selected colour theme.
    String selectedTheme;

    // an Interface for accessing and modifying preference data. Particularly used for helping save
    // the theme the user has picked in the spinner. Basically, if the user looks at the spinner
    // again, they'll see their last choice first.
    private SharedPreferences mMyPrefs;

    // Interface used for modifying values in a SharedPreferences object. All changes you make in an
    // editor are batched. Particularly used for helping save the theme the user has picked in the
    // spinner. Basically, if the user looks at the spinner again, they'll see their last choice
    // first.
    private SharedPreferences.Editor mMyEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button confirm = findViewById(R.id.button_settings_confirm);
        confirm.setOnClickListener(this);

        // The codes below this line is used to help setup the spinner for colour theme.
        themesSpinner = findViewById(R.id.spinner_settings_theme);
        themesArray = getResources().getStringArray(R.array.themes);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themesArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themesSpinner.setAdapter(arrayAdapter);
        themesSpinner.setOnItemSelectedListener(this);

        // The codes below this line is used to help saved the last choice the user picked for the
        // colour theme of this app; So that if they were to look at the spinner again they'll see
        // their previous choice first.
        mMyPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mMyEdit = mMyPrefs.edit();
        int selectedPosition = mMyPrefs.getInt("selected_position", 0) ;
        themesSpinner.setSelection(selectedPosition);

        // The codes below this line is use to help setup this activity as a pop-up.
        getActionBar().hide();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int)(height*.32));

        // The codes below this line is use to help setup/manage the background music/sound effects
        // of this app. Specifically the seekBar that controls the volume of this app.
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SeekBar seekBarVolume =findViewById(R.id.seekBar_settings_volume);
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedTheme = adapterView.getItemAtPosition(i).toString();
        mMyEdit.putInt("selected_position", themesSpinner.getSelectedItemPosition());
        mMyEdit.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onClick(View view) {
        if (selectedTheme != null) {
            Intent intent = new Intent();
            intent.putExtra("theme", selectedTheme);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}