package com.example.myapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.Arrays;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ContactList extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{
    public static final int REQUEST_READ_CONTACTS = 79;
    ListView list;
    String[] items;
    ImageView dial;
    ArrayList<String> mobileArray,backup;
    ArrayAdapter<String> adapter;
    ImageView toolbar;
    LinearLayout contact_loader;
    ProgressDialog progressDialog;
    private EditText etSearch;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        toolbar=findViewById(R.id.cancel_back);
        progressDialog = new ProgressDialog(ContactList.this);
        contact_loader=findViewById(R.id.contact_loader);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = findViewById(R.id.list);
        dial=findViewById(R.id.dialpad);
        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                startActivity(callIntent);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {



            new weatherTask().execute();


        } else {
            requestPermission();
        }

       // ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
       // list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    public void setSearch()
    {
        etSearch=findViewById(R.id.search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){

                    initList(backup);
                }

                else{

                    // perform search

                    searchItem(s.toString());

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    void initList(ArrayList<String> item)
    {
        items=new String[item.size()];

        for(int i=0;i<item.size();i++)
            items[i]=item.get(i);

        mobileArray=new ArrayList<>(Arrays.asList(items));

        adapter=new ArrayAdapter<String>(this,
                R.layout.contact_display, R.id.Name, mobileArray);

        list.setAdapter(adapter);

    }

    public void searchItem(String textToSearch){

        for(String item:items){

            if(!item.contains(textToSearch)){

                mobileArray.remove(item);

            }

        }
        adapter.notifyDataSetChanged();
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("contact","contacts loading ......");
                    //progressDialog.setMessage("Loading Contacts");
                    //progressDialog.show();
                    new weatherTask().execute();
                   // progressDialog.dismiss();
                    Log.e("contact finish","done yahh ");

                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((!cur.isClosed()? cur.getCount() : 0)>0) {
            while (!cur.isClosed() && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(name==phoneNo)
                        {
                            name="UnKnown";
                            if(!nameList.contains(name+"\n"+phoneNo))
                                nameList.add(name+"\n"+phoneNo);
                        }
                        else
                        {
                            if(!nameList.contains(name+"\n"+phoneNo))
                                nameList.add(name+"\n"+phoneNo);
                        }
                    }

                    pCur.close();
                }
            }
        }
        if (!cur.isClosed()) {
            cur.close();
        }
        return nameList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i=getIntent();
        Bundle bundle=new Bundle();
        bundle.putString("number",mobileArray.get(position).toString().split("\n")[1]);
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        Toast.makeText(ContactList.this,mobileArray.get(position).toString().split("\n")[1].toString(),Toast.LENGTH_LONG);
        finish();


    }


    class weatherTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    mobileArray=getAllContacts();
                    backup=mobileArray;
                    initList(mobileArray);
                    setSearch();
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contact_loader.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contact_loader.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }


    }






    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KEYCODE_BACK)
        {
            finish();
        }
        return false;
    }
}