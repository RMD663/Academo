package com.explosiverodent.academo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explosiverodent.academo.model.Level;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.explosiverodent.academo.adapter.LevelAdapter;
import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.model.User;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView userNameText;
    private TextView levelText;
    private ProgressBar levelProgress;
    private BottomNavigationView bottomNavigationView;

    private UserDatabase userDatabase;
    private int userId = -1;


    private SharedPreferences s;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadUserData();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        s = getSharedPreferences("refs", MODE_PRIVATE);
        userDatabase = new UserDatabase(this);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                    return insets;
                }
        );

        userId = getIntent().getIntExtra("USER_ID", -1);

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void initViews() {
        profilePicture = findViewById(R.id.profile_picture);
        userNameText = findViewById(R.id.user_name_text);
        levelText = findViewById(R.id.level_text);
        levelProgress = findViewById(R.id.level_progress);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = userDatabase.getUserById(userId);
                String currentName = (user != null) ? user.getUserName() : "";
                String currentPic = (user != null) ? user.getProfilePicture() : "";

                Intent editProfileIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("USER_ID", userId);
                editProfileIntent.putExtra("USER_NAME", currentName);
                editProfileIntent.putExtra("USER_PICTURE", currentPic);

                editProfileLauncher.launch(editProfileIntent);
            }
        });
    }

    private void loadUserData() {
        if (userId != -1) {
            User user = userDatabase.getUserById(userId);
            if (user != null) {
                String name = user.getUserName();
                int level = user.getLevel();
                float xp = user.getXp();
                String currentPicUriString = user.getProfilePicture();

                if (name != null) userNameText.setText(name);
                levelText.setText("LV " + level);

                int xpRequired = level * 100;
                levelProgress.setMax(xpRequired);
                levelProgress.setProgress((int) xp);

                if (currentPicUriString != null && !currentPicUriString.isEmpty()) {
                    try {
                        profilePicture.setImageURI(null);
                        profilePicture.setImageURI(Uri.parse(currentPicUriString));
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        List<Level> levelList = new ArrayList<>();
        levelList.add(new Level(1, "Forest", "Easy", R.raw.level_1));

        RecyclerView recyclerView = findViewById(R.id.level_select_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LevelAdapter adapter = new LevelAdapter(
                levelList,
                level -> {
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("LEVEL_RESOURCE", level.getRawResourceId());
                    intent.putExtra("LEVEL_TITLE", level.getTitle());
                    startActivity(intent);
                }
        );
        recyclerView.setAdapter(adapter);
    }
}