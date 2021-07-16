package com.example.dogbook.ComposeActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogbook.MainActivity.Models.Post;
import com.example.dogbook.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeActivity extends AppCompatActivity {

    private static final String APP_TAG = "Dogbook";
    private static final String TAG = "ComposeActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 123;
    private static final String photoFileName = "image.jpg";

    private File photoFile;

    private Toolbar toolbar;
    private Button btnPost;
    private ImageView ivProfilePicture;
    private ImageView ivPostPicture;
    private TextView tvUsername;
    private TextView tvOwner;
    private EditText etCaption;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        initView();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, String.valueOf(tab.getPosition()));
                if (tab.getPosition() == 0){
                    launchCamera();
                    return;
                }
                if (tab.getPosition() == 1) {
                    Log.i(TAG, "Current location selected");
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    launchCamera();
                    return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPost = findViewById(R.id.btnPost);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivPostPicture = findViewById(R.id.ivPostPicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvOwner = findViewById(R.id.tvOwner);
        etCaption = findViewById(R.id.etCaption);
        tabLayout = findViewById(R.id.tabLayout);
        updateUserData();

    }

    private void updateUserData() {
        ParseUser user = ParseUser.getCurrentUser();
        user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    bindUserData(user);
                }
                else {
                    Toast.makeText(ComposeActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void bindUserData(ParseUser user) {
        ParseFile profilePicture = user.getParseFile("profilePicture");
        if (profilePicture != null) {
            Glide.with(ComposeActivity.this).load(profilePicture.getUrl()).into(ivProfilePicture);
        }
        tvUsername.setText(user.getUsername());
        tvOwner.setText(String.format("from %s", user.getString("ownerName")));
    }

    private void uploadPost() {
        if (etCaption.getText().toString().isEmpty()) {
            Toast.makeText(this, "Post caption cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Post post = new Post();
        post.setAuthor(ParseUser.getCurrentUser());
        post.setDescription(etCaption.getText().toString());
        if (photoFile != null) {
            post.setPhoto(new ParseFile(photoFile));
        }
        //TODO: add indicator of "loading"
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(ComposeActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void launchCamera() {
        //Intent to image capturing (camera)
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Create file reference where the taken picture will be saved
        photoFile = getPhotoFileUri(photoFileName);
        //Provider security measure
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.dogbook.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private File getPhotoFileUri(String photoFileName) {
        //Create a path to a directory specific of the app
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        //If directory does not exist, tries to make the directory, if fails, logs a message
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "Failed to create app directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivPostPicture.setImageBitmap(takenImage);
            return;
        }
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}