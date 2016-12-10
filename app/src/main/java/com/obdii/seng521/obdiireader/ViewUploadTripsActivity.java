package com.obdii.seng521.obdiireader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import static java.net.HttpURLConnection.HTTP_OK;

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
                uploadAllFiles();
                finish();
                startActivity(getIntent());
            }
        });
    }

    protected class SendPostRequest extends AsyncTask<String, Void, String> {
        private final String HOST = "http://68.147.200.5/";
        private String ADDRESS;

        @Override
        protected String doInBackground(String... params) {
            try {
                String vid = params[0].split(" ")[0];
                String time = "'" + params[0].split(" ")[1].replace("_", " ") + "'";
                String urlParams = "vid=" + vid + "&time=" + time;
                URL url = new URL(HOST + "webserver/uploadNewTrip.php");
                String tripID = sendRequest(url, "POST", "application/x-www-form-urlencoded", urlParams);
                if (tripID != null) {
                    File f = new File(params[0]);
                    BufferedReader br = null;
                    try {
                        url = new URL(HOST + "webserver/updateTripLeg.php");
                        br = new BufferedReader(new FileReader(f));
                        String line;
                        urlParams = "tripID=" + tripID;

                        NumberFormat formatter = new DecimalFormat("###.#");
                        // first line is vid
                        line = br.readLine();
                        if ((line = br.readLine()) != null) {
                            while (true) {
                                urlParams += "&time='" + line + "'";
                                line = br.readLine();
                                urlParams += "&xloc=" + line.split(" ")[1];
                                line = br.readLine();
                                urlParams += "&yloc=" + line.split(" ")[1];
                                line = br.readLine();
                                urlParams += "&speed=" + formatter.format(line.split(" ")[1]);
                                sendRequest(url, "POST", "application/x-www-form-urlencoded", urlParams);
                                urlParams = "tripID=" + tripID;
                            }
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
                } else {
                    return "bad response";
                }
            } catch (Exception e) {
                // TODO
            }
            return null;
        }

        private String sendRequest(URL url, String method, String type, String params) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            try {
                // open conn
                conn = (HttpURLConnection) url.openConnection();
                // POST or GET
                conn.setRequestMethod(method);
                // Content-type
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
                conn.setRequestProperty("Content-Language", "en-US");

                // send request
                conn.setDoOutput(true);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(params);
                dos.flush();
            } catch (Exception e) {

            } finally {
                try {
                    dos.close();
                } catch (Exception e) {

                }
            }
            String response = null;
            if (conn != null) {
                BufferedReader br = null;
                StringBuffer sb = null;
                try {
                    // get response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HTTP_OK) {
                        // get response
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        sb = new StringBuffer();
                        String temp;
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                        response = sb.toString();
                    }
                } catch (Exception e) {

                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {

                    }
                }
            }
            return response;
        }
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
        new SendPostRequest().execute(fileName);
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
