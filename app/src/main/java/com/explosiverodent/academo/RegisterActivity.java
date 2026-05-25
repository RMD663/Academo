package com.explosiverodent.academo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.model.User;

public class RegisterActivity extends AppCompatActivity {

    UserDatabase userDatabase;
    private ImageView profile_image;
    private TextView import_image_text;

    private EditText user_name;
    private EditText password;

    private Button register;
    private ActivityResultLauncher<PickVisualMediaRequest> pick_media;

    private Uri select_image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        userDatabase = new UserDatabase(getApplicationContext());

        user_name = findViewById(R.id.register_user_name_input);
        password = findViewById(R.id.register_password_input);

        profile_image = findViewById(R.id.register_profile_image);
        import_image_text = findViewById(R.id.register_import_picture_text);
        register = findViewById(R.id.register_button);

        pick_media = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null){
                        select_image = uri;
                        profile_image.setImageAlpha(255);
                        profile_image.setImageURI(uri);
                    }
                });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        import_image_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openGallery(){
        pick_media.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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

    public void registerUser(){
        String register_user_name = user_name.getText().toString();
        String register_password = password.getText().toString();
        if(register_user_name.isEmpty() || register_password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_LONG).show();
        } else {
            User user = new User(0, register_user_name, 0, 0.0f, 1);

            if (select_image != null) {
                String secureUriString = saveImageToInternalStorage(select_image);
                if (secureUriString != null) {
                    user.setProfilePicture(secureUriString);
                } else {
                    Toast.makeText(this, "Failed to process profile picture.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean success = userDatabase.insertUser(user, register_password);

            if(success){
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }
    }
}