package gzt.manus.findqueuke.face_recognition;

import android.graphics.Bitmap;
import android.graphics.RectF;
import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


/** Generic interface for interacting with different recognition engines. */
public interface FaceClassifier {


    void register(String name, Recognition recognition);

    Recognition recognizeImage(Bitmap bitmap, boolean getExtra);
    default void saveToFirebase(String name, Bitmap bitmap) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference userImageRef = storageRef.child("images/" + name + ".jpg");
        UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL
            userImageRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("imageUrl", imageUrl);
                    usersRef.push().setValue(userData);
                }
            });
        });
    }
    public class Recognition {
        private final String id;

        /** Display name for the recognition. */
        private final String title;
        // A sortable score for how good the recognition is relative to others. Lower should be better.
        private final Float distance;
        private Object embeeding;
        /** Optional location within the source image for the location of the recognized face. */
        private RectF location;
        private Bitmap crop;

        public Recognition(
                final String id, final String title, final Float distance, final RectF location) {
            this.id = id;
            this.title = title;
            this.distance = distance;
            this.location = location;
            this.embeeding = null;
            this.crop = null;
        }

        public void setEmbeeding(Object extra) {
            this.embeeding = extra;
        }
        public Object getEmbeeding() {
            return this.embeeding;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getDistance() {
            return distance;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        public void setLocation(RectF location) {
            this.location = location;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (distance != null) {
                resultString += String.format("(%.1f%%) ", distance * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }

        public void setCrop(Bitmap crop) {
            this.crop = crop;
        }

        public Bitmap getCrop() {
            return this.crop;
        }
    }


}
