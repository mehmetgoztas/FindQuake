package gzt.manus.findqueuke;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    String email = "ali@gmail.com";
    String location ="mersin";
    String  name= "mehmet";
    private static int SPLASH_TIME_OUT = 5000;
    //Hooks

    TextView slogan;
    //Animations
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sendPostRequest();

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Hooks
        //a = findViewById(R.id.a);
        slogan = findViewById(R.id.tagLine);
        //Animation Calls

        //a.setAnimation(middleAnimation);
        //Splash Screen Code to call new Activity after some time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,  MainActivity2.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
    private void sendPostRequest() {
        String url = "http://dd6b2db7c25a43f7b7b4130bf86bdb51.apic.ap-southeast-1.huaweicloudapis.com/";
        SendPostRequestTask task = new SendPostRequestTask();
        task.execute(url, email, location, name);
    }


}