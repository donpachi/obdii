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

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://68.147.199.144/webserver/uploadNewTrip.php");
                String tripID = sendRequest(url, "POST", "vid", "85335", "time", params[0].split(".")[0].split("/")[-1].replace("_", " "));
                if (tripID != null) {
                    File f = new File(params[0]);
                    BufferedReader br = null;
                    try {
                        url = new URL("http://68.147.199.144/webserver/updateTripLeg.php");
                        br = new BufferedReader(new FileReader(f));
                        String line;
                        String tripParams[] = new String[10];
                        tripParams[0] = "tripID";
                        tripParams[1] = tripID;
                        tripParams[2] = "time";
                        tripParams[4] = "xloc";
                        tripParams[6] = "yloc";
                        tripParams[8] = "speed";


                        NumberFormat formatter = new DecimalFormat("###.#");
                        line = br.readLine();
                        if (line != null) {
                            while (true) {
                                tripParams[3] = line;
                                line = br.readLine();
                                tripParams[5] = line.split(" ")[1];
                                line = br.readLine();
                                tripParams[7] = line.split(" ")[1];
                                line = br.readLine();
                                tripParams[9] = formatter.format(line.split(" ")[1]);
                                sendRequest(url, "POST", tripParams);
                                line = br.readLine();
                                if (line == null) {
                                    break;
                                }
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
                    return new String("bad response");
                }
            } catch (Exception e) {
                // TODO
            }
            return null;
        }

        private String sendRequest(URL url, String type, String... params) {
            if (params.length % 2 == 1 || params.length == 0) {
                return null;
            }

            BufferedWriter bw;
            OutputStream os;
            HttpURLConnection conn;
            BufferedReader br;
            try {
                JSONObject postDataParams = new JSONObject();
                for (int i = 0; i < params.length; i = i++) {
                    postDataParams.put(params[i++], params[i]);
                }
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(type);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                os = conn.getOutputStream();
                bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(getPostDataString(postDataParams));
                bw.flush();

                bw.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    return sb.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                // TODO
                return null;
            }
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

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
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
