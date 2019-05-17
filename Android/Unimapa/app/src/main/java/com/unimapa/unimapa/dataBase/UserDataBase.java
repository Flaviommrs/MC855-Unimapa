package com.unimapa.unimapa.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unimapa.unimapa.domain.User;

import java.util.ArrayList;


public class UserDataBase {
    myDbHelper myhelper;

    public UserDataBase(Context context) {
        myhelper = new myDbHelper(context);
    }


    public String insertData(User user) {
        String id = "";
        SQLiteDatabase db = myhelper.getWritableDatabase();
        //db.execSQL(Tables.DELETE_TABLE_USER);
        //db.execSQL(Tables.CREATE_TABLE_USER);

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.ID, user.getId());
        contentValues.put(User.USERNAME, user.getUsername());
        contentValues.put(User.NAME, user.getName());

        String.valueOf(dbb.insert(User.TABLE_NAME, null, contentValues));

        return id;
    }

    public ArrayList<User> getData() {
        ArrayList<User> users = new ArrayList<User>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {User.ID, User.USERNAME, User.NAME};

        try {
            Cursor cursor = db.query(User.TABLE_NAME, columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(User.ID));
                String username = cursor.getString(cursor.getColumnIndex(User.USERNAME));
                String name = cursor.getString(cursor.getColumnIndex(User.NAME));

                users.add(new User(id, username, name));
            }
        } catch (Exception e) {

        }

        return users;
    }


    public User getUser(){
        ArrayList<User> users = getData();

        for(User user : users){
            return user;
        }

        return null;
    }

    public int updateName(String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(User.TABLE_NAME, contentValues, User.NAME + " = ?", whereArgs);
        return count;
    }

    public int updateToken(String newToken, String id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] whereArgs = {id};
        contentValues.put("TOKEN", newToken);
        int count = db.update(User.TABLE_NAME, contentValues, User.ID + " = ?", whereArgs);
        return count;
    }

    public void resetDataBase() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        db.execSQL(Tables.DELETE_TABLE_MAPA);
        db.execSQL(Tables.DELETE_TABLE_USER);

        db.execSQL(Tables.CREATE_TABLE_MAPA);
        db.execSQL(Tables.CREATE_TABLE_USER);
    }

    public void deleteAll() {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        db.delete(User.TABLE_NAME, null, null);
    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + User.TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, Tables.DATABASE_NAME, null, Tables.DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(Tables.CREATE_TABLE_USER);
                db.execSQL(Tables.CREATE_TABLE_MAPA);
            } catch (Exception e) {
                System.out.println(context.toString() + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                System.out.println(context.toString() + "OnUpgrade");
                db.execSQL(DROP_TABLE);
                db.execSQL(Tables.DELETE_TABLE_USER);
                db.execSQL(Tables.DELETE_TABLE_MAPA);

                db.execSQL(Tables.CREATE_TABLE_USER);
                db.execSQL(Tables.CREATE_TABLE_MAPA);
            } catch (Exception e) {
                System.out.println(context.toString() + e);
            }
        }
    }
}