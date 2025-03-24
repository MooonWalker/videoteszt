package com.example.videoteszt;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import com.example.videoteszt.databinding.ActivityMainBinding;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.concurrent.CountedCompleter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private VideoView mVideoView;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your app.
                    loadVideo();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoView);

        // Create a media controller and attach it to the VideoView
// Set the MediaController so we can use it to control video playback
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        //setContentView(R.layout.activity_main);




        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (Build.VERSION.SDK_INT >= 34) {
                    if (!(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)))
                    {
                        Boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO,

                                android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, android.Manifest.permission.ACCESS_MEDIA_LOCATION}, 138);
                    }
                }

                if (!(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)))
                {
                    Boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES);
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_MEDIA_LOCATION, android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO}, 138);
                }
            }

            if (Build.VERSION.SDK_INT >= 29)
            {
                if (!(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_MEDIA_LOCATION)))
                {
                    Boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_MEDIA_LOCATION);
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_MEDIA_LOCATION}, 138);
                }

            }

            //no authorization yet
            if (!(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)))
            {
                Boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 138);
            } else //authorization exists already
            {

            }
        }
        checkPermissionAndLoadVideo();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void checkPermissionAndLoadVideo() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                Manifest.permission.READ_MEDIA_VIDEO :
                Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            loadVideo();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(permission);
        }
    }
    private void loadVideo()
    {
        // Specify the path to the video file in the DCIM directory
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File videoFile = new File(dcimDirectory, "robot.mp4");
        String videoPath = videoFile.getAbsolutePath();

        //Uri videoUri = getVideoUriFromDCIM();
        // Set the video URI and start playback
        Uri videoUri = Uri.parse(videoPath);

        if (videoUri != null)
        {
            mVideoView.setVideoURI(videoUri);
            //mVideoView.setVideoPath(String.valueOf(videoUri));
            mVideoView.requestFocus();

            mVideoView.start();
        }
        else
        {
            // Handle the case where no video is found
            Toast.makeText(this, "No video found in DCIM", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getVideoUriFromDCIM ()
    {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        else
        {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]
                {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME
        };

        String selection = MediaStore.Video.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"%DCIM%"};

        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);

                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                );
                System.out.println("Found video: " + name + ", URI: " + contentUri);
                return contentUri;
            }
        }
        return null;
    }
}