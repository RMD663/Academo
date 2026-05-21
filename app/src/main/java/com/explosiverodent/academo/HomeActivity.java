package com.explosiverodent.academo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explosiverodent.academo.adapter.LevelAdapter;
import com.explosiverodent.academo.model.Level;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView profile_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile_picture = findViewById(R.id.profile_picture);

        List<Level> level_list = new ArrayList<>();
        level_list.add(new Level(1, "Forest", "Easy", "level_1.json"));
        level_list.add(new Level(2, "Cavern", "Medium", "level_2.json"));

        RecyclerView recyclerView = findViewById(R.id.level_select_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LevelAdapter adapter = new LevelAdapter(level_list, level -> {
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra("JSON_FILE", level.getJsonFileName());
            intent.putExtra("LEVEL_TITLE", level.getTitle());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}