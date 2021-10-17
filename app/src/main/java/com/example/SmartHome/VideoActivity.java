package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A Class for Video Activity
 */
public class VideoActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 101;
    VideoView videoView;
    Button recordButton;
    Button uploadButton;
    String gesture;
    String filepath;
    String buildName;
    Uri videoUri;

    /**
     * Main Method for the Class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        int counter = ((MyApplication) this.getApplication()).getCounter();
        videoView = (VideoView) findViewById(R.id.recordedVideoView);
        recordButton = (Button) findViewById(R.id.recordButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);

        // Check if Camera is configured
        if (!checkCamera()) {
            recordButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Camera not Found", Toast.LENGTH_LONG).show();
        }

        //intent values
        gesture = getIntent().getStringExtra("gesture");
        String lastName = getIntent().getStringExtra("name");
        buildName = gesture.toUpperCase() + "_PRACTICE_" + counter + "_" + lastName + ".mp4";
        ((MyApplication) this.getApplication()).setCounter(++counter);

        // Set Listener for Record Button
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
                uploadButton.setEnabled(true);
            }
        });

        // Upload Button Listener
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadTask uploadTaskObj = new UploadTask();
                uploadTaskObj.execute();
                Toast.makeText(getApplicationContext(), "Video Upload Completed", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(VideoActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    /**
     * Check if Device has Camera
     * @return true if Device has Camera
     * @return false else
     */
    private boolean checkCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            return true;
        return false;
    }

    /**
     * Method to Start Recording
     */
    public void startRecording() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    /**
     * Method to Execute Main Activity
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            filepath = getAbsPathFromURI(videoUri);
            Toast.makeText(getApplicationContext(), "Stored at:" + filepath, Toast.LENGTH_LONG).show();
            videoView.start();
        }
    }

    /**
     * Method to get the Abs Path from Video URI
     * @param uri
     * @return filePath
     */
    public String getAbsPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(id);
    }

    /**
     * Class to Upload Video to Server
     */
    public class UploadTask extends AsyncTask<String, String, String> {
        /**
         * Method to Call API and Upload the Video
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            // URL of Server
            // Server should have implemented Upload-file API
            String UPLOAD_URL = "http://192.168.0.40:5000/uploadFiles";
            int serverResponseCode = 0;
            HttpURLConnection connection = null;
            DataOutputStream dataStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "***";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            // Check if File Exists
            File sourceFile = new File(filepath);
            if (!sourceFile.isFile()) {
                Log.i("DEBUG", "File not exists");
                return null;
            }

            try {
                System.out.println("Connecting with server");
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dataStream = new DataOutputStream(connection.getOutputStream());
                dataStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataStream.writeBytes("Content-Disposition: form-data; name=\"newname\"" + lineEnd);
                dataStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd + lineEnd);
                dataStream.writeBytes(buildName + lineEnd);
                dataStream.writeBytes(lineEnd);

                dataStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataStream.writeBytes("Content-Disposition: form-data; name=\"" + buildName + "\";filename=" + buildName + lineEnd);
                dataStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dataStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dataStream.writeBytes(lineEnd);
                dataStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                Log.i("DEBUG", "Response Code :: " + serverResponseCode);
                // Closing the Connections
                fileInputStream.close();
                dataStream.flush();
                dataStream.close();
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (serverResponseCode == 200) {
                StringBuilder sb = new StringBuilder();
                try
                {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection
                            .getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
                return sb.toString();
            } else {
                return "Error in upload";
            }
        }
    }
}

