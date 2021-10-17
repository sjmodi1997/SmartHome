package com.example.SmartHome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Class for TutorialActivity
 */
public class TutorialActivity extends AppCompatActivity {
    Button practiceButton;
    private static final String androidResourceStr = "android.resource://";
    private static final String SLASH = "/";
    /**
     * get the VideoPath of tutorial from GestureName
     * @param gestureName
     * @return videoPathStr
     */
    private String getVideoURI(String gestureName){
        String videoUriStr = "";
        if (gestureName.equals("Num0")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture0;
        } else if (gestureName.equals("Num1")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture1;
        } else if (gestureName.equals("Num2")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture2;
        } else if (gestureName.equals("Num3")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture3;
        } else if (gestureName.equals("Num4")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture4;
        } else if (gestureName.equals("Num5")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture5;
        } else if (gestureName.equals("Num6")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture6;
        } else if (gestureName.equals("Num7")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture7;
        } else if (gestureName.equals("Num8")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture8;
        } else if (gestureName.equals("Num9")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.gesture9;
        } else if (gestureName.equals("LightOn")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.lighton;
        } else if (gestureName.equals("LightOff")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.lightoff;
        } else if (gestureName.equals("FanOn")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.fanon;
        } else if (gestureName.equals("FanOff")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.fanoff;
        } else if (gestureName.equals("FanUp")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.increasefanspeed;
        } else if (gestureName.equals("FanDown")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.decreasefanspeed;
        } else if (gestureName.equals("SetThermo")) {
            videoUriStr = androidResourceStr + getPackageName() + SLASH + R.raw.setthermo;
        }
        return videoUriStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent varsFromPreviousIntent = getIntent();
        final String gestureName = varsFromPreviousIntent.getStringExtra("gesture");
        final String lastName = varsFromPreviousIntent.getStringExtra("name");
        final int position = varsFromPreviousIntent.getIntExtra("position", 0);

        String videoUriStr = getVideoURI(gestureName);

        VideoView video = (VideoView) findViewById(R.id.gestureVideoView);
        Uri vidUri = Uri.parse(videoUriStr);
        video.setVideoURI(vidUri);
        video.start();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        practiceButton = (Button) findViewById(R.id.practiceButton);
        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TutorialActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TutorialActivity.this,
                            Manifest.permission.CAMERA)) {
                    } else {
                        ActivityCompat.requestPermissions(TutorialActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                101);
                    }
                } else {
                    Intent startIntent = new Intent(getApplicationContext(), VideoActivity.class);
                    System.out.println(gestureName);
                    startIntent.putExtra("gesture", gestureName);
                    startIntent.putExtra("name", lastName);
                    startIntent.putExtra("position", position);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(startIntent);
                }
            }
        });
    }
}
