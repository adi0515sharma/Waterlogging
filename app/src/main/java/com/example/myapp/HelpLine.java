package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class HelpLine extends AppCompatActivity {

    LinearLayout police,ambulance,fire,senior,women,ndrf,railway,tourist,gas,kisan;
    ImageView add_contact;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_line);
        police=(LinearLayout)findViewById(R.id.police);
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("100");
            }
        });
        toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        fire=(LinearLayout)findViewById(R.id.fire);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("101");
            }
        });
        ambulance=(LinearLayout)findViewById(R.id.ambulance);
        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("102");
            }
        });
        senior=(LinearLayout)findViewById(R.id.senior);
        senior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("1291");
            }
        });
        women=(LinearLayout) findViewById(R.id.womenhelpline);
        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("1091");
            }
        });
        ndrf=(LinearLayout) findViewById(R.id.NDRF);
        ndrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("9711077372");
            }
        });
        tourist=(LinearLayout)findViewById(R.id.travler);
        tourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("1800111363");
            }
        });
        kisan=(LinearLayout)findViewById(R.id.Kisan);
        kisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("1551");
            }
        });
        gas=(LinearLayout)findViewById(R.id.lpg_gas);
        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("1906");
            }
        });
        railway=(LinearLayout) findViewById(R.id.railway);
        railway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_to("139");
            }
        });

        add_contact=(ImageView)findViewById(R.id.custom_call);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HelpLine.this,ContactList.class);
                startActivityForResult(i,999);
            }
        });

    }

    public void call_to(String number)
    {
        if (ActivityCompat.checkSelfPermission(HelpLine.this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+number));
            startActivity(callIntent);
        } else {
            requestPermission();
        }

    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(HelpLine.this, Manifest.permission.CALL_PHONE)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(HelpLine.this, new String[]{android.Manifest.permission.CALL_PHONE},
                    1);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            Bundle extras = data.getExtras();
            call_to(extras.getString("number"));
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
