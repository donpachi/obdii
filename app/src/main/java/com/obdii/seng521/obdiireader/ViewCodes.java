package com.obdii.seng521.obdiireader;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tai on 2016-12-04.
 */

public class ViewCodes extends Activity {
    // Android 6.0 Permissions
    private static final String[] INITIAL_PERMS={
            Manifest.permission.INTERNET
    };
    private static final int INITIAL_REQUEST=1337;
    private static final int INTERNET_REQUEST=INITIAL_REQUEST+2;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");

        setContentView(R.layout.view_codes);
        TextView myTextView = (TextView)findViewById(R.id.vehicleIDNum);
        myTextView.setText("Vehicle ID: " + id);

        new SendPostRequest().execute(id);
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg) {
            try{
                if (!canAccessInternet()) {
                    requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                }
                String urlstring = "http://68.147.199.144/webserver/queryOBD.php?id=" + arg[0];
                URL url = new URL(urlstring);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();

                //System.out.println(response.toString());

                return response.toString();

            } catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                String rpm = jArray.getJSONObject(0).getString("RPM");
                String fuel = jArray.getJSONObject(0).getString("fuelcode");
                String seatbelt = jArray.getJSONObject(0).getString("seatbeltcode");
                String abs = jArray.getJSONObject(0).getString("abscode");

                TextView rpmText = (TextView)findViewById(R.id.RPM);
                rpmText.setText("RPM: " + rpm);

                TextView fuelText = (TextView)findViewById(R.id.fuelcode);
                fuelText.setText("Fuel Code: " + fuel);

                TextView seatbeltText = (TextView)findViewById(R.id.sbcode);
                seatbeltText.setText("Seat Belt Code: " + seatbelt);

                TextView abscodeText = (TextView)findViewById(R.id.abscode);
                abscodeText.setText("ABS Code: " + abs);



            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private boolean canAccessInternet() {
        return(hasPermission(Manifest.permission.INTERNET));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}

