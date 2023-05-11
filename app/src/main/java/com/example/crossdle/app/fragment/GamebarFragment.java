package com.example.crossdle.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.crossdle.R;
import com.example.crossdle.app.activity.MainActivity;
import com.example.crossdle.app.popup.HelpPopup;
/**
 * This is a Fragment that holds that will holds/represents the Gamebar of the Game Activity.
 */
public class GamebarFragment extends Fragment {
    public GamebarFragment() {}
    //An ImageView that is a reference to the info image.
    ImageView infoImage;
    //An ImageView that is a reference to the back-button image.
    ImageView backButtonImage;

    public static GamebarFragment newInstance() {
        return new GamebarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamebar, container, false);
        infoImage = view.findViewById(R.id.gamebar_imageView_info);
        infoImage.setOnClickListener(v -> {
            Intent intent = new Intent(GamebarFragment.this.getActivity(), HelpPopup.class);
            startActivity(intent);
        });
        backButtonImage = view.findViewById(R.id.gamebar_imageView_back_button);
        backButtonImage.setOnClickListener(v -> {
            Intent intent = new Intent(GamebarFragment.this.getActivity(), MainActivity.class);
            startActivity(intent);
        });
        return view;
    }
}