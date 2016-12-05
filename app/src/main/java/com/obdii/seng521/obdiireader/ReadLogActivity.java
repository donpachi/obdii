package com.obdii.seng521.obdiireader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_log);

        Intent i = getIntent();
        File f = new File(i.getExtras().getString("f"));
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            // TODO
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // TODO
            }
        }

        TextView logTextView = (TextView) findViewById(R.id.logFile);
        logTextView.setSingleLine(false);
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        logTextView.setText(text);
    }
}
