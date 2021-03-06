package com.safedeliver;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import android.view.*;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.os.StrictMode;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class FloatingButtonService extends Service implements LocationListener {

    private View floatingBubbleView;
    private View numPad;
    private WindowManager windowManager;
    private static int Lati;
    private static int Lngi;
    private static int Acc;
    private static String uId;
    private static final String FILE_NAME = "uId.txt";
    private LocationManager locationManager;
    private String provider;
    private static Boolean LongClicked = false;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    double latitude,longitude;

    public static void req_api(String eP) throws Exception {
        String url = "http://safedeliver.herokuapp.com/" + eP;
        Log.i("url", "value: " + url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            Log.i("get req", "Sending 'GET' request to URL : " + url);
            Log.i("req code", "Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            //print in String
            Log.i("response", "resString: " + response.toString());
            
            // Read JSON response and print
            JSONObject myResponse = new JSONObject(response.toString());
            Log.i("result of parse", "result after Reading JSON Response");
            // Log.i("id", "id: " +myResponse.getString("id"));
            
            Log.i("id", "id: " +myResponse.getString("alarmId"));
        
            if(myResponse.getString("alarmId").length() > 1) {
                LongClicked = true;
            } else {
                LongClicked = false;
            }
        }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (startId == Service.START_STICKY) {
                handleStart();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * initiates the layout params and defines a onTouch listener to drag it on user interaction.
     * */
    private void handleStart() {
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        floatingBubbleView = LayoutInflater.from(this).inflate(R.layout.floating_bubble_layout, null);
        handleFloatingBubble();
        minimizeApp();
    }

    //api call 
    private void createAlarm() {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            String urlEP = "alarm/create/" + uId + "/" + Lati + "/" + Lngi + "/" + Acc;
            FloatingButtonService.req_api(urlEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //checks if there was already an alarm created
    private void checkAlarmReq() {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            String urlEP = "api/users/" + uId;
            FloatingButtonService.req_api(urlEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    //reopen the app
    private void startApp(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        PackageManager managerclock = getPackageManager();
        i = managerclock.getLaunchIntentForPackage("com.safedeliver");
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);
    }
    private void numPad() {
        Intent myIntent = new Intent(getApplicationContext(), NumberPadActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
    }
    private void location() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        Location location = locationManager.getLastKnownLocation(provider);
        
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("gpsenabled", "value: " + isGPSEnabled);
        Log.i("netenabled", "value: " + isNetworkEnabled);


       if (isNetworkEnabled){
            Log.i("insidenet", "value: " + isNetworkEnabled);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, this);

            if (location != null){
                // location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Lati = (int) (location.getLatitude());
                    Lngi = (int) (location.getLongitude());
                    Acc = (int) (location.getAccuracy());
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, this);
            }
        }

        else if (isGPSEnabled){
                
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Lati = (int) (location.getLatitude());
                Lngi = (int) (location.getLongitude());
                Acc = (int) (location.getAccuracy());
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                }
        } 

        else {
            Log.i("no location", "none");
        }
    }
    public void load() {
        FileInputStream fis = null;
 
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
 
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            Log.i("loaded text", "value: " + sb);
            uId = sb.toString().replaceAll("\\s+", "");
            fis.close();
        } catch (Exception e) {
            Log.i("error", "no load text");
        }
    }
    private void handleFloatingBubble() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 0;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingBubbleView, params);
    
        Button btn = (Button) floatingBubbleView.findViewById(R.id.btnDoMagic);
        btn.setBackgroundColor(R.color.transparent);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        createAlarm(); 
                    }
                };
                if(!LongClicked) {
                    location();
                    createAlarm();
                    Toast.makeText(getApplicationContext(),
                        "Alarm Created",
                        Toast.LENGTH_SHORT).show();
                }
                    startApp();
                    numPad();
                    stopSelf();
            }
        });    

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("Long Click", "Longclick!");
                location();
                createAlarm(); 
                Toast.makeText(getApplicationContext(),
                        "Alarm Created",
                        Toast.LENGTH_SHORT).show();
                checkAlarmReq();
                return false;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        Lati = (int) (location.getLatitude());
        Lngi = (int) (location.getLongitude());
        Acc = (int) (location.getAccuracy());
    }

    //need bottom 3
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        load();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingBubbleView != null) windowManager.removeView(floatingBubbleView);
    }
}