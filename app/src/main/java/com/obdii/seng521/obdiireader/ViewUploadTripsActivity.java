package com.obdii.seng521.obdiireader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ViewUploadTripsActivity extends AppCompatActivity {
    protected ArrayList<String> listItems = new ArrayList<String>();
    protected ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_upload_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ListView fileListView = (ListView) findViewById(R.id.fileList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entry = ((TextView) view).getText().toString();
                String f = getApplicationContext().getFilesDir() + "/" + entry.split("\n")[0];

                Intent intent = new Intent(ViewUploadTripsActivity.this, ReadLogActivity.class);
                intent.putExtra("f", f);
                startActivity(intent);
            }
        });

        File f = new File(getApplicationContext().getFilesDir().toString());
        File files[] = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filePath = files[i].getPath();
            if (filePath.endsWith(".log")) {
                adapter.add(
                        filePath.split("/")[6]
                        + "\nUploaded: No ("
                        + String.format("%.2f", (double) files[i].length()/1024) + " kB)"
                );
            }
        }

        f = new File(getApplicationContext().getFilesDir().toString() + "/uploaded");
        if (!f.exists()) {
            f.mkdirs();
        } else {
            files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                String filePath = files[i].getPath();
                if (filePath.endsWith(".log")) {
                    adapter.add(
                            "uploaded/" + filePath.split("/")[7]
                            + "\nUploaded: Yes ("
                            + String.format("%.2f", (double) files[i].length() / 1024) + " kB)"
                    );
                }
            }
        }

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadAllFiles();
            }
        });
    }

    private void uploadAllFiles() {
        File f = new File(getApplicationContext().getFilesDir().toString());
        File files[] = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filePath = files[i].getPath().split("/")[6];
            if (filePath.endsWith(".log")) {
                uploadFile(filePath);
                moveFile(filePath);
            }
        }
    }

    private void uploadFile(String fileName) {

    }

    private void moveFile(String fileName) {
        InputStream in = null;
        OutputStream out = null;
        String path = getApplicationContext().getFilesDir().toString();
        try {
            in = new FileInputStream(path + "/" + fileName);
            out = new FileOutputStream(path + "/uploaded/" + fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(path + "/" + fileName).delete();
        }

        catch (FileNotFoundException e) {
            // TODO
        }
        catch (Exception e) {
            // TODO
        }

    }
}
