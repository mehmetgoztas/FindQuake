package gzt.manus.findqueuke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gzt.manus.findqueuke.ui.PhotographerLoginActivity;
import gzt.manus.findqueuke.ui.VideographerLoginActivity;

public class MainActivity2 extends AppCompatActivity {

    Button storgebtn,camerabutton ,videobtn,hakkimdabtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        storgebtn = findViewById(R.id.storage);
        videobtn = findViewById(R.id.video);
        hakkimdabtn=findViewById(R.id.hakkimda);

        camerabutton = findViewById(R.id.camera);

        storgebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, PhotographerLoginActivity.class));
            }
        });
        videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, VideographerLoginActivity.class));
            }
        });

        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this,CameraActivity.class));
            }
        });
        hakkimdabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this,hakkimda.class));
            }
        });
    }
}
