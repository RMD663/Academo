package com.explosiverodent.academo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.explosiverodent.academo.adapter.LevelAdapter;
import com.explosiverodent.academo.model.Level;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView userNameText;
    private TextView levelText;
    private ProgressBar levelProgress;
    private BottomNavigationView bottomNavigationView;

    private int userId = -1;
    private String currentPicUriString = null;
    private String name = null;

    private SharedPreferences s;

    // CORREÇÃO APLICADA AQUI: O launcher agora atualiza a variável global e o componente visual
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String newPicUri = result.getData().getStringExtra("NEW_PICTURE");
                    if (newPicUri != null && !newPicUri.isEmpty()) {
                        // 1. Atualiza a referência local para os próximos cliques de edição
                        currentPicUriString = newPicUri;

                        // 2. Reseta o cache do ImageView e renderiza a nova imagem segura
                        profilePicture.setImageURI(null);
                        profilePicture.setImageURI(Uri.parse(newPicUri));
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        s = getSharedPreferences("refs", MODE_PRIVATE);

        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                    return insets;
                }
        );

        userId = getIntent().getIntExtra("USER_ID", -1);


        name = getIntent().getStringExtra("USER_NAME");
        int level = getIntent().getIntExtra("USER_LEVEL", 1);
        float xp = getIntent().getFloatExtra("USER_XP", 0.0f);
        currentPicUriString = getIntent().getStringExtra("USER_PICTURE");

        initViews(level, xp);
        setupRecyclerView();
        setupBottomNavigation();
    }

    private void initViews(int level, float xp) {
        profilePicture = findViewById(R.id.profile_picture);
        userNameText = findViewById(R.id.user_name_text);
        levelText = findViewById(R.id.level_text);
        levelProgress = findViewById(R.id.level_progress);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        if (name != null) userNameText.setText(name);
        levelText.setText("LV " + level);
        levelProgress.setProgress((int) xp);

        if (currentPicUriString != null && !currentPicUriString.isEmpty()) {
            try {
                profilePicture.setImageURI(null);
                profilePicture.setImageURI(Uri.parse(currentPicUriString));
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("USER_ID", userId);
                editProfileIntent.putExtra("USER_NAME", name);
                editProfileIntent.putExtra("USER_PICTURE", currentPicUriString);

                editProfileLauncher.launch(editProfileIntent);
            }
        });
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

<<<<<<< HEAD
        RecyclerView recyclerView = findViewById(R.id.level_select_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LevelAdapter adapter = new LevelAdapter(
=======
        Level lvl1 = new Level(1, "FUNDAMENTOS DE SISTEMAS", "Easy", R.raw.level_1);
        Level lvl2 = new Level(2, "FUNDAMENTOS DE REDES", "Easy", R.raw.level_2);
        Level lvl3 = new Level(3, "TÉCNICAS DE PROGRAMAÇÃO 1", "Medium", R.raw.level_3);
        Level lvl4 = new Level(4, "TEORIA GERAL DA ADMINISTRAÇÃO", "Hard", R.raw.level_4);
        Level lvl5 = new Level(5, "TÉCNICAS DE PROGRAMAÇÃO 2", "Medium", R.raw.level_5);
        Level lvl6 = new Level(6, "BANCO DE DADOS", "Hard", R.raw.level_6);


        lvl1.setQuestionsCount(JsonReader.loadQuestions(this, lvl1.getRawResourceId()).size());
        lvl2.setQuestionsCount(JsonReader.loadQuestions(this, lvl2.getRawResourceId()).size());
        lvl3.setQuestionsCount(JsonReader.loadQuestions(this, lvl3.getRawResourceId()).size());
        lvl4.setQuestionsCount(JsonReader.loadQuestions(this, lvl4.getRawResourceId()).size());
        lvl5.setQuestionsCount(JsonReader.loadQuestions(this, lvl5.getRawResourceId()).size());
        lvl6.setQuestionsCount(JsonReader.loadQuestions(this, lvl6.getRawResourceId()).size());

        levelList.add(lvl1);
        levelList.add(lvl2);
        levelList.add(lvl3);
        levelList.add(lvl4);
        levelList.add(lvl5);
        levelList.add(lvl6);

        for (Level level : levelList) {
            userDatabase.loadLevelStats(userId, level);
        }

        levelAdapter = new LevelAdapter(
>>>>>>> fefa681 (questions added)
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