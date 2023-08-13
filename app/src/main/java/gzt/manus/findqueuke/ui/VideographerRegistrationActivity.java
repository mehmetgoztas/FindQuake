package gzt.manus.findqueuke.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import gzt.manus.findqueuke.DataHolder;
import gzt.manus.findqueuke.R;
import gzt.manus.findqueuke.RecognitionActivity;
import gzt.manus.findqueuke.RegisterActivity;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class VideographerRegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    // Replace with your EditText fields
    private EditText emailEditText, passwordEditText, locationEditText;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videographer_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        usersRef = mDatabase.getReference("users2"); // Bu satırı buraya taşıyın

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);

        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerVideographer();
            }
        });
    }

    private void registerVideographer() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference usersRef = mDatabase.getReference("users");

                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("role", "videographer");
                            userData.put("location", location);
                            userData.put("email", email);
                            usersRef.child(user.getUid()).setValue(userData);
                            DataHolder.getInstance().setLocation(location);

                            Intent intent = new Intent(VideographerRegistrationActivity.this, VideographerLoginActivity.class);
                            startActivity(intent);
                            finish();
                            // Navigate to the main activity or videographer dashboard
                        } else {
                            // Display an error message
                        }
                    }
                });


    }
}

