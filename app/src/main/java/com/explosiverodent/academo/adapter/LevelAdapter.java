package com.explosiverodent.academo.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.explosiverodent.academo.R;
import com.explosiverodent.academo.model.Level;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<Level> levelList;
    private OnLevelClickListener clickListener;
    private OnLevelLongClickListener longClickListener;

    public interface OnLevelClickListener {
        void onLevelClick(Level level);
    }

    public interface OnLevelLongClickListener {
        void onLevelLongClick(Level level);
    }

    public LevelAdapter(List<Level> levelList, OnLevelClickListener clickListener, OnLevelLongClickListener longClickListener) {
        this.levelList = levelList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView textPosition, textTitle, textDifficulty;
        TextView textScore, textDate, textRank;
        LinearLayout containerRank;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            textPosition = itemView.findViewById(R.id.level_position_text);
            textTitle = itemView.findViewById(R.id.level_title_text);
            textDifficulty = itemView.findViewById(R.id.level_difficulty_text);

            textScore = itemView.findViewById(R.id.level_score_text);
            textDate = itemView.findViewById(R.id.level_date_text);
            textRank = itemView.findViewById(R.id.level_rank_text);
            containerRank = itemView.findViewById(R.id.container_level_rank);
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

        boolean isCustomLevel = (currentLevel.getRawResourceId() == 0);

        if (isCustomLevel) {
            holder.textPosition.setText("[C]");
            holder.textTitle.setText(currentLevel.getTitle());
        } else {
            holder.textPosition.setText("#" + currentLevel.getPosition());
            holder.textTitle.setText(currentLevel.getTitle());
        }

        holder.textDifficulty.setText("Difficulty: " + currentLevel.getDifficulty());

        if (isCustomLevel) {
            holder.textPosition.setTextColor(Color.parseColor("#00BCD4"));
        } else {
            String difficulty = currentLevel.getDifficulty() != null ? currentLevel.getDifficulty() : "Easy";
            switch (difficulty.trim().toLowerCase()) {
                case "hard":
                    holder.textPosition.setTextColor(Color.parseColor("#E53935"));
                    break;
                case "medium":
                    holder.textPosition.setTextColor(Color.parseColor("#FF9800"));
                    break;
                case "easy":
                default:
                    holder.textPosition.setTextColor(Color.parseColor("#468232"));
                    break;
            }
        }

        if (currentLevel.getBestScore() > 0) {
            holder.textScore.setVisibility(View.VISIBLE);
            holder.containerRank.setVisibility(View.VISIBLE);
            holder.textScore.setText("Best Score: " + currentLevel.getBestScore() + " pts");
            holder.textRank.setText(currentLevel.getMaxRank());

            if (isCustomLevel && currentLevel.getLastAttemptDate() != null && currentLevel.getLastAttemptDate().contains("://")) {
                holder.textDate.setVisibility(View.GONE);
            } else if (currentLevel.getLastAttemptDate() != null && !currentLevel.getLastAttemptDate().isEmpty()) {
                holder.textDate.setVisibility(View.VISIBLE);
                holder.textDate.setText("Last attempt: " + currentLevel.getLastAttemptDate());
            } else {
                holder.textDate.setVisibility(View.GONE);
            }

            String rank = currentLevel.getMaxRank() != null ? currentLevel.getMaxRank() : "D";
            switch (rank.trim().toUpperCase()) {
                case "SS":
                    holder.textRank.setTextColor(Color.parseColor("#FFD700"));
                    break;
                case "S":
                    holder.textRank.setTextColor(Color.parseColor("#FFA500"));
                    break;
                case "A":
                    holder.textRank.setTextColor(Color.parseColor("#9370DB"));
                    break;
                case "B":
                    holder.textRank.setTextColor(Color.parseColor("#4682B4"));
                    break;
                default:
                    holder.textRank.setTextColor(Color.parseColor("#8B8B8B"));
                    break;
            }
        } else {
            holder.textScore.setVisibility(View.GONE);
            holder.textDate.setVisibility(View.GONE);
            holder.containerRank.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLevelClick(currentLevel);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLevelLongClick(currentLevel);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() { return levelList.size(); }
}