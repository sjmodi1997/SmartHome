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


public class videoActivity extends AppCompatActivity {

    private static final int VIDEO_CAPTURE = 101;
    VideoView videoView;
    Button recordButton;
    Button uploadButton;
    String gesture;
    String filepath;
    String buildName;
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        int counter= ((MyApplication) this.getApplication()).getCounter();
        videoView = (VideoView) findViewById(R.id.recordedVideoView);
        recordButton = (Button) findViewById(R.id.recordButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);

        // Check if Camera is configured
        if (!isCamera()) {
            recordButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Camera not detected", Toast.LENGTH_LONG).show();
        }

        //intent values
        gesture = getIntent().getStringExtra("gesture");
        String lastName= getIntent().getStringExtra("name");
        buildName= gesture.toUpperCase()+"_PRACTICE_"+ counter + "_" + lastName +".mp4";
        ((MyApplication) this.getApplication()).setCounter(++counter);


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRecording();
                //System.out.println("Recording Successful");
                uploadButton.setEnabled(true);
            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpldTask up1 = new UpldTask();
                //Toast.makeText(getApplicationContext(), "Starting to Upload", Toast.LENGTH_LONG).show();
                up1.execute();
                Toast.makeText(getApplicationContext(), "Video Upload Completed", Toast.LENGTH_LONG).show();

                Intent mainIntent=new Intent(videoActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    private boolean isCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    public void startRecording() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        startActivityForResult(intent, VIDEO_CAPTURE);

    }

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
            //System.out.println("the uri: "+videoUri+"--the path:"+filepath);
            Toast.makeText(getApplicationContext(), "Stored at:"+filepath, Toast.LENGTH_LONG).show();
            videoView.start();
        }
    }

    public String getAbsPathFromURI(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(id);
    }

    public class UpldTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            String UPLOAD_URL = "http://192.168.0.40:5000/uploadFiles";
            int serverResponseCode=0;
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "***";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(filepath);
            if (!sourceFile.isFile()) {
                return null;
            }

            try {
                System.out.println("Connecting with server");
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"newname\""+lineEnd);
                dos.writeBytes("Content-Type: text/plain; charset=UTF-8"+lineEnd+lineEnd);
                dos.writeBytes(buildName+lineEnd);
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\""+buildName+"\";filename="+buildName+ lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();
                Log.i("DEBUG", "Response Code :: " + serverResponseCode);
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (serverResponseCode == 200) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                            .getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                } catch (IOException ioex) {
                }
                System.out.println("Values>>"+sb.toString());
                return sb.toString();
            }else {
                return "Error in upload";
            }
        }
    }
}

