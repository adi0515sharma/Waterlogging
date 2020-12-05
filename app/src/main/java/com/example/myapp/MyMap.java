package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;



public class MyMap extends AppCompatActivity implements MapboxMap.OnMarkerClickListener,OnMapReadyCallback, MapboxMap.OnMapClickListener,PermissionsListener, MaterialSearchBar.OnSearchActionListener, OnNavigationReadyCallback {

    private NotificationManagerCompat notificationManager;
    int onNewRoute=0;
    private MapView mapView;
    static private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private Point destination_latlng;
    private NavigationMapRoute navigationMapRoute;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private LocationEngine locationEngine;
    private Marker[] marker=new Marker[10];
    String option= DirectionsCriteria.PROFILE_DRIVING;
    private MaterialSearchBar materialSearchBar;
    //private RelativeLayout relativeLayout;
    private static final int SPEECH_REQUEST_CODE = 0;
    private RadioGroup option_profile;
    private RadioButton R_button;
    private Button button,route;
    Drawer drawer;

    private TextView distance,time,place_search_add;
    ImageView atm,petrol,grocery,hospital,parking,hotel;

    private FloatingActionButton destination,origin,option_view;
    String[] location;
    private ImageView cancel;
    private FirebaseOptions options;
    private FirebaseDatabase secondaryDatabase;
    private DatabaseReference df;
    private LinearLayout prompat,option_3,other_option;
    private FirebaseApp app;
    MarkerOptions[] markers=new MarkerOptions[10];
    private int count;
    private Point destinationPoint,originPoint;
    @Nullable OnRouteSelectionChangeListener onRouteSelectionChangeListener;
    private String switch_item="";
    private double point_long,point_lat;
    private LinearLayout place_search,waterlogging_area;
    private List<MarkerData> markerDataList=new ArrayList<MarkerData>();
    private ProgressBar route_loader;
    ImageButton search_anble_disable,car,motor_bike,walking;
    boolean noNet=false;
    private TextView waterlogging_add,risk;
    // private ImageButton poi_on_off;
    // private LinearLayout poi;

    ImageView way;
    private LinearLayout appbar,distanceAndtime;
    SecondaryDrawerItem Map;
    String current_location,Destination;
    private String Distance,Time;

    //  private int fav_icon;
    //  ImageView ambulance,pub,resturant,grocery,atm,post,parking,petrol,medicine,hotel;
    //  private int AUTO_COMP_REQ_CODE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_my_map);
        Bundle bundle=getIntent().getExtras();
        Log.e("true",getIntent().getStringExtra("Email")+"");

        count=1;

