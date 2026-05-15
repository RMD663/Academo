package com.explosiverodent.academo.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASENAME = "ACADEMO";

    public DatabaseHelper(Context context){
        super(context, DATABASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE login(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "phone TEXT UNIQUE," +
                "username VARCHAR(20) UNIQUE," +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE user(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL," +
                "course TEXT NOT NULL," +
                "institution TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean createLogin(SQLiteDatabase db, ContentValues values){
        db.insert("login", null, values);
        return true;
    }
    public boolean createUser(SQLiteDatabase db, ContentValues values){
        return db.insert("user", null, values) != -1;
    }

    public boolean getUserLogin(SQLiteDatabase db, String login, String password){
        Cursor cursor = db.rawQuery("SELECT email, password FROM login WHERE (email = ? OR phone = ? OR username = ?) AND password = ?", new String[]{login, login, login, password});

        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }
}
