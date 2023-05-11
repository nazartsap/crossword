package com.example.crossdle.app.popup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.example.crossdle.R;

/**
 * This class contains methods/variables that support the help/information popup.
 */
public class HelpPopup extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getActionBar().hide();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int)(height*.39));
        Button understandButton = findViewById(R.id.button_help_understood);
        understandButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}