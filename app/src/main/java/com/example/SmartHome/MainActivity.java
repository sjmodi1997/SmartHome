package com.example.SmartHome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class  MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showPhoneStatePermission();

        Spinner gestures= (Spinner) findViewById(R.id.dropDownButton);

        ArrayAdapter<String> gestures_list= new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gestures));

        gestures_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gestures.setAdapter(gestures_list);

        final EditText firstNameTextEdit= (EditText) findViewById(R.id.firstName);
        final EditText lastNameTextEdit= (EditText) findViewById(R.id.lastName);

        gestures.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("Select Gesture")){

                } else
                {
                    String gesture = parent.getItemAtPosition(position).toString();
                    String lastName = lastNameTextEdit.getText().toString();
                    Intent startIntent= new Intent(getApplicationContext(), SecondActivity.class);
                    startIntent.putExtra("name", lastName);
                    startIntent.putExtra("gesture", gesture);
                    startIntent.putExtra("position", position);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(startIntent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
            }
        } else {

        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
    }