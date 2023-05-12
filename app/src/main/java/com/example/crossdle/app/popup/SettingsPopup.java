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


public class SettingsPopup extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{


    AudioManager audioManager;

    int currentVolume;


    Spinner themesSpinner;


    String[] themesArray;

    String selectedTheme;


    private SharedPreferences mMyPrefs;


    private SharedPreferences.Editor mMyEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button confirm = findViewById(R.id.button_settings_confirm);
        confirm.setOnClickListener(this);


        themesSpinner = findViewById(R.id.spinner_settings_theme);
        themesArray = getResources().getStringArray(R.array.themes);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themesArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themesSpinner.setAdapter(arrayAdapter);
        themesSpinner.setOnItemSelectedListener(this);


        mMyPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mMyEdit = mMyPrefs.edit();
        int selectedPosition = mMyPrefs.getInt("selected_position", 0) ;
        themesSpinner.setSelection(selectedPosition);


        getActionBar().hide();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int)(height*.32));


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