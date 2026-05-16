package com.explosiverodent.academo.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.explosiverodent.academo.Database.DatabaseHelper;

public class User {
   private String email;
   private String phone;
   private String username;
   private String password;
   private String name;
   private String course;
   private String institution;

   private Context context;
   private  DatabaseHelper databaseHelper;

   public User(String email, String phone, String username, String password, String name, String course, String institution, Context context){
       this.setEmail(email);
       this.setPhone(phone);
       this.setUsername(username);
       this.setPassword(password);
       this.setName(name);
       this.setCourse(course);
       this.setInstitution(institution);
       this.setContext(context);
       this.databaseHelper = new DatabaseHelper(getContext());
   }

   public User(String email, String password, Context context){
       this.setEmail(email);
       this.setPassword(password);
       this.setContext(context);
       this.databaseHelper = new DatabaseHelper(getContext());
   }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getInstitution() {
        return institution;
    }

    public String getCourse() {
        return course;
    }

    public Context getContext() {
        return context;
    }

    public boolean registerUser() throws Exception {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
        boolean rows = this.databaseHelper.getUserLogin(db, this.getEmail(), this.getPassword());

        if (rows){
            throw new Exception("Usuário já Cadastrado!");
        }

        ContentValues values = new ContentValues();
        ContentValues users = new ContentValues();

        values.put("email", this.getEmail());
        values.put("phone", this.getPhone());
        values.put("username", this.getUsername());
        values.put("password", this.getPassword());


        users.put("name", this.getName());
        users.put("course", this.getCourse());
        users.put("institution", this.getInstitution());


        if (!this.databaseHelper.createLogin(db, values)){
           throw  new Exception("Erro ao registrar as informações de Login");
        }

        if (!this.databaseHelper.createUser(db, users)){
            throw  new Exception("Erro ao registrar as informações de Login");
        }
        return true;
    }

    public boolean login() throws Exception{
       SQLiteDatabase db = this.databaseHelper.getReadableDatabase();

       boolean auth = this.databaseHelper.getUserLogin(db, this.getEmail(), this.getPassword());

       if(!auth){
           throw new Exception("Login ou Senha Inválidos");
       }

       return true;
    }
}
