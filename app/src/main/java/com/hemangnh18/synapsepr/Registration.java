package com.hemangnh18.synapsepr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.hemangnh18.synapsepr.ApiCalls.APIClient;
import com.hemangnh18.synapsepr.ApiCalls.ApiInterface;
import com.hemangnh18.synapsepr.Email.SendMail;
import com.hemangnh18.synapsepr.Models.BitmapSaver;
import com.hemangnh18.synapsepr.Models.EventsList;
import com.hemangnh18.synapsepr.Models.Participant;
import com.hemangnh18.synapsepr.Models.PostResponce;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    ProgressDialog progressDialog ;
    private static int AUTHENTICATE = 500;
    SharedPreferences sharedPreferences;
    EditText name,number,alt_number,email,institute,city,total_members;
    String event="",teamis="",total_teammembers,code;
    RadioButton team,individual;
    File file;
    Button register_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        name= findViewById(R.id.fullname_p);
        number=findViewById(R.id.phone_p);
        alt_number = findViewById(R.id.alt_phone_p);
        email=findViewById(R.id.email_p);
        institute=findViewById(R.id.institute_p);
        city = findViewById(R.id.city_p);

        spinner= findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, EventsList.EventsName));

        total_members = findViewById(R.id.total_members_p);
        total_members.setVisibility(View.GONE);

        team = findViewById(R.id.team);
        individual = findViewById(R.id.individual);

        register_button = findViewById(R.id.register_btn);


            progressDialog = new ProgressDialog(Registration.this);
            sharedPreferences  = getSharedPreferences("Auth",MODE_PRIVATE);
            register_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CloseKeyboard();
                    if(Validate())
                    {

                        if(alt_number.getText().toString().equals(""))
                        {
                            alt_number.setText(number.getText().toString());
                        }

                        Authenticate();
                    }

                }
            });

        }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position==0){
            register_button.setClickable(false);
        }else {
            register_button.setClickable(true);
            event = parent.getItemAtPosition(position).toString();
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {}


    public void onRadioButtonClicked(View view)
    {

        switch (view.getId())
        {

            case R.id.team:
            {
                teamis = "Team";
                total_members.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.individual:
            {
               teamis="Individual";
               total_members.setVisibility(View.GONE);
                break;
            }
        }

    }


    boolean Validate()
    {
            if(name.getText().toString().equals(""))
            {
                    name.setError("Enter Name");
                    name.requestFocus();
                    return false;
            }
            else if(number.getText().toString().equals(""))
            {
                number.setError("Enter Number");
                number.requestFocus();
                return false;
            }
            else if(email.getText().toString().equals(""))
            {
                email.setError("Enter Email");
                email.requestFocus();
                return false;
            }
            else if(institute.getText().toString().equals(""))
            {
                institute.setError("Enter Institute Name");
                institute.requestFocus();
                return false;
            }
            else if(city.getText().toString().equals(""))
            {
                city.setError("Enter City");
                city.requestFocus();
                return false;
            }
            else if(event.equals(""))
            {
                Toast.makeText(Registration.this,"Choose Any Event",Toast.LENGTH_LONG).show();
                return false;
            }
            else if(teamis.equals(""))
            {
                Toast.makeText(Registration.this,"Choose as a Team/Individual",Toast.LENGTH_LONG).show();
                return false;
            }
            else if(teamis.equals("Team"))
            {
                total_teammembers = total_members.getText().toString();
                if(total_teammembers.equals(""))
                {
                    total_members.setError("Enter Number of Team Members");
                    total_members.requestFocus();
                    return false;
                }
                else
                {
                    return true;
                }

            }
            else
            {
                total_teammembers= "1";
                return true;
            }

    }

    void Clear()
    {
            name.setText("");
            number.setText("");
            alt_number.setText("");
            email.setText("");
            institute.setText("");
            city.setText("");
            spinner.setSelection(0);
            team.setChecked(false);
            individual.setChecked(false);
            total_members.setText("");
    }


    public void Post(final Participant participant)
    {

        progressDialog.setTitle("Updating Your Registration...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<PostResponce> call = apiService.sendPost("addParticipant",participant.getName(),participant.getNumber(),participant.getAlt_number(),participant.getEmail(),participant.getInstitute(),participant.getCity(),participant.getEvent(),participant.getTeam(),participant.getTotal_members(),participant.getRegistred_by());

        call.enqueue(new Callback<PostResponce>() {
            @Override
            public void onResponse(Call<PostResponce> call, Response<PostResponce> response) {

                if(response.isSuccessful()) {

                    PostResponce pp = response.body();
                    //progressDialog.dismiss();
                    participant.setCode(pp.getResponce().getCode().toString());
                    //Toast.makeText(Registration.this,pp.getResponce().getCode().toString(),Toast.LENGTH_SHORT).show();
                    BarcodeEncoder(participant);
                }
                else
                {
                    progressDialog.dismiss();
                    participant.setCode("404:(");
                    Toast.makeText(Registration.this,"Responce UnsuccessFull",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponce> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(Registration.this,t.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    void CloseKeyboard()
    {
        View view =  this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    void BarcodeEncoder(Participant participant) {
        progressDialog.setTitle("Generating QR Code...");
        Gson gson = new Gson();
        String json = gson.toJson(participant);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(base64, BarcodeFormat.QR_CODE, 2000, 2000);

            int offset = 400;
            // Initialize a new Bitmap to hold the source bitmap
            Bitmap dstBitmap = Bitmap.createBitmap(
                    bitmap.getWidth() + offset, // Width
                    bitmap.getHeight() + offset * 2 + offset/2, // Height
                    Bitmap.Config.ARGB_8888 // Config
            );

            // Initialize a new Canvas instance
            Canvas canvas = new Canvas(dstBitmap);

            // Draw a solid color on the canvas as background
            canvas.drawColor(Color.argb(255,255,211,226));

            canvas.drawBitmap(
                    bitmap, // Bitmap
                    offset/2, // Left
                    offset, // Top
                    null // Paint
            );



            Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/bebas.ttf");
            Paint tPaint = new Paint();
            tPaint.setTypeface(tf);
            tPaint.setTextSize(330);
            tPaint.setColor(Color.BLACK);
            tPaint.setStyle(Paint.Style.FILL);

            canvas.drawText("Synapse '20", (dstBitmap.getWidth() - tPaint.measureText("Synapse '20"))/2, 330, tPaint);

            String p_name=participant.getName();
            String p_event= participant.getEvent();


            tPaint.setTextSize(280);
            float width = tPaint.measureText(p_name);
            float x_coord = (dstBitmap.getWidth() - width)/2;
            float y_coord = (dstBitmap.getHeight()- 310f);


            canvas.drawText(p_name, x_coord, y_coord, tPaint);

            tPaint.setTextSize(180);
            float width1 = tPaint.measureText(p_event);
            float x_coord1 = (dstBitmap.getWidth() - width1)/2;
            float y_coord1 = (dstBitmap.getHeight()- 120f);

            canvas.drawText(p_event, x_coord1, y_coord1, tPaint);

            file = BitmapSaver.saveImageToExternalStorage(Registration.this, dstBitmap,participant);

            progressDialog.dismiss();
            SendMail sm = new SendMail(Registration.this,participant.getEmail(),"Registration Successfull for " + participant.getEvent()+":Synapse '20" ,"Hello "+ participant.getName()+ " !!\n\n            Thank You for participating in Synapse'20. At the time of event you have to show your QRcode(atteched with this mail) to the respected coordinators group on place of event.They will scan it and confirm your participation. \n\nCheers !!!\nTeam Synapse'20.",file);
            sm.execute();

        } catch (Exception e) {

            progressDialog.dismiss();
            System.out.println(e);

        }

    }


    void Authenticate()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (km.isKeyguardSecure()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent("Confirm Here!!.", "DAIICT !");
                startActivityForResult(authIntent, AUTHENTICATE);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATE) {
            if (resultCode == RESULT_OK) {

                Participant participant = new Participant(name.getText().toString(),number.getText().toString(),alt_number.getText().toString(),email.getText().toString(),institute.getText() .toString(),city.getText().toString(),teamis,total_teammembers,sharedPreferences.getString("username",""),event,"");
                Post(participant);
                Clear();

            }
            else
            {
                Toast.makeText(Registration.this,"Confirm Registration from Volunteer.",Toast.LENGTH_LONG).show();
            }
        }
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
                startActivity(new Intent(Registration.this, AboutActivity.class));
                return true;
            }

        }
        return false;
    }


    void SignOut()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
        builder.setMessage("Are You Sure ?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Registration.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
