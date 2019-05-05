package com.mc855.unimap.com.mc855.unimap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class EnvironmentDataBase {
    
    myDbHelper myhelper;

    public EnvironmentDataBase(Context context){
        myhelper = new myDbHelper(context);
    }

    public long insertData(Environment environment){
        long id = -1;
        SQLiteDatabase db = myhelper.getWritableDatabase();

        deleteByTitle(environment.getTitle());

        if(findByTitle(environment.getTitle()) == null) {
            SQLiteDatabase dbb = myhelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Environment.ID, environment.getId());
            contentValues.put(Environment.TITLE, environment.getTitle());
            contentValues.put(Environment.URLMAIN, environment.getUrlMain());
            contentValues.put(Environment.URLSERVICE, environment.getUrlService());
            contentValues.put(Environment.BACKGROUND_IMAGE_TOPMENU, environment.getBackground_image_topmenu());
            contentValues.put(Environment.BACKGROUND_IMAGE_LOADING, environment.getBackground_image_loading());
            contentValues.put(Environment.LOGO_IMAGE, environment.getLogo_image());
            contentValues.put(Environment.COLOR, environment.getColor());

            id = dbb.insert(Environment.TABLE_NAME, null, contentValues);
        }


        return id;
    }

    public ArrayList<Environment> getData(){
        ArrayList<Environment> environments = new ArrayList<Environment>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {Environment.ID, Environment.TITLE,
                            Environment.URLMAIN, Environment.URLSERVICE, Environment.BACKGROUND_IMAGE_TOPMENU,
                            Environment.BACKGROUND_IMAGE_LOADING, Environment.LOGO_IMAGE,
                            Environment.COLOR};
        try {
            Cursor cursor = db.query(Environment.TABLE_NAME, columns, null, null, null, null, null);

            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(Environment.ID));
                String title = cursor.getString(cursor.getColumnIndex(Environment.TITLE));
                String urlmain = cursor.getString(cursor.getColumnIndex(Environment.URLMAIN));
                String urlservice = cursor.getString(cursor.getColumnIndex(Environment.URLSERVICE));
                String backgroundimagetopmenu = cursor.getString(cursor.getColumnIndex(Environment.BACKGROUND_IMAGE_TOPMENU));
                String backgroundimageloading = cursor.getString(cursor.getColumnIndex(Environment.BACKGROUND_IMAGE_LOADING));
                String logoimgage = cursor.getString(cursor.getColumnIndex(Environment.LOGO_IMAGE));
                String color = cursor.getString(cursor.getColumnIndex(Environment.COLOR));

                environments.add(new Environment(id, title, urlmain, urlservice, backgroundimagetopmenu,
                        backgroundimageloading, logoimgage, color));
            }
        }catch (Exception e){

            /*//db.execSQL(Tables.DELETE_TABLE_ENVIRONMENT);
            db.execSQL(Tables.CREATE_TABLE_ENVIRONMENT);


            insertData(new Environment(0,"Ambiente 1", "http://www.facebook.com.br","www.dorma.com",
                    "https://st3.depositphotos.com/2166177/18016/i/1600/depositphotos_180163246-stock-photo-aerial-view-of-the-airport.jpg",
                    "https://img.elo7.com.br/product/original/115E580/painel-paisagem-g-frete-gratis-decoracao-de-festa.jpg",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXf-qbiDWB32lUx6eVnNRVk2sMcGKNbvPGf3Sxylc5V69n2Lu9uA",
                    "#3f8e2c"));

            Cursor cursor = db.query(Environment.TABLE_NAME, columns, null, null, null, null, null);

            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(Environment.ID));
                String title = cursor.getString(cursor.getColumnIndex(Environment.TITLE));
                String urlmain = cursor.getString(cursor.getColumnIndex(Environment.URLMAIN));
                String urlservice = cursor.getString(cursor.getColumnIndex(Environment.URLSERVICE));
                String backgroundimagetopmenu = cursor.getString(cursor.getColumnIndex(Environment.BACKGROUND_IMAGE_TOPMENU));
                String backgroundimageloading = cursor.getString(cursor.getColumnIndex(Environment.BACKGROUND_IMAGE_LOADING));
                String logoimgage = cursor.getString(cursor.getColumnIndex(Environment.LOGO_IMAGE));
                String color = cursor.getString(cursor.getColumnIndex(Environment.COLOR));

                environments.add(new Environment(id, title, urlmain, urlservice, backgroundimagetopmenu,
                        backgroundimageloading, logoimgage, color));
            }*/

        }


        return environments;
    }

    public Environment findById(String id){
        ArrayList<Environment> environments = getData();
        System.out.println(id);
        for(Environment environment : environments){
            System.out.println(environment.getId());
            if(environment.getId().equals(id)){
                return environment;
            }
        }

        return null;
    }

    public Environment findByTitle(String title){
        ArrayList<Environment> environments = getData();

        for(Environment environment : environments){
            if(environment.getTitle().equals(title)){
                return environment;
            }
        }

        return null;
    }
    public  int deleteByTitle(String title){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={title};

        int count = db.delete(Environment.TABLE_NAME ,Environment.TITLE+" = ?",whereArgs);
        return  count;
    }

    public int updateTitle(String oldName , String newTitle){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Environment.TITLE, newTitle);
        String[] whereArgs= {oldName};
        int count = db.update(Environment.TABLE_NAME,contentValues, Environment.TITLE+" = ?", whereArgs);
        return count;
    }

    public void deleteAll() {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        db.delete(Environment.TABLE_NAME ,null,null);
    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Environment.TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, Tables.DATABASE_NAME, null, Tables.DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(Tables.CREATE_TABLE_ENVIRONMENT);
                db.execSQL(Tables.CREATE_TABLE_USER);
                db.execSQL(Tables.CREATE_TABLE_STATE);
            } catch (Exception e) {
                System.out.println(context.toString()+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                System.out.println(context.toString()+"OnUpgrade");
                db.execSQL(DROP_TABLE);
                db.execSQL(Tables.DELETE_TABLE_ENVIRONMENT);
                db.execSQL(Tables.DELETE_TABLE_STATE);

                db.execSQL(Tables.CREATE_TABLE_ENVIRONMENT);
                db.execSQL(Tables.CREATE_TABLE_USER);
                db.execSQL(Tables.CREATE_TABLE_STATE);
            }catch (Exception e) {
                System.out.println(context.toString()+e);
            }
        }
    }


    ////////////////STATE////////////////////////////

    public void deleteState(){
        try {
            SQLiteDatabase db = myhelper.getWritableDatabase();
            db.execSQL(Tables.DELETE_DATAS_FROM_STATE);
        }catch (Exception e){

        }

    }

    public int insertState(State state){
        deleteState();

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(State.IDENVIRONMENT, state.getId_environment());
        contentValues.put(State.IDUSER, state.getId_user());

        int id = (int)dbb.insert(State.TABLE_NAME, null, contentValues);

        return id;
    }

    public State getState(){
        State state = null;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {State.IDENVIRONMENT, State.IDUSER};
        try {
            Cursor cursor = db.query(State.TABLE_NAME, columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                String id_environment = cursor.getString(cursor.getColumnIndex(State.IDENVIRONMENT));
                String id_user = cursor.getString(cursor.getColumnIndex(State.IDUSER));

                state = new State(id_environment, id_user);
            }
        }catch (Exception e){

            //db.execSQL(Tables.DELETE_TABLE_STATE);
            /*db.execSQL(Tables.CREATE_TABLE_STATE);


            insertState(new State(0,0));

            Cursor cursor = db.query(State.TABLE_NAME, columns, null, null, null, null, null);

            while (cursor.moveToNext()) {

                int id_environment = cursor.getInt(cursor.getColumnIndex(State.IDENVIRONMENT));
                int id_user = cursor.getInt(cursor.getColumnIndex(State.IDUSER));

                state = new State(id_environment, id_user);
            }*/
        }

        return state;
    }

}