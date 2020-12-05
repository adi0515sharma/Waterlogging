package com.example.myapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class poi_display extends AppCompatActivity {

    ProgressBar progressBar;
    ListView listView;
    Toolbar toolbar;
    ArrayList<poi_storage> poiList=new ArrayList<>();
    int img;
    static String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_display);


        progressBar=findViewById(R.id.poi_loader);
        toolbar=findViewById(R.id.poi_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView=findViewById(R.id.poi_list);
        toolbar.setSubtitle(getIntent().getStringExtra("url").toString().toUpperCase());
        switch (getIntent().getStringExtra("url").toString())
        {
            case "atm":
                img=R.drawable.atm;
                break;
            case "petrol":
                img=R.drawable.gas_station;
                break;
            case "grocery":
                img=R.drawable.grocery;
                break;
            case "parking":
                img=R.drawable.parking;
                break;
            case "hotel":
                img=R.drawable.bed;
                break;
            case "hospital":
                img=R.drawable.hospital;
                break;

        }
        url="https://places.api.here.com/places/v1/autosuggest?app_id=qFQ6QZBSwd9Yn0OsuiF2&app_code=94AbAg_F6UFJkLZh5d_r5Q&at="+getIntent().getStringExtra("lat").toString()+","+getIntent().getStringExtra("lng").toString()+"&q=";
        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet(url+getIntent().getStringExtra("url"));
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObj = new JSONObject(result);
                JSONArray results=jsonObj.getJSONArray("results");
                for(int i=0;i<results.length();i++)
                {
                    JSONObject jsonObject=results.getJSONObject(i);

                    if(jsonObject.has("position"))
                    {
                        poi_storage pstorage=new poi_storage();
                        pstorage.setTitle(jsonObject.getString("title").toString());
                        Geocoder gc = new Geocoder(poi_display.this);
                        List<Address> list = null;
                        try {

                            String position=jsonObject.getJSONArray("position").toString().replace("[","");
                            String position1=position.replace("]","");
                            String lat,lng;
                            lat=position1.split(",")[0];
                            lng=position1.split(",")[1];
                            list = gc.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng),1);
                            pstorage.setPosition(list.get(0).getAddressLine(0));
                            }
                        catch(Exception e)
                        {
                            pstorage.setPosition("No Address Found");
                        }

                        pstorage.setDistance("Distance : "+jsonObject.getString("distance").toString());
                        pstorage.url=img;
                        poiList.add(pstorage);
                    }
                }

                if(poiList.size()>0)
                {
                    poi_adapter myAdapter=new poi_adapter(poi_display.this,R.layout.poi_view,poiList);
                    listView.setAdapter(myAdapter);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
            catch (java.lang.NullPointerException e)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(poi_display.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Please Turn On Wifi or Mobile Data")
                        .setCancelable(false)

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                                Toast.makeText(poi_display.this,"Please Turn On Wifi or Mobile Data",
                                        Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("No Internet Access");
                alert.show();
            }
            catch (JSONException e) {

                Toast.makeText(poi_display.this,"Weak Internet Network",Toast.LENGTH_LONG).show();
            }

        }
    }



    public class poi_adapter extends ArrayAdapter<poi_storage> {


        ArrayList<poi_storage> poi = new ArrayList<>();

        public poi_adapter(Context context, int textViewResourceId, ArrayList<poi_storage> objects) {
            super(context, textViewResourceId, objects);
            poi = objects;

        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.poi_view, null);
            final TextView title,address;
            title=v.findViewById(R.id.title_poi);
            address=v.findViewById(R.id.address_poi);
            ImageView poi_image=v.findViewById(R.id.poi_image);
            Button direction=v.findViewById(R.id.direction);
            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = address.getText().toString();
                   // Intent intent = new Intent();
                  //  intent.putExtra("MyData", data);
                   // setResult(400, intent);
                    Intent intent = new Intent();
                    intent.putExtra("address", data);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });


            poi_image.setImageResource(poi.get(position).getUrl());
            title.setText(poi.get(position).getTitle());



            address.setText(poi.get(position).getPosition());


            return v;

        }
    }


}
