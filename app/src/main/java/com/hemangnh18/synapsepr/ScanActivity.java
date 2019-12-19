package com.hemangnh18.synapsepr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hemangnh18.synapsepr.ApiCalls.APIClient;
import com.hemangnh18.synapsepr.ApiCalls.ApiInterface;
import com.hemangnh18.synapsepr.Models.Participant;
import com.hemangnh18.synapsepr.Models.PostRes;
import com.hemangnh18.synapsepr.Models.PostResponce;

import java.net.SecureCacheResponse;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {


    ImageView scan;
    ProgressDialog progressDialog;
    private IntentIntegrator qrScan;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hey! Scan here😁");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        scan= findViewById(R.id.scan);
        text = findViewById(R.id.text);
        qrScan = new IntentIntegrator(this);
        progressDialog = new ProgressDialog(ScanActivity.this);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {

                Participant participant = getDecoded(result.getContents().toString());
                PostScan(participant);
                //Toast.makeText(ScanActivity.this,participant.getName()+" "+participant.getEvent(),Toast.LENGTH_LONG).show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



     void PostScan(Participant participant)
    {

        progressDialog.setTitle("Updating Attendence...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        SharedPreferences preferences= getSharedPreferences("Auth",MODE_PRIVATE);

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<PostRes> call = apiService.sendAttendence("scanParticipant",participant.getCode(),participant.getEvent(),preferences.getString("username",""));

        call.enqueue(new Callback<PostRes>() {
            @Override
            public void onResponse(Call<PostRes> call, Response<PostRes> response) {

                if(response.isSuccessful()) {

                      PostRes pp=response.body();
                      progressDialog.dismiss();
                      Toast.makeText(ScanActivity.this,pp.getResponce().getCode().toString()+"'s Attendence Marked",Toast.LENGTH_LONG).show();

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(ScanActivity.this,"Attendence UnsuccessFull !",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostRes> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ScanActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }


    Participant getDecoded(String base64)
    {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String json = new String(data, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Participant party = gson.fromJson(json,Participant.class);
        return party;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dots,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.logout:
            {
                SignOut();
                return true;
            }
            case R.id.about:
            {
                startActivity(new Intent(ScanActivity.this, AboutActivity.class));
                return true;
            }

        }
        return false;
    }


    void SignOut()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setMessage("Are You Sure ?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ScanActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
