package com.fieldbook.tracker.Speech;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by zrm22 on 8/28/15.
 */
public class TextToSpeechFactory implements TextToSpeech.OnInitListener  {

    protected static final int REQUEST_OK = 1;

    private TextToSpeech tts;

    public TextToSpeechFactory(Activity parent) {
        tts = new TextToSpeech(parent,this);
    }


    @Override
    public void onInit(int status) {
        if (status==TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
        } else {
            tts = null;
        }
    }

    public TextToSpeech getTTS() {
        return tts;
    }
}
