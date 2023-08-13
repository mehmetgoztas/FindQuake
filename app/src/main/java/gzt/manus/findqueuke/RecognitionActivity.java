package gzt.manus.findqueuke;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import gzt.manus.findqueuke.face_recognition.FaceClassifier;
import gzt.manus.findqueuke.face_recognition.TFLiteFaceRecognition;


public class RecognitionActivity extends AppCompatActivity {

    String email = DataHolder.getInstance().getEmail();
    String location = DataHolder.getInstance().getLocation();
    CardView galleryCard, cameraCard;
    ImageView imageView;
    Uri image_uri;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    public static final int PERMISSION_CODE = 100;
    private static final int REQUEST_CODE = 1;

    //TODO declare face detector

    // High-accuracy landmark detection and face classification
    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();
    FaceDetector detector;

    //TODO declare face recognizer
    FaceClassifier faceClassifier;

    //TODO get the image from gallery and display it

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri = result.getData().getData();
                        Bitmap inputImage = uriToBitmap(image_uri);
                        Bitmap rotated = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotated);
                        performFaceDetection(rotated);
                        Toast.makeText(RecognitionActivity.this, "Basarili", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    //TODO capture the image using camera and display it
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap inputImage = uriToBitmap(image_uri);
                        Bitmap rotated = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotated);
                        performFaceDetection(rotated);
                        Toast.makeText(RecognitionActivity.this, "Basarili", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageReference = FirebaseStorage.getInstance().getReference("images");
        mFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_recognition);
        fetchFacesAndNames();


        //TODO handling permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }



        //TODO initialize views
        galleryCard = findViewById(R.id.gallerycard);
        cameraCard = findViewById(R.id.cameracard);
        imageView = findViewById(R.id.imageView2);

        //TODO code for choosing images from gallery
        galleryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });

        //TODO code for capturing images using camera
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });

        //TODO initialize face detector

        detector = FaceDetection.getClient(highAccuracyOpts);

        //TODO initialize face recognition model
        try {
            faceClassifier = TFLiteFaceRecognition.create(getAssets(),"facenet.tflite",160,false);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    //TODO opens camera so that user can capture image
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    //TODO takes URI of the image and returns bitmap
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO rotate image if image captured on samsung devices
    //TODO Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input) {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(image_uri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Log.d("tryOrientation", orientation + "");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

    //TODO perform face detection
    Canvas canvas;
    public void performFaceDetection(Bitmap input) {
        Bitmap mutableBmp = input.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBmp);
        InputImage image = InputImage.fromBitmap(input, 0);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        // Task completed successfully
                                        // ...
                                        //     Toast.makeText(RegisterActivity.this, "tryface" + "" + faces.size(), Toast.LENGTH_SHORT).show();
                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            Paint p1 = new Paint();
                                            p1.setColor(Color.RED);
                                            p1.setStyle(Paint.Style.STROKE);
                                            p1.setStrokeWidth(5);
                                            performFaceRecognition(bounds, input);
                                            canvas.drawRect(bounds, p1);
                                        }
                                        imageView.setImageBitmap(mutableBmp);
                                        //Burayı kontrol et!!!!!!!!
                                        Toast.makeText(RecognitionActivity.this, "Kişi Bulunmuştur Yetkililere mail Gönderildi", Toast.LENGTH_SHORT).show();

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

    //TODO perform face recognition

    public void performFaceRecognition(Rect bound,Bitmap input)
    {
        if (bound.top<0)
        {
            bound.top = 0;
        }
        if (bound.left<0)
        {
            bound.left = 0;
        }
        if (bound.right>input.getWidth())
        {
            bound.right = input.getWidth()-1;
        }
        if (bound.bottom>input.getHeight())
        {
            bound.bottom = input.getHeight()-1;
        }
        Bitmap croppedFace = Bitmap.createBitmap(input,bound.left,bound.top,bound.width(),bound.height());
        imageView.setImageBitmap(croppedFace);
        croppedFace = Bitmap.createScaledBitmap(croppedFace,160,160,false);
        FaceClassifier.Recognition recognition = faceClassifier.recognizeImage(croppedFace,true);
        if(recognition!=null)
        {
            Log.d("tryFR",recognition.getTitle()+""+recognition.getDistance());
            if(recognition.getDistance()<1)
            {
                Paint p1 = new Paint();
                p1.setColor(Color.BLACK);
                //p1.setStyle(Paint.Style.STROKE);
                p1.setTextSize(90);
                canvas.drawText(recognition.getTitle(),bound.left,bound.top,p1);
                String name =recognition.getTitle();
                DataHolder.getInstance().setName(name);



                // sendPostRequest() metodunu çağırırken location ve email değerlerini aktar
                sendPostRequest(name);




            }

        }
    }

    private void fetchFacesAndNames() {
        mFirestore.collection("images/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        String imageUrl = document.getString("image_url");
                        downloadImage(imageUrl, new OnSuccessListener<Bitmap>() {
                            @Override
                            public void onSuccess(Bitmap faceImage) {
                                trainFaceRecognitionModel(name, faceImage);
                            }
                        });
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadImage(String imageUrl, final OnSuccessListener<Bitmap> onSuccess) {
        StorageReference imageRef = mStorageReference.child(imageUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                onSuccess.onSuccess(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Error downloading image", exception);
            }
        });
    }

    private void trainFaceRecognitionModel(String name, Bitmap faceImage) {

        faceImage = Bitmap.createScaledBitmap(faceImage, 160, 160, false);
        FaceClassifier.Recognition recognition = faceClassifier.recognizeImage(faceImage, true);
        faceClassifier.register(name, recognition);
    }
    private void sendResultToRegisterActivity(String name, boolean isMatched) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("matched_name", name);
        resultIntent.putExtra("is_matched", isMatched);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


//    private void sendPostRequest() {
//        String url = "http://9cca628b0fa2453cadd82e9434f505ce.apic.ap-southeast-1.huaweicloudapis.com/";
//        String postData = "location";
//
//        SendPostRequestTask task = new SendPostRequestTask();
//        task.execute(url, postData);
//    }
private void sendPostRequest(String name) {
    String email = DataHolder.getInstance().getEmail();
    String location = DataHolder.getInstance().getLocation();

    String url = "http://4a54602d95c943e7b220b8059a982212.apic.ap-southeast-1.huaweicloudapis.com/";
    String postData = "email=" + email + "&location=" + location + "&name=" + name;
    SendPostRequestTask task = new SendPostRequestTask();
    task.execute(url, postData);
}





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

