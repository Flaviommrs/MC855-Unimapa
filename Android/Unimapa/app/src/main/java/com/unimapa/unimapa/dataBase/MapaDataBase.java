package com.unimapa.unimapa.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unimapa.unimapa.domain.Mapa;

import java.util.ArrayList;


public class MapaDataBase {
    myDbHelper myhelper;

    public MapaDataBase(Context context) {
        myhelper = new myDbHelper(context);
    }


    public String insertData(Mapa mapa) {
        String id = "";
        //TODO: retirar
        //SQLiteDatabase db = myhelper.getWritableDatabase();
        //db.execSQL(Tables.DELETE_TABLE_MAPA);
        //db.execSQL(Tables.CREATE_TABLE_MAPA);

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Mapa.ID, mapa.getId());
        contentValues.put(Mapa.NAME, mapa.getName());
        contentValues.put(Mapa.TIPO, mapa.getTipo());
        contentValues.put(Mapa.POSTS, mapa.getPosts());

        id = String.valueOf(dbb.insert(Mapa.TABLE_NAME, null, contentValues));
        return id;
    }

    public ArrayList<Mapa> getData() {
        ArrayList<Mapa> users = new ArrayList<Mapa>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {Mapa.ID, Mapa.NAME, Mapa.POSTS, Mapa.TIPO};

        try {
            Cursor cursor = db.query(Mapa.TABLE_NAME, columns, null, null, null, null, Mapa.NAME);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(Mapa.ID));
                String name = cursor.getString(cursor.getColumnIndex(Mapa.NAME));
                int posts = cursor.getInt(cursor.getColumnIndex(Mapa.POSTS));
                String tipo = cursor.getString(cursor.getColumnIndex(Mapa.TIPO));

                users.add(new Mapa(id, name, posts, false, tipo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    public Mapa findMapaById(String id){
        ArrayList<Mapa> mapas = getData();

        for(Mapa mapa : mapas) {
            if (mapa.getId().equals(id)){
                return mapa;
            }
        }

        return null;
    }

    public void removeById(int id){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        //String[] whereArgs = {id};

        db.delete(Mapa.TABLE_NAME, Mapa.ID + " = " + id, null);
    }

    public int updateName(String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Mapa.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(Mapa.TABLE_NAME, contentValues, Mapa.NAME + " = ?", whereArgs);
        return count;
    }

    public int updateToken(String newToken, String id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] whereArgs = {id};
        contentValues.put("TOKEN", newToken);
        int count = db.update(Mapa.TABLE_NAME, contentValues, Mapa.ID + " = ?", whereArgs);
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

        db.delete(Mapa.TABLE_NAME, null, null);
    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Mapa.TABLE_NAME;
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

