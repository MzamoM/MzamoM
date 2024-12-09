package com.sos.msgroup.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sos.msgroup.R;
import com.sos.msgroup.RegisterActivity;


public class LocationDisclosureActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "SecurifyPrefs";
    public static final String firstTimeUser = "used";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Button buttonAllow, buttonDeny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_location_disclosure);


        buttonAllow = findViewById(R.id.btnDisclosureAllow);
        buttonDeny = findViewById(R.id.btnDisclosureDeny);

        buttonAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();
                editor.putBoolean(firstTimeUser, true);
                editor.commit();
                editor.apply();


                if (checkPermission()) {
                    startActivity(new Intent(LocationDisclosureActivity.this, RegisterActivity.class));
                    finish();
                } else {
                    requestPermission();
                }

            }
        });

        buttonDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(LocationDisclosureActivity.this).setTitle("Confirm Exit").setMessage("This selected choose will force app to exit?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }).setNegativeButton("No", null).show();

            }
        });
    }

    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LocationDisclosureActivity.this, RegisterActivity.class));
                finish();
                // main logic
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel("You need to allow access permissions", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LocationDisclosureActivity.this).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }
}