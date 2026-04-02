package com.khabar.saree.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.khabar.saree.R;

public class Splash extends AppCompatActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);

        final Handler handler = new Handler();
        int SPLASH_DISPLAY_LENGTH = 1000;
        handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                    handler.removeCallbacks(this);
                }
            }, SPLASH_DISPLAY_LENGTH);
    }
}