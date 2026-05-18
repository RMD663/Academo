package com.explosiverodent.academo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    public void loginUser(){
        String _user_name = user_name_text.getText().toString();
        String _password = password_text.getText().toString();


        if(_user_name.isEmpty() || _password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty User Name or Password", Toast.LENGTH_LONG).show();
        }

        User user = null;

        user = userDatabase.validateLogin(_user_name, _password);
        System.out.println(user);


        if(user != null){
            Log.d("DATABASE TEST: ", user.getUserName());
            Toast.makeText(getApplicationContext(), "Welcome back " + user.getUserName() + "!", Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra("USER_NAME", user.getUserName());
            homeIntent.putExtra("USER_LEVEL", user.getLevel());
            homeIntent.putExtra("USER_XP", user.getXp());
            homeIntent.putExtra("USER_PICTURE", user.getProfilePictureUri());
            startActivity(homeIntent);
        }
    }
}