package com.example.myapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androdocs.httprequest.HttpRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsManager;

import org.json.JSONException;
import org.json.JSONObject;

public class weather1 extends AppCompatActivity {

    String API = "c903de17dc9cb2e5e6a68466afa5c14d";

    String url;
    TextView addressTxt, tempTxt, temp_minTxt, temp_maxTxt,windTxt, pressureTxt, humidityTxt,reload,condition,windDegree;
    SwipeRefreshLayout progressBar;
    LinearLayout relativeLayout;

    LinearLayout maincontainer;
    private FusedLocationProviderClient fusedLocationClient;
    // private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    //private HomeFragment.MainActivityLocationCallback callback = new HomeFragment.MainActivityLocationCallback(this);
    //private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    static private Location location;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    Toolbar toolbar;
    ImageView info;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather1);
        bundle=getIntent().getExtras();
        addressTxt = (TextView)findViewById(R.id.address);
        toolbar=findViewById(R.id.weather_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        maincontainer=findViewById(R.id.mainContainer);
        tempTxt = (TextView)findViewById(R.id.temp);
        temp_minTxt =(TextView)findViewById(R.id.temp_min);
        temp_maxTxt = (TextView)findViewById(R.id.temp_max);
        reload = (TextView)findViewById(R.id.Condition);
        info=findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog dialog;
                androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(weather1.this);
                final View mView=getLayoutInflater().inflate(R.layout.campas,null);
                ImageView cancel_campas=(ImageView)mView.findViewById(R.id.cancel_campas);

                builder.setView(mView);
                dialog=builder.create();
                cancel_campas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        windTxt = (TextView)findViewById(R.id.wind);
        windDegree=(TextView)findViewById(R.id.windDegree);
        pressureTxt = (TextView)findViewById(R.id.pressure);
        humidityTxt = (TextView)findViewById(R.id.humidity);
        progressBar= (SwipeRefreshLayout) findViewById(R.id.referesh);
        progressBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                permission_checker();
            }
        });

        relativeLayout=(LinearLayout)findViewById(R.id.mainContainer);
        permission_checker();

    }
//19.002738, 72.841113


    void permission_checker()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(weather1.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(weather1.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(weather1.this)
                        .setTitle("Required Location Permission")
                        .setMessage("You Have To Give The Location Permission To Access Weather Forecast Of Current Location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(weather1.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(weather1.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            fusedLocationClient= LocationServices.getFusedLocationProviderClient(weather1.this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(weather1.this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {

                            if(location!=null) {
                                weather1.location=location;
                                try{
                                    url="https://api.openweathermap.org/data/2.5/weather?lat=" +location.getLatitude()+"&lon="+location.getLongitude()+"&units=metric&appid=" + API;
                                    Task();
                                }
                                catch(Exception e) {
                                    // Toast.makeText(getActivity(),"Sorry Can't Find The Weather Info Of Current Location",Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {

                                //enableLocationComponent();
                            }
                        }


                    });
        }


    }

    public void Task(){
        new weather1.weatherTask().execute();
    }


    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setRefreshing(true);
            maincontainer.setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet(url);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");

                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: \n" + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: \n" + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");
                String windSpeed = wind.getString("speed");
                String winddeg = wind.getString("deg");
                String weatherDescription = weather.getString("description");
                String address = bundle.get("area").toString();
                reload.setText(weather.getString("main"));
                addressTxt.setText(address);
                toolbar.setSubtitle(weatherDescription);
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                windTxt.setText("Speed : \n"+windSpeed+" km/h");
                int direc=Integer.parseInt(winddeg);

                windDegree.setText("Direction : \n"+direction(direc%360));
                pressureTxt.setText(pressure+"mb");
                humidityTxt.setText(humidity+"%");
                progressBar.setRefreshing(false);
                maincontainer.setVisibility(View.VISIBLE);
            }
            catch (JSONException e) {
                progressBar.setRefreshing(false);
                Toast.makeText(weather1.this,"Weak Internet Network",Toast.LENGTH_LONG).show();
            }
            catch (java.lang.NullPointerException e)
            {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(weather1.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Please Turn On Wifi or Mobile Data")
                        .setCancelable(false)

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(weather1.this,"Please Turn On Wifi or Mobile Data",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                android.app.AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("No Internet Access");
                alert.show();
            }

        }
    }

    String direction(int dir)
    {
       if(dir<11.25)
       {
            return "N";
       }
       else if(11.25<=dir && dir<33.75)
       {
            return "NNE";
       }
       else if(33.75<=dir && dir<56.25)
       {
            return "NE";
       }
       else if(56.25<=dir && dir<78.75)
       {
           return "ENE";
       }
       else if(78.75<=dir && dir<101.25)
       {
           return "E";
       }
       else if(101.25<=dir && dir<123.75)
       {
           return "ESE";
       }
       else if(123.75<=dir && dir<146.25)
       {
           return "SE";
       }
       else if(146.25<=dir && dir<168.75)
       {
           return "SSE";
       }
       else if(168.75<=dir && dir<191.25)
       {
           return "S";
       }
       else if(191.25<=dir && dir<213.75)
       {
           return "SSW";
       }
       else if(213.75<=dir && dir<236.25)
       {
           return "SW";
       }
       else if(236.25<=dir && dir<258.75)
       {
           return "WSW";
       }
       else if(258.75<=dir && dir<281.25)
       {
           return "W";
       }
       else if(281.25<=dir && dir<303.75)
       {
           return "WNW";
       }
       else if(303.75<=dir && dir<326.25)
       {
           return "NW";
       }
       else if(326.25<=dir && dir<348.75)
       {
           return "NNW";
       }
       return null;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
