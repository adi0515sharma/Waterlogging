package com.example.myapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


class NetworkUtil {


    public static void getConnectivityStatusString(final Context context) {
        String status = null;


        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            //Toast.makeText(context,activeNetwork.getDetailedState().toString(),Toast.LENGTH_LONG).show();
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            //Setting message manually and performing action on button click
            builder.setMessage("Please Turn On Wifi or Mobile Data")
                    .setCancelable(false)

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            Toast.makeText(context,"Please Turn On Wifi or Mobile Data",
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
