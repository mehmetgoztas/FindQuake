package gzt.manus.findqueuke;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gzt.manus.findqueuke.face_recognition.FaceClassifier;
import gzt.manus.findqueuke.face_recognition.TFLiteFaceRecognition;

public class registervideo extends AppCompatActivity {
    private final Random random = new Random();
    private final List<Integer> colors = new ArrayList<>();

    private VideoView videoView;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button selectVideoButton;
    private Uri videoUri;

    private FaceDetector detector;

    private FaceClassifier faceClassifier; // Bu satırı sınıfın başında ekleyin

    private final FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();

    private final ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        videoUri = result.getData().getData();
                        videoView.setVideoURI(videoUri);
                        videoView.start();
                        processVideo(videoUri);
                        Toast.makeText(registervideo.this, "Video selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registervideo2);

        videoView = findViewById(R.id.videoView);
        surfaceView = findViewById(R.id.surfaceView);
        selectVideoButton = findViewById(R.id.select_video_button);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceView.setZOrderOnTop(true);

        detector = FaceDetection.getClient(highAccuracyOpts);

        // FaceClassifier örneği oluşturun (model dosyanızın adını ve etiket dosyanızın adını belirtin)
        try {
            faceClassifier = TFLiteFaceRecognition.create(getAssets(),"facenet.tflite",160,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                videoActivityResultLauncher.launch(videoIntent);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    private void processVideo(Uri videoUri) {
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

                int frameIntervalMs = 500; // Örneklem süresini ayarlayarak daha iyi sonuçlar elde edebilirsiniz

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(registervideo.this, videoUri);
                int videoLengthInMs = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                for (int i = 0; i < videoLengthInMs; i += frameIntervalMs) {
                    final Bitmap frame = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                    if (frame != null) {
                        Runnable faceDetectionRunnable = new Runnable() {
                            @Override
                            public void run() {
                                performFaceDetection(frame);
                            }
                        };
                        new Thread(faceDetectionRunnable).start();
                    }
                }

                try {
                    retriever.release();
                } catch (IOException ex) {
                    // handle the exception here
                }
            }
        });
        videoView.start();
    }

    public void performFaceDetection(Bitmap input) {
        InputImage image = InputImage.fromBitmap(input, 0);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Toast.makeText(registervideo.this, "Kişi Bulunmuştur Yetkililere mail Gönderildi", Toast.LENGTH_SHORT).show();
                                        if (!faces.isEmpty()) {
                                            List<RectF> faceBounds = new ArrayList<>();
                                            for (Face face : faces) {
                                                Rect bounds = face.getBoundingBox();
                                                RectF boundsF = new RectF(bounds);
                                                faceBounds.add(boundsF);
                                            }
                                            performFaceRecognition(faceBounds, input);
                                        } else {
                                            drawOnSurfaceView(new ArrayList<>(), input, new ArrayList<>());
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(registervideo.this, "Yüz Tespit edilemedi", Toast.LENGTH_SHORT).show();
                                    }
                                });
    }

    public void performFaceRecognition(List<RectF> faceBounds, Bitmap input) {
        List<RectF> faceRectangles = new ArrayList<>();
        List<String> faceNames = new ArrayList<>();

        for (RectF bound : faceBounds) {
            Bitmap croppedFace = Bitmap.createBitmap(input, (int) bound.left, (int) bound.top, (int) bound.width(), (int) bound.height());
            croppedFace = Bitmap.createScaledBitmap(croppedFace, 160, 160, false);
            FaceClassifier.Recognition recognition = faceClassifier.recognizeImage(croppedFace, false);
            String name;
            if (recognition != null && recognition.getDistance() < 1) {
                name = recognition.getTitle();
                sendPostRequest(name);


            } else {
                name = "Unknown";
            }
            faceNames.add(name);

            float videoWidth = videoView.getMeasuredWidth();
            float videoHeight = videoView.getMeasuredHeight();
            float frameWidth = input.getWidth();
            float frameHeight = input.getHeight();
            float scaleX = videoWidth / frameWidth;
            float scaleY = videoHeight / frameHeight;

            float left = bound.left * scaleX;
            float top = bound.top * scaleY;
            float right = bound.right * scaleX;
            float bottom = bound.bottom * scaleY;
            final RectF rectF = new RectF(left, top, right, bottom);
            faceRectangles.add(rectF);
        }

        drawOnSurfaceView(faceRectangles, input, faceNames);
    }

    private void drawOnSurfaceView(List<RectF> faceRectangles, Bitmap input, List<String> faceNames) {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            for (int i = 0; i < faceRectangles.size(); i++) {
                RectF rectF = faceRectangles.get(i);
                Paint paint = new Paint();

                if (colors.size() <= i) {
                    int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                    colors.add(color);
                }

                paint.setColor(colors.get(i));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);

                canvas.drawRect(rectF, paint);

                // Draw face name
                paint.setTextSize(40);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(faceNames.get(i), rectF.left, rectF.top - 10, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    private void uploadVideo(Uri videoUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference videoRef = storageRef.child("videos/" + videoUri.getLastPathSegment());
        UploadTask uploadTask = videoRef.putFile(videoUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Video yükleme başarılı
                Toast.makeText(registervideo.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Video yükleme başarısız
                Toast.makeText(registervideo.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPostRequest(String name) {
        String email = DataHolder.getInstance().getEmail();
        String location = DataHolder.getInstance().getLocation();

        String url = "https://4a54602d95c943e7b220b8059a982212.apic.ap-southeast-1.huaweicloudapis.com/";
        String postData = "email=" + email + "&location=" + location + "&name=" + name;
        SendPostRequestTask task = new SendPostRequestTask();
        task.execute(url, postData);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.close();
    }
}


