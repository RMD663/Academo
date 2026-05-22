package com.explosiverodent.academo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.explosiverodent.academo.database.UserDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputNewPassword;
    private Button btnVerifyUser;
    private Button btnUpdatePassword;
    private LinearLayout containerNewPassword;

    private UserDatabase userDatabase;
    private int verifiedUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        userDatabase = new UserDatabase(this);

        inputUsername = findViewById(R.id.forgot_username_input);
        inputNewPassword = findViewById(R.id.forgot_new_password_input);
        btnVerifyUser = findViewById(R.id.btn_verify_user);
        btnUpdatePassword = findViewById(R.id.btn_update_password);
        containerNewPassword = findViewById(R.id.container_new_password);

        btnVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUsername();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_forgot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void verifyUsername() {
        String username = inputUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        verifiedUserId = userDatabase.getUserIdByUsername(username);

        if (verifiedUserId != -1) {
            Toast.makeText(this, "User verified! Enter your new password.", Toast.LENGTH_SHORT).show();
            inputUsername.setEnabled(false);
            btnVerifyUser.setVisibility(View.GONE);
            containerNewPassword.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePassword() {
        String newPassword = inputNewPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = userDatabase.updateUserPassword(verifiedUserId, newPassword);

        if (success) {
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating password. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}