package com.explosiverodent.academo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.explosiverodent.academo.database.UserDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView usernameText;
    private FrameLayout changePictureClickZone;
    private Button btnSave;
    private Button btnCancel;

    private UserDatabase userDatabase;
    private int userId = -1;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profilePicture.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        userDatabase = new UserDatabase(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        String currentName = getIntent().getStringExtra("USER_NAME");
        String currentPicStr = getIntent().getStringExtra("USER_PICTURE");

        initViews(currentName, currentPicStr);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews(String name, String picStr) {
        profilePicture = findViewById(R.id.edit_profile_picture);
        usernameText = findViewById(R.id.edit_username_text);
        changePictureClickZone = findViewById(R.id.profile_image_container);
        btnSave = findViewById(R.id.btn_save_profile);
        btnCancel = findViewById(R.id.btn_cancel_profile);

        if (name != null) {
            usernameText.setText(name);
        }

        if (picStr != null && !picStr.isEmpty()) {
            profilePicture.setImageURI(Uri.parse(picStr));
        }

        changePictureClickZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            java.io.File file = new java.io.File(getFilesDir(), "profile_picture_" + System.currentTimeMillis() + ".jpg");

            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.OutputStream outputStream = new java.io.FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file).toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveChanges() {
        if (selectedImageUri != null) {
            String secureUriString = saveImageToInternalStorage(selectedImageUri);

            if (secureUriString == null) {
                Toast.makeText(this, "Failed to process image.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = userDatabase.updateUserProfilePicture(userId, secureUriString);

            if (success) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                Intent intentResult = new Intent();
                intentResult.putExtra("NEW_PICTURE", secureUriString);
                setResult(RESULT_OK, intentResult);

                finish();
            } else {
                Toast.makeText(this, "Failed to save to database", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
    }
}