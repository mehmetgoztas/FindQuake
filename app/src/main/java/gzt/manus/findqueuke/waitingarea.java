package gzt.manus.findqueuke;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class waitingarea extends AppCompatActivity {
    private static final int RECOGNITION_REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitingarea);
        TextView userEmailTextView = findViewById(R.id.user_email_text_view);
        String userEmail = DataHolder.getInstance().getEmail();


        userEmailTextView.setText(userEmail);
        TextView matchResultTextView = findViewById(R.id.match_result_text_view);
        String location = DataHolder.getInstance().getLocation();
        String name = DataHolder.getInstance().getName();




        if (name!=null) {
            //matchResultTextView.setText(name +" "+location+" "+" Buludu yetkililerin sizinle iletişime geçmesini ebkle");
            matchResultTextView.setText("Emre Ozkan Bulunmuştur.Yetkililer sizinle iletişime geçicektir");

        } else {
            matchResultTextView.setText("Emre Ozkan bulunmuştur.Yetkililer sizinle iletişime geçicektir");
        }
    }



}