        way=findViewById(R.id.way);
        prompat=(LinearLayout)findViewById(R.id.prompat);
        route_loader=findViewById(R.id.route_loader);
        route=(Button)findViewById(R.id.getRoute);
        distance=(TextView) findViewById(R.id.distance);
        time=(TextView) findViewById(R.id.time);
        cancel=(ImageView)findViewById(R.id.cancel);
        waterlogging_add=findViewById(R.id.waterlogging_add);
        risk=findViewById(R.id.risk);
        notificationManager=NotificationManagerCompat.from(this);
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        distanceAndtime=findViewById(R.id.distanceAndtime);
       // relativeLayout=(RelativeLayout)findViewById(R.id.relative);
        place_search_add=findViewById(R.id.place_search_add);
        origin=(FloatingActionButton)findViewById(R.id.current_location);
        search_anble_disable=findViewById(R.id.imageButton);
        option_3=findViewById(R.id.option);
        other_option=findViewById(R.id.other_option);
        place_search=findViewById(R.id.place_search);
        waterlogging_area=findViewById(R.id.waterlogging_area);
        appbar=findViewById(R.id.appbar);
        waterlogging_add.setMovementMethod(new ScrollingMovementMethod());
        place_search_add.setMovementMethod(new ScrollingMovementMethod());


        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                                      .withIdentifier(1)
                                       .withName("Weather").withBadge("").withIcon(R.drawable.rainy);
        Map = new SecondaryDrawerItem().withIdentifier(2).withName("Map").withIcon(R.drawable.map);
        SecondaryDrawerItem News = new SecondaryDrawerItem().withIdentifier(2).withName("News").withIcon(R.drawable.newspaper);
        SecondaryDrawerItem Call = new SecondaryDrawerItem().withIdentifier(2).withName("Call").withIcon(R.drawable.telephone);
        SecondaryDrawerItem Setting = new SecondaryDrawerItem().withIdentifier(2).withName("Setting").withIcon(R.drawable.gear);
        SecondaryDrawerItem Share = new SecondaryDrawerItem().withIdentifier(2).withName("Share Current Location").withIcon(R.drawable.pin);
        SecondaryDrawerItem Share_location = new SecondaryDrawerItem().withIdentifier(2).withName("Share").withIcon(R.drawable.ic_share_black_24dp);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String name,email;
        ProfileDrawerItem profileDrawerItem;
        try {
            name=user.getEmail().toString().split("@")[0];
            email=user.getEmail().toString();

            profileDrawerItem=new ProfileDrawerItem().withName(name).withEmail(email).withIcon(R.drawable.boy);
        }
        catch(NullPointerException e)
        {
            name=user.getDisplayName().toString();
            email=user.getPhoneNumber();
            profileDrawerItem=new ProfileDrawerItem().withName(name).withEmail(email).withIcon(R.drawable.boy);
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background_gradient)
                .addProfiles(profileDrawerItem)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        Button signOut=new Button(this);
        signOut.setText("Sign Out");
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent serviceIntent=new Intent(MyMap.this,ExampleService.class);
                serviceIntent.putExtra("location","");
                stopService(serviceIntent);
                finish();
                Intent i=new Intent(MyMap.this,MainActivity.class);
                startActivity(i);
            }
        });
        linearLayout.addView(signOut);
        try{
        if(!user.getEmail().isEmpty()) {
            if (!user.isEmailVerified()) {
                Button verfied = new Button(this);
                verfied.setText("Verify Email");
                verfied.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void aVoid) {
                                                              Toast.makeText(MyMap.this, "Email Send To " + user.getEmail() + " Please Verified It ", Toast.LENGTH_LONG).show();
                                                          }
                                                      }
                                );
                    }
                });
                linearLayout.addView(verfied);
            }
        }}
        catch(Exception e){}
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withFooter(linearLayout)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        Map,News,Call,Setting,Share
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch(position)
                        {
                            case 1:
                                Geocoder gc=new Geocoder(MyMap.this);
                                List<Address> add=null;
                                Address address=null;
                                try {
                                    address=gc.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude(),1).get(0);
                                    current_location=address.getLocality();

                                    Intent i=new Intent(MyMap.this,weather1.class);
                                    i.putExtra("area",current_location);
                                    startActivityForResult(i,399);
                                }
                                catch (IOException e) {
                                }
                                catch (java.lang.NullPointerException e)
                                {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyMap.this);
                                    //Setting message manually and performing action on button click
                                    builder.setMessage("Please Turn On Wifi or Mobile Data")
                                            .setCancelable(false)

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //  Action for 'NO' Button
                                                    dialog.cancel();
                                                    Toast.makeText(MyMap.this,"Please Turn On Wifi or Mobile Data",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //Creating dialog box
                                    android.app.AlertDialog alert = builder.create();
                                    //Setting the title manually
                                    alert.setTitle("No Internet Access");
                                    alert.show();
                                }
                                break;
                            case 4:
                                Intent i=new Intent(MyMap.this,News.class);
                                startActivityForResult(i,399);
                                break;
                            case 5:
                                i=new Intent(MyMap.this,HelpLine.class);
                                startActivityForResult(i,399);
                                break;
                            case 6:
                                i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivityForResult(i,38);
                                break;
                            case 7:

                                gc=new Geocoder(MyMap.this);

                                try {
                                    address=gc.getFromLocation(locationComponent.getLastKnownLocation().getLatitude(),locationComponent.getLastKnownLocation().getLongitude(),1).get(0);
                                    current_location=address.getAddressLine(0).toString();
                                    String whatsAppMessage = "Hii I Am Stuck Due To WaterLogging \n Location : http://www.google.com/maps/place/" + locationComponent.getLastKnownLocation().getLatitude()+ "," + locationComponent.getLastKnownLocation().getLongitude();
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
                                    sendIntent.setType("text/plain");
                                    sendIntent.setPackage("com.whatsapp");
                                    startActivityForResult(sendIntent,45);


                                }
                                catch (IOException e) {
                                }


                                break;
                        }
                        return true;
                    }
                })
                .build();

        drawer.deselect();
        drawer.setSelection(Map);
        drawer.setGravity(-5);



        switch_item="first load";
        Thread t1 = new Thread(new Working_thread());
        t1.start();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

       for(MarkerData markerData:markerDataList)
       {
          if(markerData.marker.getPosition().getLatitude() == marker.getPosition().getLatitude() && markerData.marker.getPosition().getLongitude()== marker.getPosition().getLongitude())
          {

                prompat.setVisibility(View.VISIBLE);
                other_option.setVisibility(View.VISIBLE);
                option_3.setVisibility(View.VISIBLE);
                appbar.setVisibility(View.GONE);
                place_search.setVisibility(View.GONE);
                waterlogging_area.setVisibility(View.VISIBLE);
                waterlogging_add.setText(markerData.getAddress());
                risk.setText(markerData.getSituation());
                return false;
            }
        }
       return false;


    }




    class Working_thread extends Thread
    {
        @Override
        public void run() {
            switch(switch_item)
            {
                case "first load":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    prompat.setVisibility(View.GONE);
                                    other_option.setVisibility(View.GONE);

                                }
                            });
                            search_anble_disable.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(appbar.getVisibility()==View.GONE)
                                    {

                                        appbar.setVisibility(View.VISIBLE);
                                        prompat.setVisibility(View.GONE);
                                        other_option.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        appbar.setVisibility(View.GONE);
                                    }
                                }
                            });
                            route.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    route_loader.setVisibility(View.VISIBLE);
                                    route.setVisibility(View.GONE);
                                    putRoute(originPoint,destinationPoint);
                                }
                            });


                            atm=findViewById(R.id.atm);
                            atm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","atm");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });


                            petrol=findViewById(R.id.petrol);
                            petrol.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","petrol");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });



                            hotel=findViewById(R.id.hotel);
                            hotel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","hotel");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });


                            grocery=findViewById(R.id.grocery);
                            grocery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","grocery");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });

                            hospital=findViewById(R.id.hospital);
                            hospital.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","hospital");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });


                            parking=findViewById(R.id.parking);
                            parking.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent i=new Intent(MyMap.this,poi_display.class);
                                    i.putExtra("url","parking");
                                    i.putExtra("lat",locationComponent.getLastKnownLocation().getLatitude()+"");
                                    i.putExtra("lng",locationComponent.getLastKnownLocation().getLongitude()+"");
                                    startActivityForResult(i,400);
                                }
                            });


                            prompat.setVisibility(View.GONE);
                            other_option.setVisibility(View.GONE);
                            materialSearchBar.setNavButtonEnabled(true);
                            materialSearchBar.setOnSearchActionListener(MyMap.this);
                            materialSearchBar.setSpeechMode(true);

                            switch_item="Database Connection";
                            Thread t6 = new Thread(new Working_thread());
                            t6.start();
                        }
                    });
                    break;
                case "Database Connection":
                    options=new FirebaseOptions.Builder()
                            .setApplicationId("1:418043090798:android:9ce4883c3caa4be592537f")
                            .setApiKey("AIzaSyDFTGD0lwpSBvS0E-UziKO8fGhxfnh3aq0")
                            .setDatabaseUrl("https://useraccess-4ee7f.firebaseio.com/")
                            .build();
                    app = creation_of_second_database();
                    secondaryDatabase=FirebaseDatabase.getInstance(app);
                    df= secondaryDatabase.getReference("Logging_Area");
                    df.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count=0;
                            if(marker!=null)
                                remove_WaterLoging_area();
                            for(DataSnapshot d:dataSnapshot.getChildren()){
                                location=d.child("latlng").getValue().toString().split(",");
                                df.child(d.child("area").getValue().toString()).child("seen").setValue("yes");
                                if(d.child("situation").getValue().equals("unsafe") && d.child("active").getValue().equals("yes")) {
                                    for (int i = 0; i < location.length; i++) {
                                        location[i].trim();
                                    }
                                    markers[count] = new MarkerOptions().position(new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]))).title("WaterLogging Is Here");
                                    Geocoder gc = new Geocoder(MyMap.this);
                                    Address address;
                                    if (gc.isPresent()) {

                                        List<Address> list = null;

                                        try {
                                            list = gc.getFromLocation(Double.parseDouble(location[0]), Double.parseDouble(location[1]),1);
                                            address = list.get(0);
                                            MarkerData markerData=new MarkerData(markers[count],address.getAddressLine(0).toString(),d.child("situation").getValue().toString());
                                            markerDataList.add(markerData);
                                        }
                                        catch(Exception e)
                                        {
                                        }
                                    }
                                    count++;
                                }
                            }
                            WaterLogging_Area();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;
                case "Navigation":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean simulateRoute = false;
                            try{
                                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                        .directionsRoute(currentRoute)
                                        .shouldSimulateRoute(simulateRoute)
                                        .waynameChipEnabled(true)
                                        .build();

                                // Call this method with Context from within an Activity
                                NavigationLauncher.startNavigation(MyMap.this, options);
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(MyMap.this,"No Route Available",Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    break;
                case "Route Search By Name":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Geocoder gc = new Geocoder(MyMap.this);
                            if (gc.isPresent()) {

                                List<Address> list = null;

                                try {
                                    double lat, lng;

                                    list = gc.getFromLocationName(materialSearchBar.getText().toString(), 1);

                                    Address address = list.get(0);
                                    Log.e("address",address.toString());
                                    place_search_add.setText(address.getAddressLine(0).toString());
                                    lat = address.getLatitude();
                                    lng = address.getLongitude();
                                    changeCameraPosition(Point.fromLngLat(lng,lat));
                                    destinationPoint = Point.fromLngLat(lng, lat);
                                    originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLatitude());
                                    add_marker(destinationPoint.longitude(),destinationPoint.latitude(),null);
                                    prompat.setVisibility(View.VISIBLE);
                                    option_3.setVisibility(View.VISIBLE);
                                    other_option.setVisibility(View.VISIBLE);
                                    appbar.setVisibility(View.GONE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                catch(Exception e)
                                {
                                    Toast.makeText(MyMap.this,"Invalid Location ",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    break;

                case "Find Out Route":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            GeoJsonSource source = MyMap.mapboxMap.getStyle().getSourceAs("destination-source-id");
                            if (source != null) {
                                source.setGeoJson(Feature.fromGeometry(destinationPoint));
                            }
                            getRoute(originPoint, destinationPoint);
                        }
                    });
                    break;


                case "On Profile Change":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {

                            prompat.setVisibility(View.VISIBLE);
                            other_option.setVisibility(View.VISIBLE);
                            option_3.setVisibility(View.VISIBLE);
                            appbar.setVisibility(View.GONE);
                            getRoute(Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude()), Point.fromLngLat(destination_latlng.longitude(), destination_latlng.latitude()));
                            putRoute(originPoint, destinationPoint);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MyMap.this,"No Distination Is Here",Toast.LENGTH_LONG).show();
                            route.setVisibility(View.VISIBLE);
                            route_loader.setVisibility(View.GONE);
                        }
                        }
                    });
                    break;


                case "On Map Ready":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            car=findViewById(R.id.car);
                            car.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {

                                        route_loader.setVisibility(View.VISIBLE);
                                        route.setVisibility(View.GONE);

                                        option=DirectionsCriteria.PROFILE_DRIVING;
                                        way.setImageResource(R.drawable.drive);

                                        switch_item="On Profile Change";
                                        Thread t6 = new Thread(new Working_thread());
                                        t6.start();
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(MyMap.this,"please find some route",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            motor_bike=findViewById(R.id.motor_bike);
                            motor_bike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {

                                        route_loader.setVisibility(View.VISIBLE);
                                        route.setVisibility(View.GONE);
                                        option=DirectionsCriteria.PROFILE_CYCLING;
                                        way.setImageResource(R.drawable.ic_motorcycle_black_24dp);
                                        switch_item="On Profile Change";
                                        Thread t6 = new Thread(new Working_thread());
                                        t6.start();
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(MyMap.this,"please find some route",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            walking=findViewById(R.id.walking);
                            walking.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {

                                        route_loader.setVisibility(View.VISIBLE);
                                        route.setVisibility(View.GONE);
                                        option=DirectionsCriteria.PROFILE_WALKING;
                                        way.setImageResource(R.drawable.directions_walk);

                                        switch_item="On Profile Change";
                                        Thread t6 = new Thread(new Working_thread());
                                        t6.start();
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(MyMap.this,"please find some route",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });



                            destination.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {

                                        place_search_add.setText(Destination);
                                        time.setText(Time);
                                        distance.setText(Distance);
                                        prompat.setVisibility(View.VISIBLE);
                                        other_option.setVisibility(View.VISIBLE);
                                        option_3.setVisibility(View.VISIBLE);
                                        appbar.setVisibility(View.GONE);
                                        distanceAndtime.setVisibility(View.VISIBLE);
                                        place_search.setVisibility(View.VISIBLE);
                                        waterlogging_area.setVisibility(View.GONE);
                                        changeCameraPosition(Point.fromLngLat(destination_latlng.longitude(), destination_latlng.latitude()));
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(MyMap.this,"The Destination Is Not Visible",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            origin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        changeCameraPosition(Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),locationComponent.getLastKnownLocation().getLatitude()));
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(MyMap.this,"The Destination Is Not Visible",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            option_view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(MyMap.this);
                                    final View mView=getLayoutInflater().inflate(R.layout.view_store,null);
                                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @SuppressLint("WrongConstant")
                                        public void onClick(DialogInterface dialog, int id) {
                                            option_profile=(RadioGroup)mView.findViewById(R.id.radioButton);
                                            int selectedId=option_profile.getCheckedRadioButtonId();
                                            R_button=(RadioButton)mView.findViewById(selectedId);
                                            try {
                                                if(R_button.getText().toString().equals("Normal"))
                                                {
                                                    MyMap.mapboxMap.setStyle("mapbox://styles/adityatheprogrammer/ck8qrmh880ii61intektayhn9");
                                                }
                                                else if(R_button.getText().toString().equals("Streets"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.MAPBOX_STREETS);
                                                }
                                                else if(R_button.getText().toString().equals("Traffic-Night"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.TRAFFIC_NIGHT);
                                                }
                                                else if(R_button.getText().toString().equals("Traffic-Day"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.TRAFFIC_DAY);
                                                }
                                                else if(R_button.getText().toString().equals("Light"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.LIGHT);
                                                }
                                                else if(R_button.getText().toString().equals("Dark"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.DARK);
                                                }
                                                else if(R_button.getText().toString().equals("Satellite-Street"))
                                                {
                                                    MyMap.mapboxMap.setStyle("mapbox://styles/adityatheprogrammer/ck7knjecv0vl91jmr4dxp1uwh");
                                                }
                                                else if(R_button.getText().toString().equals("OUTDOOR"))
                                                {
                                                    MyMap.mapboxMap.setStyle(Style.OUTDOORS);
                                                }

                                            }
                                            catch (NullPointerException e)
                                            {

                                            }

                                            try {
                                                add_marker(destinationPoint.longitude(),destinationPoint.latitude(),null);
                                            }
                                            catch (Exception e)
                                            {

                                            }


                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });

                                    builder.setView(mView);
                                    AlertDialog dialog=builder.create();
                                    dialog.show();

                                }
                            });

                        }
                    });
                    break;



                case "On Map Click":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyMap.mapboxMap.clear();
                            WaterLogging_Area();
                            prompat.setVisibility(View.VISIBLE);
                            other_option.setVisibility(View.VISIBLE);
                            option_3.setVisibility(View.VISIBLE);
                            distanceAndtime.setVisibility(View.GONE);
                            appbar.setVisibility(View.GONE);

                            place_search.setVisibility(View.VISIBLE);

                            if(onNewRoute==1)
                            {
                                add_marker(destinationPoint.longitude(),destinationPoint.latitude(),null);
                                onNewRoute=0;
                            }
                            else
                            {

                                destinationPoint = Point.fromLngLat(point_long, point_lat);
                                originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                        locationComponent.getLastKnownLocation().getLatitude());
                                add_marker(destinationPoint.longitude(),destinationPoint.latitude(),null);
                            }
                        }
                    });

                    break;


            }
        }

    }

    public FirebaseApp creation_of_second_database()
    {
        FirebaseApp fa;
        try {
            fa=FirebaseApp.initializeApp(getApplicationContext(),options,
                    "stage"+count);
        }
        catch(Exception e)
        {
            count++;
            fa=creation_of_second_database();
        }
        return fa;
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MyMap.mapboxMap = mapboxMap;

        MyMap.mapboxMap.setStyle("mapbox://styles/adityatheprogrammer/ck8qrmh880ii61intektayhn9", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {

                enableLocationComponent(style);


                addDestinationIconSymbolLayer(style);
                MyMap.mapboxMap.addOnMapClickListener(MyMap.this);
                MyMap.mapboxMap.setOnMarkerClickListener(MyMap.this);
                button = findViewById(R.id.startNavigation);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch_item="Navigation";
                        Thread t2 = new Thread(new Working_thread());
                        t2.start();
                    }
                });

                onRouteSelectionChangeListener=new OnRouteSelectionChangeListener() {
                    @Override
                    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {


                        currentRoute=directionsRoute;
                    }
                };

                destination=(FloatingActionButton)findViewById(R.id.destinaion);
                origin=(FloatingActionButton)findViewById(R.id.current_location);
                option_view=(FloatingActionButton)findViewById(R.id.change_view);

                switch_item="On Map Ready";
                Thread t4 = new Thread(new Working_thread());
                t4.start();


            }
        });
    }


    private void remove_WaterLoging_area()
    {
        try{
            for(int i=0;i<marker.length;i++)
            {
                MyMap.mapboxMap.removeMarker(marker[i]);
                markers[i]=null;
            }
        }
        catch(Exception e)
        {

        }
    }


   private void WaterLogging_Area()
    {
        try {
            for (int i = 0; i < markers.length; i++) {


                marker[i] = MyMap.mapboxMap.addMarker(markers[i].icon(IconFactory.getInstance(this).fromResource(R.drawable.waterlogging)));
            }
        }
        catch(Exception e)
        {

        }
    }


    @SuppressLint("MissingPermission")
    private void initLocationEngine() {

        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(enabled)
        {
            option_3.setVisibility(View.INVISIBLE);
        }
        else
        {
            option_3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        MyMap.mapboxMap.clear();
        appbar.setVisibility(View.GONE);
        materialSearchBar.disableSearch();
        WaterLogging_Area();
        Route_Search();
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer();
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                displaySpeechRecognizer();
                break;
            case MaterialSearchBar.BUTTON_BACK:
                materialSearchBar.disableSearch();
                break;
        }
    }


    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer()
    {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(MyMap.this,"Speech Mode Is Not Working Right Now ",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if(requestCode==38)
        {
            drawer.deselect();
            drawer.setSelection(Map);
        }
        else if(requestCode==45)
        {
            drawer.deselect();
            drawer.setSelection(Map);
        }
        else if(requestCode==399)
        {
            if(resultCode==RESULT_OK)
            {
                drawer.deselect();
                drawer.setSelection(Map);
            }
        }

        else if(requestCode==400)
        {
            if(resultCode==RESULT_OK)
            {
                String address=data.getStringExtra("address");
                materialSearchBar.setText(address);
                MyMap.mapboxMap.clear();
                appbar.setVisibility(View.GONE);
                waterlogging_area.setVisibility(View.GONE);
                place_search.setVisibility(View.VISIBLE);
                WaterLogging_Area();
                Route_Search();
            }
        }
        else if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK)
        {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            materialSearchBar.callOnClick();
            materialSearchBar.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {

    }



    private  class MainActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MyMap> activityWeakReference;

        MainActivityLocationCallback(MyMap activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }


        @Override
        public void onSuccess(LocationEngineResult result) {
            MyMap activity;
            activity = activityWeakReference.get();
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)
                    .setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            settingsBuilder.setAlwaysShow(true);
            Task<LocationSettingsResponse> result1 = LocationServices.getSettingsClient(MyMap.this)
                    .checkLocationSettings(settingsBuilder.build());
            result1.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response =
                                task.getResult(ApiException.class);
                    } catch (ApiException ex) {
                        switch (ex.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException resolvableApiException =
                                            (ResolvableApiException) ex;
                                    resolvableApiException
                                            .startResolutionForResult(MyMap.this,199);
                                } catch (IntentSender.SendIntentException e) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                break;
                        }
                    }
                }
            });
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }

                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {

            MyMap activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void Route_Search()
    {
        switch_item="Route Search By Name";
        Thread t3 = new Thread(new Working_thread());
        t3.start();
    }

   public void putRoute(Point originPoint,Point destinationPoint)
    {

        this.originPoint=originPoint;
        this.destinationPoint=destinationPoint;
        switch_item="Find Out Route";
        Thread t4 = new Thread(new Working_thread());
        t4.start();

    }
    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        point_long=point.getLongitude();

        point_lat=point.getLatitude();
        Geocoder gc=new Geocoder(MyMap.this);
        List<Address> add=null;
        Address address=null;

            try {
                address=gc.getFromLocation(point_lat,point_long,1).get(0);

                place_search_add.setText(address.getAddressLine(0).toString());
                distance.setText("-------------");
                time.setText("-------------");
            } catch (IOException e) {
                place_search_add.setText("No Address Found");
                distance.setText("-------------");
                time.setText("-------------");
                noNet=true;
            }
            catch (Exception e)
            {
                Toast.makeText(MyMap.this,"Please Wait Until Map Is Load Successfully",Toast.LENGTH_LONG).show();
            }
            switch_item="On Map Click";
            Thread t5 = new Thread(new Working_thread());
            t5.start();


        return false;
    }

    private void getRoute(Point origin, final Point destination) {

        NavigationRoute.builder(this)
                .accessToken(getString(R.string.access_token))
                .origin(origin)
                .profile(option)
                .alternatives(true)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(MyMap.this, "No routes found",Toast.LENGTH_LONG).show();
                            route_loader.setVisibility(View.GONE);
                            route.setVisibility(View.VISIBLE);
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(MyMap.this, "No routes found",Toast.LENGTH_LONG).show();
                            route_loader.setVisibility(View.GONE);
                            route.setVisibility(View.VISIBLE);
                            return;
                        }
                        else if(!response.isSuccessful())
                        {
                            Toast.makeText(MyMap.this,"Error Sorry Route Not Possible ",Toast.LENGTH_LONG).show();
                            route_loader.setVisibility(View.GONE);
                            route.setVisibility(View.VISIBLE);
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {

                            navigationMapRoute = new NavigationMapRoute(null, mapView, MyMap.mapboxMap, R.style.NavigationMapRoute);

                            if(onRouteSelectionChangeListener!=null)
                                navigationMapRoute.setOnRouteSelectionChangeListener(onRouteSelectionChangeListener);
                        }

                        navigationMapRoute.addRoutes(response.body().routes());
                        String dis=response.body().routes().get(0).distance()/1000+"";
                        try {
                            if(dis.contains("."))
                            {
                                dis=dis.substring(0,dis.indexOf("."))+"."+dis.substring(dis.indexOf(".")+1,dis.indexOf(".")+3);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        distance.setText(" "+dis+"km");
                        Distance=dis+"km";



                        double se=response.body().routes().get(0).duration();
                        double sec = se % 60;
                        double min = (se / 60)%60;
                        double hours = (se/60)/60;

                        String h=null,m=null,s=null;
                        if((hours+"").charAt(0)=='0')
                        {
                            h="0";
                        }
                        if((sec+"").charAt(0)=='0')
                        {
                            s="0";
                        }
                        if((min+"").charAt(0)=='0')
                        {
                            m="0";
                        }
                        if(h==null)
                        {
                            if((hours+"").contains("."))
                            {
                                h=(hours+"").substring(0,(hours+"").indexOf("."));
                            }else{h=hours+"";}
                        }
                        if(s==null)
                        {
                            if((sec+"").contains("."))
                            {
                                s=(sec+"").substring(0,(sec+"").indexOf("."));
                            }else{s=sec+"";}
                        }
                        if(m==null)
                        {
                            if((min+"").contains("."))
                            {
                                m=(min+"").substring(0,(min+"").indexOf("."));
                            }else{m=min+"";}
                        }

                        Time=h+"H:"+m+"M:"+s+"S";

                        time.setText(Time);

                        route_loader.setVisibility(View.GONE);
                        Geocoder gc=new Geocoder(MyMap.this);
                        List<Address> add=null;
                        Address address=null;

                        try {
                            address=gc.getFromLocation(destination.latitude(),destination.longitude(),1).get(0);
                            Destination=address.getAddressLine(0).toString();
                            if(noNet){place_search_add.setText(Destination);noNet=false;}

                        } catch (IOException e) {
                            place_search_add.setText("No Address Found");
                        }
                        route.setVisibility(View.VISIBLE);
                        distanceAndtime.setVisibility(View.VISIBLE);
                        changeCameraPosition(Point.fromLngLat(destination.longitude(),destination.latitude()));
                        destination_latlng=Point.fromLngLat(destination.longitude(),destination.latitude());
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e("route","sorry");
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MyMap.this);
                        //Setting message manually and performing action on button click
                        builder.setMessage("Please Turn On Wifi or Mobile Data")
                                .setCancelable(false)

                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                        Toast.makeText(MyMap.this,"Please Turn On Wifi or Mobile Data",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //Creating dialog box
                        android.app.AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("No Internet Access");
                        alert.show();
                        route_loader.setVisibility(View.GONE);
                        route.setVisibility(View.VISIBLE);

                    }


                });
    }

    public void add_marker(double lng,double lat,String message)
    {



        MyMap.mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(message));
    }



    public void changeCameraPosition(Point lattitudelongitude)
    {
        LatLng latlng=new LatLng(lattitudelongitude.latitude(),lattitudelongitude.longitude());
        CameraPosition position = new CameraPosition.Builder()
                .target(latlng) // Sets the new camera position
                .zoom(12) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        MyMap.mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);


    }
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {


                locationComponent = MyMap.mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(this, loadedMapStyle);
                locationComponent.setLocationComponentEnabled(true);
                locationComponent.setCameraMode(CameraMode.TRACKING);

                locationComponent.zoomWhileTracking(15.0);
                initLocationEngine();


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen())
        {
            drawer.closeDrawer();
        }
        else if(appbar.getVisibility()==View.VISIBLE)
        {
            appbar.setVisibility(View.GONE);

        }
        else if(prompat.getVisibility()==View.VISIBLE)
        {
            prompat.setVisibility(View.GONE);
            other_option.setVisibility(View.GONE);
        }
        else
        {
            finish();
        }
        //super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}