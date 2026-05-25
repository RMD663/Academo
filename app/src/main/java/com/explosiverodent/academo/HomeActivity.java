package com.explosiverodent.academo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explosiverodent.academo.adapter.LevelAdapter;
import com.explosiverodent.academo.database.UserDatabase;
import com.explosiverodent.academo.jsonreader.JsonReader;
import com.explosiverodent.academo.model.Level;
import com.explosiverodent.academo.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView userNameText;
    private TextView levelText;
    private ProgressBar levelProgress;
    private FloatingActionButton fabFilter;
    private FloatingActionButton fabImport;
    private RecyclerView recyclerViewLevels;
    private LevelAdapter levelAdapter;
    private List<Level> levelList = new ArrayList<>();
    private Map<Integer, String> customPathsMap = new HashMap<>();

    private UserDatabase userDatabase;
    private int userId = -1;

    private SharedPreferences s;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadUserData();
                    loadLevelsData();
                }
            }
    );

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processImportedLevel(uri);
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
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        userId = getIntent().getIntExtra("USER_ID", -1);

        initViews();
        loadLevelsData();
        setupFilterMenu();
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
        fabFilter = findViewById(R.id.fab_filter);
        fabImport = findViewById(R.id.fab_import);

        recyclerViewLevels = findViewById(R.id.level_select_list);
        recyclerViewLevels.setLayoutManager(new LinearLayoutManager(this));

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

        fabImport.setOnClickListener(v -> filePickerLauncher.launch("application/json"));
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

    private void loadLevelsData() {
        levelList.clear();
        customPathsMap.clear();

        Level lvl1 = new Level(1, "FUNDAMENTOS DE SISTEMAS", "Easy", R.raw.level_1);
        Level lvl2 = new Level(2, "FUNDAMENTOS DE REDES", "Easy", R.raw.level_2);
        Level lvl3 = new Level(3, "TÉCNICAS DE PROGRAMAÇÃO 1", "Medium", R.raw.level_3);
        Level lvl4 = new Level(4, "TEORIA GERAL DA ADMINISTRAÇÃO", "Hard", R.raw.level_4);

        lvl1.setQuestionsCount(JsonReader.loadQuestions(this, lvl1.getRawResourceId()).size());
        lvl2.setQuestionsCount(JsonReader.loadQuestions(this, lvl2.getRawResourceId()).size());
        lvl3.setQuestionsCount(JsonReader.loadQuestions(this, lvl3.getRawResourceId()).size());
        lvl4.setQuestionsCount(JsonReader.loadQuestions(this, lvl4.getRawResourceId()).size());

        levelList.add(lvl1);
        levelList.add(lvl2);
        levelList.add(lvl3);
        levelList.add(lvl4);

        File folder = getFilesDir();
        File[] files = folder.listFiles();

        int positionTracker = 5;

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("custom_level_") && file.getName().endsWith(".json")) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        List<com.explosiverodent.academo.model.Question> questions = JsonReader.loadQuestionsFromStream(this, fis);

                        String rawName = file.getName();
                        String cleanTitle = rawName.replace("custom_level_", "");
                        if (cleanTitle.contains("_")) {
                            cleanTitle = cleanTitle.substring(0, cleanTitle.lastIndexOf("_")).replace("_", " ");
                        } else {
                            cleanTitle = cleanTitle.replace(".json", "");
                        }
                        String customTitle = cleanTitle.toUpperCase();

                        Level customLvl = new Level(positionTracker, customTitle, "Medium", 0);
                        customLvl.setQuestionsCount(questions.size());

                        customPathsMap.put(positionTracker, file.getAbsolutePath());

                        levelList.add(customLvl);
                        positionTracker++;

                    } catch (Exception e) {
                        android.util.Log.e("LOAD_CUSTOM_LEVEL", "Error reading file " + file.getName(), e);
                    }
                }
            }
        }

        for (Level level : levelList) {
            userDatabase.loadLevelStats(userId, level);
        }

        levelAdapter = new LevelAdapter(
                levelList,
                level -> {
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("LEVEL_RESOURCE", level.getRawResourceId());
                    intent.putExtra("LEVEL_TITLE", level.getTitle());
                    intent.putExtra("LEVEL_POSITION", level.getPosition());
                    intent.putExtra("LEVEL_DIFFICULTY", level.getDifficulty());

                    if (level.getRawResourceId() == 0) {
                        String realPath = customPathsMap.get(level.getPosition());
                        intent.putExtra("LEVEL_CUSTOM_URI", realPath);
                    }

                    startActivity(intent);
                },
                level -> {
                    if (level.getRawResourceId() == 0) {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Delete Custom Level")
                                .setMessage("Are you sure you want to delete \"" + level.getTitle() + "\"?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    String filePath = customPathsMap.get(level.getPosition());
                                    if (filePath != null) {
                                        File fileToDelete = new File(filePath);
                                        if (fileToDelete.exists() && fileToDelete.delete()) {
                                            Toast.makeText(HomeActivity.this, "Level deleted successfully.", Toast.LENGTH_SHORT).show();
                                            loadLevelsData();
                                        } else {
                                            Toast.makeText(HomeActivity.this, "Failed to delete the level file.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                }
        );
        recyclerViewLevels.setAdapter(levelAdapter);
    }

    private void processImportedLevel(Uri uri) {
        android.os.ParcelFileDescriptor pfd = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            String originalName = "custom_level";
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        String fullName = cursor.getString(nameIndex);
                        if (fullName.contains(".")) {
                            originalName = fullName.substring(0, fullName.lastIndexOf("."));
                        } else {
                            originalName = fullName;
                        }
                    }
                }
                cursor.close();
            }

            pfd = getContentResolver().openFileDescriptor(uri, "r");
            if (pfd == null) {
                Toast.makeText(this, "Could not open selected file.", Toast.LENGTH_SHORT).show();
                return;
            }

            fis = new FileInputStream(pfd.getFileDescriptor());
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

            String rawJson = jsonStringBuilder.toString().trim();

            org.json.JSONArray testArray = new org.json.JSONArray(rawJson);
            if (testArray.length() == 0) {
                Toast.makeText(this, "The JSON file does not contain any questions.", Toast.LENGTH_SHORT).show();
                return;
            }

            String cleanFileNameLabel = originalName.replaceAll("[^a-zA-Z0-9-_]", "_");
            String fileName = "custom_level_" + cleanFileNameLabel + "_" + System.currentTimeMillis() + ".json";
            File internalFile = new File(getFilesDir(), fileName);

            fos = new FileOutputStream(internalFile);
            fos.write(rawJson.getBytes(StandardCharsets.UTF_8));
            fos.flush();

            Toast.makeText(this, "Level imported successfully!", Toast.LENGTH_SHORT).show();
            loadLevelsData();

        } catch (org.json.JSONException je) {
            android.util.Log.e("IMPORT_LEVEL", "Invalid JSON data structure", je);
            Toast.makeText(this, "Invalid JSON format. Check keys and arrays.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            android.util.Log.e("IMPORT_LEVEL", "Critical safe-stream copy failed", e);
            Toast.makeText(this, "Error processing and saving the file.", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
                if (pfd != null) pfd.close();
            } catch (Exception ignored) {}
        }
    }

    private void setupFilterMenu() {
        fabFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, fabFilter);
            popup.getMenu().add(0, 1, 0, "Rank");
            popup.getMenu().add(0, 2, 0, "Attempts");
            popup.getMenu().add(0, 3, 0, "Difficulty");
            popup.getMenu().add(0, 4, 0, "Total Questions");

            popup.setOnMenuItemClickListener(item -> {
                if (levelAdapter == null || levelList.isEmpty()) return false;

                switch (item.getItemId()) {
                    case 1:
                        Collections.sort(levelList, (l1, l2) -> {
                            String r1 = l1.getMaxRank().isEmpty() ? "Z" : l1.getMaxRank();
                            String r2 = l2.getMaxRank().isEmpty() ? "Z" : l2.getMaxRank();
                            return r1.compareTo(r2);
                        });
                        break;

                    case 2:
                        Collections.sort(levelList, (l1, l2) -> Integer.compare(l2.getAttemptsCount(), l1.getAttemptsCount()));
                        break;

                    case 3:
                        Collections.sort(levelList, (l1, l2) -> {
                            int p1 = getDifficultyWeight(l1.getDifficulty());
                            int p2 = getDifficultyWeight(l2.getDifficulty());
                            return Integer.compare(p2, p1);
                        });
                        break;

                    case 4:
                        Collections.sort(levelList, (l1, l2) -> Integer.compare(l2.getQuestionsCount(), l1.getQuestionsCount()));
                        break;
                }

                levelAdapter.notifyDataSetChanged();
                Toast.makeText(HomeActivity.this, item.getTitle() + " applied", Toast.LENGTH_SHORT).show();
                return true;
            });
            popup.show();
        });
    }

    private int getDifficultyWeight(String difficulty) {
        if (difficulty == null) return 0;
        switch (difficulty.trim().toLowerCase()) {
            case "hard": return 3;
            case "medium": return 2;
            case "easy": return 1;
            default: return 0;
        }
    }
}