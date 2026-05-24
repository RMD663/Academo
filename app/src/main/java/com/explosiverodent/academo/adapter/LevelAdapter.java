package com.explosiverodent.academo.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.explosiverodent.academo.R;
import com.explosiverodent.academo.model.Level;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<Level> levelList;
    private OnLevelClickListener clickListener;

    public interface OnLevelClickListener {
        void onLevelClick(Level level);
    }

    public LevelAdapter(List<Level> levelList, OnLevelClickListener clickListener) {
        this.levelList = levelList;
        this.clickListener = clickListener;
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView textPosition, textTitle, textDifficulty;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            textPosition = itemView.findViewById(R.id.level_position_text);
            textTitle = itemView.findViewById(R.id.level_title_text);
            textDifficulty = itemView.findViewById(R.id.level_difficulty_text);
        }
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_view, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        Level currentLevel = levelList.get(position);

        holder.textPosition.setText("#" + currentLevel.getPosition());
        holder.textTitle.setText(currentLevel.getTitle());
        holder.textDifficulty.setText("Dificuldade: " + currentLevel.getDifficulty());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLevelClick(currentLevel);
            }
        });
    }

    @Override
    public int getItemCount() { return levelList.size(); }

}