package dev.raghav.raghavapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;

public class Splash1Screen extends AppCompatActivity {
        //SessionManager manager;

        // Splash screen timer
        private static int SPLASH_TIME_OUT = 2000;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
           // manager =new SessionManager(dev.raghav.raghavapp.SplashActivity.this);

            setContentView(R.layout.splash1screen);
            TextView t2 = (TextView) findViewById(R.id.link_tv);
            t2.setText(Html.fromHtml("<a href=\"https://www.infocentroid.com\">Developed by InfoCentroid</a>"));
            t2.setMovementMethod(LinkMovementMethod.getInstance());


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    try{

                        Intent intent = new Intent(Splash1Screen.this, Splash2Screen.class);
                        startActivity(intent);
                     finish();

                    }catch (Exception e) {
                    }
//                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(i);

                    // close this activity
//                    finish();
                }
            }, SPLASH_TIME_OUT);




        }
    }



