package com.obdii.seng521.obdiireader;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Math.*;

public class TripRecordActivity extends AppCompatActivity {
    // Android 6.0 Permissions
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+1;

    // Class to store GPS coordinates
    protected class GPSCoord {
        protected double lat;
        protected double lon;
        protected long time;

        protected void setCoord(Location l) {
            if (l != null) {
                lat = l.getLatitude();
                lon = l.getLongitude();
                time = System.currentTimeMillis();
            }
        }

        protected void setCoord(GPSCoord g) {
            lat = g.getLat();
            lon = g.getLon();
            time = g.getTime();
        }

        protected double getLat() {
            return lat;
        }

        protected double getLon() {
            return lon;
        }

        protected long getTime() {
            return time;
        }
    }

    // textbox
    protected TextView timerTextView;

    // location objects
    protected LocationManager lm;
    protected Location loc;
    protected GPSCoord lastPoint = new GPSCoord();
    protected GPSCoord newPoint = new GPSCoord();

    // file objects
    protected OutputStreamWriter stream;

    // timer objects
    protected Handler timerHandler = new Handler();
    protected Runnable timerRunnable = new Runnable() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar;

        @Override
        public void run() {
            calendar = Calendar.getInstance();

            updateLocations();

            String s = sdf.format(calendar.getTime())
                    + "\n  Longitude: " + newPoint.getLon()
                    + "\n  Latitude: " + newPoint.getLat()
                    + "\n  Speed: " + calculateSpeed(newPoint, lastPoint)
                    + " km/h\n";

            try {
                stream.write(s);
            } catch (IOException e) {
                // TODO
            }
            timerTextView.append(s);

            lastPoint.setCoord(newPoint);

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        timerTextView = (TextView) findViewById(R.id.tripLog);
        timerTextView.setSingleLine(false);
        timerTextView.setMovementMethod(new ScrollingMovementMethod());

        Button startStopButton = (Button) findViewById(R.id.startStopButton);
        startStopButton.setText("start trip");
        startStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop trip")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start trip");
                    timerTextView.append("-EOF-\n\n");
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // TODO
                    }
                } else {
                    if (!canAccessLocation()) {
                        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();
                        String fileName = sdf.format(calendar.getTime()) + ".log";
                        File file = new File(getApplicationContext().getFilesDir(), fileName);

                        try {
//                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            stream = new OutputStreamWriter(getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE));
                        } catch (FileNotFoundException e) {
                            // TODO
                        } catch (IOException e) {
                            // TODO
                        }

                        timerHandler.postDelayed(timerRunnable, 0);
                        b.setText("stop trip");
                    }
                }
            }
        });
    }

    private void updateLocations() {
        try {
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria c = new Criteria();
            String bestProvider = String.valueOf(lm.getBestProvider(c, true));

            lm.requestLocationUpdates(bestProvider, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        lm.removeUpdates(this);
                    } catch (SecurityException e) {
                        // TODO: catch security exception
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
            loc = lm.getLastKnownLocation(bestProvider);
            newPoint.setCoord(loc);
        } catch (SecurityException e) {
            // TODO: catch security exception
        }
    }

    private double calculateSpeed(GPSCoord x, GPSCoord y) {
        if (x == null || y == null) {
            return 0.0;
        }
        double latx = x.getLat() * PI / 180;
        double lonx = x.getLon() * PI / 180;
        double laty = y.getLat() * PI / 180;
        double lony = y.getLon() * PI / 180;

        final double r = 6378100;

        double rho1 = r * cos(latx);
        double z1 = r * sin(latx);
        double x1 = rho1 * cos(lonx);
        double y1 = rho1 * sin(lonx);

        double rho2 = r * cos(laty);
        double z2 = r * sin(laty);
        double x2 = rho2 * cos(lony);
        double y2 = rho2 * sin(lony);

        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (r * r);
        double theta = acos(cos_theta);
        double dist = r * theta * 1.60934;

        long time = (x.getTime() - y.getTime()) / 1000;

        return (dist / time) / 3600;
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}
