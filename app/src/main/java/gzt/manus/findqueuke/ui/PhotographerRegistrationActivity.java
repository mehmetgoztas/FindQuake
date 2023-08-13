package gzt.manus.findqueuke.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import gzt.manus.findqueuke.DataHolder;
import gzt.manus.findqueuke.RecognitionActivity;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import gzt.manus.findqueuke.R;

public class PhotographerRegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private EditText emailEditText, passwordEditText, ageEditText;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        usersRef = mDatabase.getReference("users");

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);

        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPhotographer();
            }
        });
    }

    private void registerPhotographer() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        final String age = ageEditText.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference usersRef = mDatabase.getReference("users");

                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("role", "photographer");
                            userData.put("age", age);
                            userData.put("email", email);
                            usersRef.child(user.getUid()).setValue(userData);
                            DataHolder.getInstance().setEmail(email);



                            Intent intent = new Intent(PhotographerRegistrationActivity.this, PhotographerLoginActivity.class);
                            startActivity(intent);
                            finish();
                            new UploadDataTask(user.getUid(), userData.toString()).execute();
                            DataHolder.getInstance().setEmail(email);


                        } else {
                            // Display an error messages
                        }
                    }

                });



    }



    private static class UploadDataTask extends AsyncTask<Void, Void, Void> {

        private String uid;
        private String data;

        UploadDataTask(String uid, String data) {
            this.uid = uid;
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String endPoint = "obs.ap-southeast-1.myhuaweicloud.com";
            String ak = "CBX822FEK9Z8SR0BZRN1";
            String sk = "MAOviiUOHOUAeE5lM2nQWjaBD89mrpaGBbmOfMtE";
            String bucketName = "findquake";
            String objectKey = "users/" + uid;
            String contentType = "application/json";

            ObsConfiguration config = new ObsConfiguration();
            config.setEndPoint(endPoint);

            ObsClient obsClient = new ObsClient(ak, sk, config);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, inputStream);
            request.setMetadata(metadata);

            try {
                obsClient.putObject(request);
            } catch (Exception e) {
                // Handle exception while uploading data to Huawei OBS
                e.printStackTrace();
            } finally {
                if (obsClient != null) {
                    try {
                        obsClient.close();
                    } catch (IOException e) {
                        // Handle exception while closing the ObsClient
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // You can update UI here if necessary.
        }
    }
}

