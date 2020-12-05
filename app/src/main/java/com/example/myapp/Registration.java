package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    String name,phone,confirmpassword,password,email;
    EditText Name,EMail,Password,ConfirmPassword,PhoneNo;
    Button SignUp,SignIn;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    DatabaseReference df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth=FirebaseAuth.getInstance();
        fd=FirebaseDatabase.getInstance();
        df=fd.getReference("Users");
        EMail=(EditText)findViewById(R.id.Email);
        Password=(EditText)findViewById(R.id.Password);
        progressDialog = new ProgressDialog(this);
        ConfirmPassword=(EditText)findViewById(R.id.ConfirmPassword);
       // registration_loader=findViewById(R.id.registration_loader);
        SignUp=(Button) findViewById(R.id.signup);
       // SignIn=(Button)findViewById(R.id.signin);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                SignUp.setVisibility(View.GONE);
                SignUp_Process();
            }
        });
       /* SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),MainActivity.class);

                startActivity(i);
                finish();
            }
        });*/

    }






    public void SignUp_Process()
    {

        email=EMail.getText().toString();
        confirmpassword=ConfirmPassword.getText().toString();
        password=Password.getText().toString();
       // registration_loader.setVisibility(View.VISIBLE);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        if (Verify(email,confirmpassword,password)==true)
        {


            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"Successfull Registration Done",Toast.LENGTH_SHORT).show();
                                /*Intent i=new Intent(getApplicationContext(),Home.class);
                                startActivity(i);*/
                                //Toast.makeText(getApplicationContext(),"Successfull Registration Done",Toast.LENGTH_SHORT).show();



                                 FirebaseUser user = auth.getInstance().getCurrentUser();
                                 Intent i=new Intent(getApplicationContext(),MyMap.class);
                                 i.putExtra("count","1");
                                 i.putExtra("Email",user.getEmail());
                                 startActivity(i);
                                 finish();



                            }
                            else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(Registration.this, "User already exist.",  Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });

        }
        else
        {
            Toast.makeText(getApplicationContext(),"UnSuccessFull Registration !!!!",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


        //SignUp.setVisibility(View.VISIBLE);
        return;

    }



    private boolean Verify(String email,String confirmpassword,String password) {

        if(email.isEmpty())
        {
            EMail.setError("please enter the E-mail");
            return false;
        }
        else if(password.isEmpty())
        {
            Password.setError("please enter the Password");
            return false;
        }
        else if(confirmpassword.isEmpty())
        {
            ConfirmPassword.setError("please enter the ConfirmPassword");
            return false;
        }

        else if(!(confirmpassword.equals(password)))
        {
            ConfirmPassword.setError("the ConfirmPassword should be equal to Password");
            return false;
        }
        else if(!email.endsWith("@gmail.com"))
        {
            EMail.setError("please enter the proper E-mail");
            return false;
        }
        return true;
    }
}
