package com.omegaki113r.pksoftwaresolutions.shalintha.translator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.emiliadev.android.speech.R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, Main2Activity.class);
                    startActivity(mainIntent);
                }
            }
        };
        thread.start();



    }
}
