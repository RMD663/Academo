package com.explosiverodent.academo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.explosiverodent.academo.model.User;


public class UserDatabase extends SQLiteOpenHelper {
    private static final String DATABASE = "ACADEMO";
    private static final int VERSION = 2;

    // User TABLE
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "user_name";
    private static final String COL_POINTS = "points";
    private static final String COL_XP = "xp";

    private static final String COL_LEVEL = "level";
    private static final String COL_PROFILE_PIC_URI = "profile_picture_uri";

    // Auth TABLE

    private static final String TABLE_AUTH = "auth";
    private static final String COL_USER_ID_FK = "user_id";
    private static final String COL_AUTH_ID = "auth_id";
    private static final String COL_PASSWORD = "password";

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
                + COL_PROFILE_PIC_URI + " TEXT );" ;
        database.execSQL(create_table);

        String create_auth_table = "CREATE TABLE " + TABLE_AUTH + "("
                + COL_AUTH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_ID_FK + " INTEGER, "
                + COL_PASSWORD + " TEXT, "
                + " FOREIGN KEY (" + COL_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "( " + COL_ID + " ) ON DELETE CASCADE);";
        database.execSQL(create_auth_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int old_version, int new_version){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        this.onCreate(database);
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

}
