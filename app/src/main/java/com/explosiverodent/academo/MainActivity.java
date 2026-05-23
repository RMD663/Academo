package com.explosiverodent.academo;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    Button enter;
    Button register;
    TextView forgotPassword;

    EditText user_name_text;
    EditText password_text;
    UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userDatabase = new UserDatabase(this);

        enter = findViewById(R.id.login_button);
        register = findViewById(R.id.register_button);
        forgotPassword = findViewById(R.id.register_import_picture_text);

        user_name_text = findViewById(R.id.input_user_name);
        password_text = findViewById(R.id.input_password);

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
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
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
            Toast.makeText(getApplicationContext(), "Empty User Name or Password", Toast.LENGTH_LONG).show();
            return;
        }

        User user = null;

        user = userDatabase.validateLogin(_user_name, _password);


        if(user != null){
            Log.d("DATABASE TEST: ", user.getUserName());
            Toast.makeText(getApplicationContext(), "Welcome back " + user.getUserName() + "!", Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra("USER_NAME", user.getUserName());
            homeIntent.putExtra("USER_ID", user.getId());
            homeIntent.putExtra("USER_LEVEL", user.getLevel());
            homeIntent.putExtra("USER_XP", user.getXp());
            homeIntent.putExtra("USER_PICTURE", user.getProfilePicture());
            startActivity(homeIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
        }
    }
}