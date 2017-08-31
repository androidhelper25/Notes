package com.example.sarthak.notes.firebasemanager;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.sarthak.notes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUploadDataManager {

    Context mContext;

    public FirebaseUploadDataManager(Context context) {

        this.mContext = context;
    }

    /**
     * Upload Notes image to firebase storage and add Uri to firebase database
     *
     * @param notesDatabase is the firebase database reference
     * @param imageStorage is the firebase storage reference
     * @param notesImageUri is the image Uri
     */
    public void uploadImageToFirebase(final DatabaseReference notesDatabase, StorageReference imageStorage, Uri notesImageUri) {

        imageStorage.putFile(notesImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {

                    @SuppressWarnings("VisibleForTests") Uri Url = task.getResult().getDownloadUrl();
                    if (Url != null) {

                        // pass image Uri to 'imageMap'
                        Map imageMap = new HashMap<>();
                        imageMap.put("imageUri", Url.toString());

                        // set 'imageMap' to firebase database
                        notesDatabase.updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (!task.isSuccessful()) {

                                    if (mContext != null) {

                                        Toast.makeText(mContext, R.string.upload_image_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
