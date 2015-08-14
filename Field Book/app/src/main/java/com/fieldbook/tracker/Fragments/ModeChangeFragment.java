package com.fieldbook.tracker.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.fieldbook.tracker.R;


/**
 * Created by zrm22 on 8/5/15.
 */
public class ModeChangeFragment extends Fragment {

    ModeSwapListener modeListener;

    LinearLayout modeFragment;
    SeekBar modeSeekBar;

    public ModeChangeFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("OnCreateView");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("OnCreateView");
        View view = inflater.inflate(R.layout.mode_fragment, container, false);
        System.out.println("Inflated");
        modeFragment = (LinearLayout) view.findViewById(R.id.modeFragmentLayout);
        modeSeekBar = (SeekBar) modeFragment.findViewById(R.id.seekBar);
        modeSeekBar.setProgress(0);
        modeSeekBar.setMax(100);
        modeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int currentProgress;
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(currentProgress>50) {
                    modeSeekBar.setProgress(100);
                    modeListener.modeSwap(100);
                }
                else {
                    modeSeekBar.setProgress(0);
                    modeListener.modeSwap(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                currentProgress = progress;

            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            modeListener = (ModeSwapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ModeSwapListener");
        }
    }

    //Method to allow the MainActivity to set the progress based on current preferences
    public void setProgress(int progress) {
        modeSeekBar.setProgress(progress);
    }

    //Interface for handling state changes
    public interface ModeSwapListener {
        public void modeSwap(int progress);
    }
}
