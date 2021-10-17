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

public class SecondActivity extends AppCompatActivity {
    Button p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent get_previous_1= getIntent();
        final String g= get_previous_1.getStringExtra("gesture");
        final String last_name= get_previous_1.getStringExtra("name");
        final int position= get_previous_1.getIntExtra("position",0);

        String sign = null;

        if(g.equals("Num0")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture0;
        }
        else if(g.equals("Num1")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture1;
        }
        else if(g.equals("Num2")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture2;
        }
        else if(g.equals("Num3")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture3;
        }
        else if(g.equals("Num4")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture4;
        }
        else if(g.equals("Num5")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture5;
        }
        else if(g.equals("Num6")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture6;
        }
        else if(g.equals("Num7")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture7;
        }
        else if(g.equals("Num8")){
            sign= "android.resource://" + getPackageName() + "/" + R.raw.gesture8;
        }
        else if(g.equals("Num9")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.gesture9;
        }
        else if(g.equals("LightOn")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.lighton;
        }
        else if(g.equals("LightOff")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.lightoff;
        }
        else if(g.equals("FanOn")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.fanon;
        }
        else if(g.equals("FanOff")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.fanoff;
        }
        else if(g.equals("FanUp")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.increasefanspeed;
        }
        else if(g.equals("FanDown")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.decreasefanspeed;
        }
        else if(g.equals("SetThermo")){
            sign= "android.resource://" + getPackageName() + "/" +R.raw.setthermo;
        }

        VideoView video= (VideoView) findViewById(R.id.gestureVideoView);
        Uri vid= Uri.parse(sign);
        video.setVideoURI(vid);
        video.start();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        p= (Button) findViewById(R.id.practiceButton);

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( ContextCompat.checkSelfPermission(SecondActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(SecondActivity.this,
                            Manifest.permission.CAMERA)) {
                    } else {
                        ActivityCompat.requestPermissions(SecondActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                101);
                    }
                }

                else
                {
                    Intent startIntent= new Intent(getApplicationContext(), VideoActivity.class);
                    System.out.println(g);
                    startIntent.putExtra("gesture", g);
                    startIntent.putExtra("name", last_name);
                    startIntent.putExtra("position", position);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(startIntent);
                }
            }
        });
    }
}
