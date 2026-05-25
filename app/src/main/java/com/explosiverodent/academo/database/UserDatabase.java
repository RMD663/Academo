package com.explosiverodent.academo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.explosiverodent.academo.model.User;
import com.explosiverodent.academo.model.Level;

import java.util.ArrayList;
import java.util.List;

public class UserDatabase extends SQLiteOpenHelper {
    private static final String DATABASE = "ACADEMO";
    private static final int VERSION = 5;

    // table Users
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "user_name";
    private static final String COL_POINTS = "points";
    private static final String COL_XP = "xp";
    private static final String COL_LEVEL = "level";
    private static final String COL_PROFILE_PIC_URI = "profile_picture_uri";
    private static final String COL_CORRECT_ANSWERS = "correct_answers_count";

    // Table Auth
    private static final String TABLE_AUTH = "auth";
    private static final String COL_USER_ID_FK = "user_id";
    private static final String COL_AUTH_ID = "auth_id";
    private static final String COL_PASSWORD = "password";

    // Table Level Score
    private static final String TABLE_LEVELS_HISTORY = "user_levels_history";
    private static final String COL_HIST_USER_ID = "user_id";
    private static final String COL_HIST_LEVEL_POS = "level_position";
    private static final String COL_HIST_BEST_SCORE = "best_score";
    private static final String COL_HIST_MAX_RANK = "max_rank";
    private static final String COL_HIST_LAST_DATE = "last_attempt_date";
    private static final String COL_HIST_ATTEMPTS_COUNT = "attempts_count";

