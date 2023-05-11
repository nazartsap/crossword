package com.example.crossdle.bank;
import android.content.Context;
import com.example.crossdle.app.FileHandler;
import com.google.gson.annotations.SerializedName;
import java.util.Random;

/**
 * Holds word base data that is used to generate boards using words of various lengths.
 */
public class WordBase {
    private static final String FILE_NAME_FOUR = "word_base_four";
    private static final String FILE_NAME_FIVE = "word_base_five";

    public static WordBase cached;

    private WordList fourLetterWords;
    private WordList fiveLetterWords;

    private WordBase(WordList fourLetterWords, WordList fiveLetterWords) {
        this.fourLetterWords = fourLetterWords;
        this.fiveLetterWords = fiveLetterWords;
    }

    public String getFourLetterRandom() {
        Random rand = new Random();
        int randomWord = rand.nextInt(fourLetterWords.size());
        return fourLetterWords.get(randomWord);
    }

    public String getFiveLetterRandom() {
        Random rand = new Random();
        int randomWord = rand.nextInt(fiveLetterWords.size());
        return fiveLetterWords.get(randomWord);
    }

    public static WordBase load(Context context) {
        WordList fourLetterWords = FileHandler.openRawJson(context, FILE_NAME_FOUR, WordList.class);
        WordList fiveLetterWords = FileHandler.openRawJson(context, FILE_NAME_FIVE, WordList.class);
        cached = new WordBase(fourLetterWords, fiveLetterWords);
        return cached;
    }
}
