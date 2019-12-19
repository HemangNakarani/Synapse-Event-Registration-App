package com.hemangnh18.synapsepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    EditText username,password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button getin;
    FirebaseAuth auth;
    String type;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        type= getIntent().getStringExtra("type");
        //Toast.makeText(MainActivity.this,type,Toast.LENGTH_LONG).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login for "+ type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("Auth",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        progressDialog.dismiss();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        getin = findViewById(R.id.getin);

        getin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Validate())
                {
                    if(type.equals("coordinator"))
                    {
                        Login(("c."+ username.getText().toString()).toLowerCase(),password.getText().toString());
                    }
                    else
                    {
                        Login(username.getText().toString().toLowerCase(),password.getText().toString());
                    }

                }
            }
        });

    }


    private void Login(final String name, String password) {

        progressDialog.setMessage("Signing In...");
        progressDialog.show();
        auth.signInWithEmailAndPassword(name+"@gmail.com",password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            editor.putString("username",name);
                            editor.apply();
                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            RedirectToactivity();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    boolean Validate()
    {

        if(username.getText().toString().equals(""))
        {
                username.setError("Enter Username");
                username.requestFocus();
                return false;
        }
        else if(password.getText().toString().equals(""))
        {
            password.setError("Enter Password");
            password.requestFocus();
            return false;
        }
        else
        {
                return true;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    void RedirectToactivity()
    {

        Intent it;
        SharedPreferences preferences = getSharedPreferences("Auth",MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("type",type);
        editor.apply();
        if(type.equals("coordinator"))
        {

             it = new Intent(MainActivity.this, ScanActivity.class);
        }
        else
        {
             it = new Intent(MainActivity.this, Registration.class);
        }

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }
}