    public UserDatabase(Context context){
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        String create_table = "CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_POINTS + " INTEGER,"
                + COL_XP + " REAL,"
                + COL_LEVEL + " INT,"
                + COL_PROFILE_PIC_URI + " TEXT,"
                + COL_CORRECT_ANSWERS + " INTEGER DEFAULT 0 );";
        database.execSQL(create_table);

        String create_auth_table = "CREATE TABLE " + TABLE_AUTH + "("
                + COL_AUTH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_ID_FK + " INTEGER, "
                + COL_PASSWORD + " TEXT, "
                + " FOREIGN KEY (" + COL_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "( " + COL_ID + " ) ON DELETE CASCADE);";
        database.execSQL(create_auth_table);

        String create_history_table = "CREATE TABLE " + TABLE_LEVELS_HISTORY + "("
                + COL_HIST_USER_ID + " INTEGER,"
                + COL_HIST_LEVEL_POS + " INTEGER,"
                + COL_HIST_BEST_SCORE + " INTEGER DEFAULT 0,"
                + COL_HIST_MAX_RANK + " TEXT,"
                + COL_HIST_LAST_DATE + " TEXT,"
                + COL_HIST_ATTEMPTS_COUNT + " INTEGER DEFAULT 0,"
                + " PRIMARY KEY (" + COL_HIST_USER_ID + ", " + COL_HIST_LEVEL_POS + "),"
                + " FOREIGN KEY (" + COL_HIST_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE);";
        database.execSQL(create_history_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int old_version, int new_version){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS_HISTORY);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        this.onCreate(database);
    }

    public void saveLevelRecord(int userId, int levelPosition, int score, String rank, String date) {
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "SELECT " + COL_HIST_BEST_SCORE + ", " + COL_HIST_ATTEMPTS_COUNT + " FROM " + TABLE_LEVELS_HISTORY
                + " WHERE " + COL_HIST_USER_ID + " = ? AND " + COL_HIST_LEVEL_POS + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(levelPosition)});

        ContentValues values = new ContentValues();
        values.put(COL_HIST_LAST_DATE, date);

        if (cursor.moveToFirst()) {
            int previousBestScore = cursor.getInt(0);
            int currentAttempts = cursor.getInt(1);

            values.put(COL_HIST_ATTEMPTS_COUNT, currentAttempts + 1);

            if (score > previousBestScore) {
                values.put(COL_HIST_BEST_SCORE, score);
                values.put(COL_HIST_MAX_RANK, rank);
            }
            database.update(TABLE_LEVELS_HISTORY, values,
                    COL_HIST_USER_ID + " = ? AND " + COL_HIST_LEVEL_POS + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(levelPosition)});
        } else {
            values.put(COL_HIST_USER_ID, userId);
            values.put(COL_HIST_LEVEL_POS, levelPosition);
            values.put(COL_HIST_BEST_SCORE, score);
            values.put(COL_HIST_MAX_RANK, rank);
            values.put(COL_HIST_ATTEMPTS_COUNT, 1);
            database.insert(TABLE_LEVELS_HISTORY, null, values);
        }
        cursor.close();
    }

    public void loadLevelStats(int userId, Level level) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + COL_HIST_BEST_SCORE + ", " + COL_HIST_MAX_RANK + ", " + COL_HIST_LAST_DATE + ", " + COL_HIST_ATTEMPTS_COUNT
                + " FROM " + TABLE_LEVELS_HISTORY
                + " WHERE " + COL_HIST_USER_ID + " = ? AND " + COL_HIST_LEVEL_POS + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(level.getPosition())});

        if (cursor.moveToFirst()) {
            level.setBestScore(cursor.getInt(0));
            level.setMaxRank(cursor.getString(1));
            level.setLastAttemptDate(cursor.getString(2));
            level.setAttemptsCount(cursor.getInt(3));
        } else {
            level.setBestScore(0);
            level.setMaxRank("");
            level.setLastAttemptDate("");
            level.setAttemptsCount(0);
        }
        cursor.close();
    }

    public boolean insertUser(User user, String user_password){
        SQLiteDatabase database = this.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues user_values = new ContentValues();
            user_values.put(COL_NAME, user.getUserName());
            user_values.put(COL_POINTS, user.getPoints());
            user_values.put(COL_XP, user.getXp());
            user_values.put(COL_LEVEL, user.getLevel());
            user_values.put(COL_PROFILE_PIC_URI, user.getProfilePicture());
            user_values.put(COL_CORRECT_ANSWERS, 0);

            long user_id =  database.insert(TABLE_USERS, null, user_values);
            if (user_id == -1){ return false; }

            ContentValues auth_values = new ContentValues();
            auth_values.put(COL_USER_ID_FK, user_id);
            auth_values.put(COL_PASSWORD, user_password);

            long auth_id = database.insert(TABLE_AUTH, null, auth_values);
            if (auth_id == -1){ return false; }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public User validateLogin(String username, String password){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT u.* FROM " + TABLE_USERS + " u "
                + " INNER JOIN " + TABLE_AUTH + " a ON u." + COL_ID + " = a." + COL_USER_ID_FK
                + " WHERE u." + COL_NAME + " = ? AND a." + COL_PASSWORD + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{username, password});
        User userLogin = null;
        if(cursor.moveToFirst()){
            userLogin = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(COL_XP)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_LEVEL))
            );
            String profile_picture_uri = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_PIC_URI));
            if(profile_picture_uri != null && !profile_picture_uri.isEmpty()){
                userLogin.setProfilePicture(profile_picture_uri);
            }
        }
        cursor.close();
        return userLogin;
    }

    public boolean updateUserProgress(int userId, int newPoints, float newXp, int newLevel) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_POINTS, newPoints);
        values.put(COL_XP, newXp);
        values.put(COL_LEVEL, newLevel);
        int rowsAffected = database.update(TABLE_USERS, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public User getUserById(int userId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_ID + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(COL_XP)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_LEVEL))
            );
            String pic = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_PIC_URI));
            if (pic != null) user.setProfilePicture(pic);
        }
        cursor.close();
        return user;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + COL_ID + " FROM " + TABLE_USERS + " WHERE " + COL_NAME + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
        }
        cursor.close();
        return userId;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        int rowsAffected = database.update(TABLE_AUTH, values, COL_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public boolean updateUserProfilePicture(int userId, String imageUri) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_PIC_URI, imageUri);
        int rowsAffected = database.update(TABLE_USERS, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }
}