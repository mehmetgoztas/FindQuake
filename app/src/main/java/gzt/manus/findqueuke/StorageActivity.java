package gzt.manus.findqueuke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import gzt.manus.findqueuke.face_recognition.FaceClassifier;

public class StorageActivity extends AppCompatActivity {

    public static HashMap<String, FaceClassifier.Recognition> registered = new HashMap<>();


    Button recognizeBtn_photo,recognizeBtn_video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        recognizeBtn_photo = findViewById(R.id.buttonregister);
        recognizeBtn_video = findViewById(R.id.buttonrecognize);

        recognizeBtn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StorageActivity.this,RecognitionActivity.class));
            }
        });

        recognizeBtn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StorageActivity.this,registervideo.class));
            }
        });
    }
}