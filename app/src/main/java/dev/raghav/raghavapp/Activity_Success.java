package dev.raghav.raghavapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Activity_Success extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView t2 = (TextView) findViewById(R.id.link_tv);
        t2.setText(Html.fromHtml("<a href=\"https://www.infocentroid.com\">Developed by InfoCentroid</a>"));
        t2.setMovementMethod(LinkMovementMethod.getInstance());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Activity_Success.this,MainLocation.class);
        startActivity(intent);

        finish();
    }


    //**************congress app
}
