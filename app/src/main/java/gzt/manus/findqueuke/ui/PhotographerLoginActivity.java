package gzt.manus.findqueuke.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import gzt.manus.findqueuke.MainActivity2;
import gzt.manus.findqueuke.R;
import gzt.manus.findqueuke.RegisterActivity;

public class PhotographerLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // Replace with your EditText fields
    private EditText emailEditText, passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotographerLoginActivity.this, PhotographerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInPhotographer();
            }
        });
    }


    private void signInPhotographer() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(PhotographerLoginActivity.this, RegisterActivity.class);
                            intent.putExtra("email", email); // Add this line
                            startActivity(intent);

                        } else {
                            Toast.makeText(PhotographerLoginActivity.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

}