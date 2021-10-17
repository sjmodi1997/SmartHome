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

/**
 * Main Activity class for the APP
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showPhoneStatePermission();

        final EditText firstNameTextEdit = (EditText) findViewById(R.id.firstName);
        final EditText lastNameTextEdit = (EditText) findViewById(R.id.lastName);

        Spinner gesturesButton = (Spinner) findViewById(R.id.dropDownButton);
        ArrayAdapter<String> gesturesList = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gestures));
        gesturesList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gesturesButton.setAdapter(gesturesList);

        gesturesButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Method to Take Action when user selects the gesture
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String gesture = parent.getItemAtPosition(position).toString();
                String lastName = lastNameTextEdit.getText().toString();
                // Do not process 'Select Gesture'
                if (!parent.getItemAtPosition(position).equals("Select Gesture")) {
                    Intent tutorialActivityIntent = new Intent(getApplicationContext(), TutorialActivity.class);
                    tutorialActivityIntent.putExtra("name", lastName);
                    tutorialActivityIntent.putExtra("gesture", gesture);
                    tutorialActivityIntent.putExtra("position", position);
                    tutorialActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(tutorialActivityIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Get the Permissions of the Devices
     */
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
        }

        return;
    }

    /**
     * Method to Explain the Permission Requirements
     *
     * @param heading
     * @param message
     * @param permission
     * @param permissionRequestCode
     */
    private void showExplanation(String heading,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(heading)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    /**
     * Method to request Permission from User
     * @param permissionName
     * @param permissionRequestCode
     */
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
}