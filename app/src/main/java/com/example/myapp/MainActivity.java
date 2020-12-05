package com.example.myapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button SignIn,Register;
    EditText Email_id,password;
    private int count=0;
    private DatabaseReference df;
    private FirebaseDatabase secondaryDatabase;
    private BroadcastReceiver MyReceiver=null;
    private FirebaseApp app;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private FirebaseOptions options;
    private CallbackManager mCallbackManager;
    FirebaseUser user ;
    TextView forget_pass;
    @Override
    public void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

                extra_work();

                Intent i=new Intent(getApplicationContext(),MyMap.class);
                i.putExtra("count","1");

                startActivity(i);
                finish();

        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignIn=(Button)findViewById(R.id.textView5);
        Email_id=(EditText)findViewById(R.id.editText);
        password=(EditText)findViewById(R.id.editText2);
        Register=(Button)findViewById(R.id.Registration);
        progressDialog = new ProgressDialog(this);

        mCallbackManager = CallbackManager.Factory.create();
        forget_pass=findViewById(R.id.forget_pass);
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                        FirebaseAuth.getInstance().sendPasswordResetEmail(Email_id.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Email Send to " + Email_id.getText().toString(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "No Internet Access", Toast.LENGTH_LONG).show();
                                        }
                                    }


                                });



                }
                catch(java.lang.IllegalArgumentException e)
                {
                    Toast.makeText(MainActivity.this,"Please Enter E-mail Id For This",Toast.LENGTH_LONG).show();
                }

            }
        });

        mAuth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Registration.class);
                startActivity(i);
            }
        });
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Login You Please Wait...");
                progressDialog.show();
                if(Email_id.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"please enter all values ",Toast.LENGTH_LONG).show();
                    return;
                }
                auth.signInWithEmailAndPassword(Email_id.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),"Login sucessfully done",Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = auth.getInstance().getCurrentUser();


                                        extra_work();
                                        Intent i=new Intent(getApplicationContext(),MyMap.class);

                                        i.putExtra("count","1");
                                        startActivity(i);
                                        finish();

                                } else {
                                    Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();

                                // ...
                            }
                        });

            }
        });

        final ImageView loginButton = findViewById(R.id.facebook_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this,"No Internet Access",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this,"No Internet Access",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }


    void extra_work()
    {
        MyReceiver = new MyReceiver();
        broadcastIntent();
        Intent serviceIntent=new Intent(MainActivity.this,ExampleService.class);
        serviceIntent.putExtra("location","");
        startService(serviceIntent);


        options=new FirebaseOptions.Builder()
                .setApplicationId("1:418043090798:android:9ce4883c3caa4be592537f")
                .setApiKey(" AIzaSyDFTGD0lwpSBvS0E-UziKO8fGhxfnh3aq0 ")
                .setDatabaseUrl("https://useraccess-4ee7f.firebaseio.com/")
                .build();
        //Toast.makeText(MyMap.this,options.getApiKey(),Toast.LENGTH_LONG).show();

        app = creation_of_second_database();
        secondaryDatabase= FirebaseDatabase.getInstance(app);
        df= secondaryDatabase.getReference("Logging_Area");

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotificationManagerCompat notificationManager;
                notificationManager= NotificationManagerCompat.from(MainActivity.this);

                for(DataSnapshot d:dataSnapshot.getChildren()){

                    if(d.child("seen").getValue().toString().equals("no") && d.child("situation").getValue().toString().equals("unsafe") && d.child("active").getValue().toString().equals("yes"))
                    {

                       // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        Intent notificationIntent=new Intent(MainActivity.this,MyMap.class);

                      //  notificationIntent.putExtra("Email","aditya@gmail.com");


                        PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,1,notificationIntent,1);
                        Notification notification=new NotificationCompat.Builder(MainActivity.this,NotificationManager_.CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.loginlogo)
                                .setContentTitle("Warning")
                                .setContentText("WaterLogging occure in "+d.child("area").getValue().toString())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(2,notification);
                    }
                    else if(d.child("seen").getValue().toString().equals("no") && d.child("situation").getValue().toString().equals("safe") && d.child("active").getValue().toString().equals("yes"))
                    {
                       // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                         Intent notificationIntent=new Intent(MainActivity.this,MyMap.class);

                    //    notificationIntent.putExtra("Email","aditya@gmail.com");


                        PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity.this,1,notificationIntent,1);
                        Notification notification=new NotificationCompat.Builder(MainActivity.this,NotificationManager_.CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.loginlogo)
                                .setContentTitle("Warning")
                                .setContentText("WaterLogging is Secure in "+d.child("area").getValue().toString())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(3,notification);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            extra_work();
                            startActivity(new Intent(MainActivity.this, MyMap.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            task.getException().printStackTrace();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void broadcastIntent() {
        try{
            registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        catch(Exception e)
        {

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
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(MyReceiver);
        }
        catch (Exception r)
        {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastIntent();
    }
}
