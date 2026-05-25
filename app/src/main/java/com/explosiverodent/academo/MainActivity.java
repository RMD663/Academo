package com.explosiverodent.academo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button enter;
    Button register;
    TextView forgotPassword;

    EditText user_name_text;
    EditText password_text;
    UserDatabase userDatabase;

    TextInputLayout layoutUser;
    TextInputLayout layoutPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabase = new UserDatabase(this);

        SharedPreferences s = getSharedPreferences("refs", MODE_PRIVATE);
        long dateLoggedMilis = s.getLong("LOGIN_DATE", 0);
        boolean logged = s.getBoolean("LOGGED", false);
        long actualDate = System.currentTimeMillis();
        long tenDaysMili = TimeUnit.DAYS.toMillis(10);
        long days = actualDate - dateLoggedMilis;
        if (dateLoggedMilis == 0 || days >= tenDaysMili) {
            logged = false;
        }
        if (logged) {
            String savedUser = s.getString("USER", "");
            String savedPass = s.getString("PASS", "");

            User user = userDatabase.validateLogin(savedUser, savedPass);

            if (user != null) {
                Intent homeIntent = new Intent(this, HomeActivity.class);
                homeIntent.putExtra("USER_NAME", user.getUserName());
                homeIntent.putExtra("USER_ID", user.getId());
                homeIntent.putExtra("USER_LEVEL", user.getLevel());
                homeIntent.putExtra("USER_XP", user.getXp());
                homeIntent.putExtra("USER_PICTURE", user.getProfilePicture());
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
                return;
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userDatabase = new UserDatabase(this);

        enter = findViewById(R.id.login_button);
        register = findViewById(R.id.register_button);
        forgotPassword = findViewById(R.id.register_import_picture_text);

        user_name_text = findViewById(R.id.input_user_name);
        password_text = findViewById(R.id.input_password);

        layoutUser = findViewById(R.id.login_user_layout);
        layoutPass = findViewById(R.id.login_password_layout);

        String justRegistered = getIntent().getStringExtra("JUST_REGISTERED_USER");

        if (justRegistered != null && !justRegistered.isEmpty()) {
            user_name_text.setText(justRegistered);
            password_text.setText("");
            password_text.requestFocus();
        } else {
            String u = s.getString("USER", "");
            String p = s.getString("PASS", "");
            user_name_text.setText(u);
            password_text.setText(p);
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordActivity();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void registerUser(){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void openForgotPasswordActivity() {
        Intent forgotIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotIntent);
    }

    public void loginUser(){
        String _user_name = user_name_text.getText().toString();
        String _password = password_text.getText().toString();


        if(_user_name.isEmpty() || _password.isEmpty()){
            if (_user_name.isEmpty()){
                layoutUser.setError("Campo Obrigatório");
                user_name_text.requestFocus();
            }else {
                layoutUser.setError(null);
            }

            if (_password.isEmpty()){
                layoutPass.setError("Campo Obrigatório");
                password_text.requestFocus();
            }else {
                layoutPass.setError(null);
            }
            return;
        }

        User user = null;

        user = userDatabase.validateLogin(_user_name, _password);

        if(user != null){
            SharedPreferences s = getSharedPreferences("refs", MODE_PRIVATE);
            SharedPreferences.Editor edit = s.edit();

            long actualDate = System.currentTimeMillis();

            edit.putLong("LOGIN_DATE", actualDate);
            edit.putString("USER", _user_name);
            edit.putString("PASS", _password);
            edit.putBoolean("LOGGED", true);
            edit.apply();

            Toast.makeText(getApplicationContext(), "Welcome back " + user.getUserName() + "!", Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra("USER_NAME", user.getUserName());
            homeIntent.putExtra("USER_ID", user.getId());
            homeIntent.putExtra("USER_LEVEL", user.getLevel());
            homeIntent.putExtra("USER_XP", user.getXp());
            homeIntent.putExtra("USER_PICTURE", user.getProfilePicture());
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
        }
    }
}