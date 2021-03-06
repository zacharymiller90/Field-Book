package com.fieldbook.tracker.Tutorial;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fieldbook.tracker.ConfigActivity;
import com.fieldbook.tracker.R;

import java.util.Locale;

public class TutorialSettingsActivity extends Activity {
    public static Activity thisActivity;

    private int screen;

    private final int max = 8;

    @Override
    public void onDestroy() {
        ConfigActivity.helpActive = false;
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigActivity.helpActive = true;

        SharedPreferences ep = getSharedPreferences("Settings", 0);

        // This allows dynamic language change without exiting the app
        String local = ep.getString("language", "en");
        String region = ep.getString("region", "");
        Locale locale2 = new Locale(local, region);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources()
                .getDisplayMetrics());

        thisActivity = this;

        // Makes the screen a system alert, so it can "float" above other screens
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(R.layout.tutorial);

        Button close = (Button) findViewById(R.id.close);
        Button prev = (Button) findViewById(R.id.prev);
        Button next = (Button) findViewById(R.id.next);

        final TextView header = (TextView) findViewById(R.id.header);
        final TextView content = (TextView) findViewById(R.id.text2);

        screen = 1;

        // Load help strings        
        final String array[] = new String[max];
        array[0] = getString(R.string.shelp1);
        array[1] = getString(R.string.shelp2);
        array[2] = getString(R.string.shelp3);
        array[3] = getString(R.string.shelp4);
        array[4] = getString(R.string.shelp5);
        array[5] = getString(R.string.shelp6);
        array[6] = getString(R.string.shelp7);
        array[7] = getString(R.string.shelp8);

        header.setText(getString(R.string.tutorial) + " " + screen + "/" + max);
        content.setText(array[screen - 1]);

        // move one step back in the tutorial        
        prev.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                screen -= 1;

                if (screen < 1)
                    screen = 1;

                header.setText(getString(R.string.tutorial) + " " + screen + "/" + max);
                content.setText(array[screen - 1]);
            }
        });

        // move one step forward in the tutorial        
        next.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                screen += 1;

                if (screen > max)
                    screen = max;

                header.setText(getString(R.string.tutorial) + " " + screen + "/" + max);
                content.setText(array[screen - 1]);
            }
        });

        // close screen
        // help active is to indicate tips/hints is no longer open
        // user is now able to open tutorial        
        close.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ConfigActivity.helpActive = false;
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
