package com.mc855.unimap.com.mc855.unimap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class UserDataBase {
    myDbHelper myhelper;

    public UserDataBase(Context context){
        myhelper = new myDbHelper(context);
    }


    public String insertData(User user){
        String id = "";
        SQLiteDatabase db = myhelper.getWritableDatabase();
        //db.execSQL(Tables.DELETE_TABLE_USER);
        //db.execSQL(Tables.CREATE_TABLE_USER);
        //verifica se ja possui usuario neste ambiente,  pois so pode existir um usuario por ambiente
        User u = findByIDEnvironment(user.getIdEnvironment());
        if(u != null){
            deleteById(u.getId());
        }

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.ID, user.getIdEnvironment());
        contentValues.put(User.IDENVIRONMENT, user.getIdEnvironment());
        contentValues.put(User.NAME, user.getName());
        contentValues.put(User.EMAIL, user.getEmail());
        contentValues.put(User.SENHA, user.getSenha());
        contentValues.put(User.TOKEN, user.getToken());
        contentValues.put(User.ICONUSER, user.getIconUser());

        String.valueOf(dbb.insert(User.TABLE_NAME, null, contentValues));
        id = user.getIdEnvironment();

        return id;
    }

    public ArrayList<User> getData(){
        ArrayList<User> users = new ArrayList<User>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {User.ID, User.IDENVIRONMENT, User.NAME, User.EMAIL, User.SENHA, User.TOKEN, User.ICONUSER};

        try {
            Cursor cursor = db.query(User.TABLE_NAME, columns,null,null,null,null,null);

            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(User.ID));
                String idEnvironment = cursor.getString(cursor.getColumnIndex(User.IDENVIRONMENT));
                String name = cursor.getString(cursor.getColumnIndex(User.NAME));
                String email = cursor.getString(cursor.getColumnIndex(User.EMAIL));
                String senha = cursor.getString(cursor.getColumnIndex(User.SENHA));
                String token = cursor.getString(cursor.getColumnIndex(User.TOKEN));
                String iconuser = cursor.getString(cursor.getColumnIndex(User.ICONUSER));
                //buffer.append(cid+ "   " + name + "   " + password +" \n");
                users.add(new User(id, idEnvironment, name, email, senha, token, iconuser));
        }
        }catch (Exception e){

            /*
            db.execSQL(Tables.DELETE_TABLE_USER);
            db.execSQL(Tables.CREATE_TABLE_USER);


            insertData(new User(0, 0,
                    "nome", "emaikl","asdasd",
                    "https://cdn.pixabay.com/photo/2017/02/23/13/05/profile-2092113_960_720.png"));

            Cursor cursor = db.query(User.TABLE_NAME, columns,null,null,null,null,null);

            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(User.ID));
                int idEnvironment = cursor.getInt(cursor.getColumnIndex(User.IDENVIRONMENT));
                String name = cursor.getString(cursor.getColumnIndex(User.NAME));
                String email = cursor.getString(cursor.getColumnIndex(User.EMAIL));
                String  token = cursor.getString(cursor.getColumnIndex(User.TOKEN));
                String  iconuser = cursor.getString(cursor.getColumnIndex(User.ICONUSER));
                //buffer.append(cid+ "   " + name + "   " + password +" \n");
                users.add(new User(id, idEnvironment, name, email, token, iconuser));
            }*/

        }

        return users;
    }

    public User findById(String id){
        ArrayList<User> users = getData();

        for(User user : users){
            if(user.getId().equals(id)){
                return user;
            }
        }

        return null;
    }

    public User findByName(String name){
        ArrayList<User> users = getData();

        for(User user : users){
            if(user.getName().equals(name)){
                return user;
            }
        }

        return null;
    }

    public User findByIDEnvironment(String idEnvironment){
        ArrayList<User> users = getData();

        if(users != null)
        for(User user : users){
            if(user.getIdEnvironment().equals(idEnvironment)){
                return user;
            }
        }

        return null;
    }

    public int deleteByName(String uname){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={uname};

        int count =db.delete(User.TABLE_NAME ,User.NAME+" = ?",whereArgs);
        return  count;
    }

    public int deleteById(String id){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(id)};

        int count = db.delete(User.TABLE_NAME ,User.ID + " = ?", whereArgs);
        return  count;
    }

    public int updateName(String oldName , String newName){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(User.NAME, newName);
        String[] whereArgs= {oldName};
        int count =db.update(User.TABLE_NAME,contentValues, User.NAME+" = ?", whereArgs);
        return count;
    }

    public String getToken(String id){
        return findById(id).getToken();
    }

    public int updateToken(String newToken, String id){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String[] whereArgs= {id};
        contentValues.put("TOKEN", newToken);
        int count = db.update(User.TABLE_NAME,contentValues, User.ID+" = ?", whereArgs);
        return count;
    }

    public void resetDataBase() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        db.execSQL(Tables.DELETE_TABLE_STATE);
        db.execSQL(Tables.DELETE_TABLE_USER);
        db.execSQL(Tables.DELETE_TABLE_ENVIRONMENT);

        db.execSQL(Tables.CREATE_TABLE_STATE);
        db.execSQL(Tables.CREATE_TABLE_USER);
        db.execSQL(Tables.CREATE_TABLE_ENVIRONMENT);
    }

    public void deleteAll() {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        db.delete(User.TABLE_NAME ,null, null);
    }


    static class myDbHelper extends SQLiteOpenHelper{
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+User.TABLE_NAME;
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
                db.execSQL(Tables.DELETE_TABLE_USER);
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
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try {
            db.execSQL(Tables.DELETE_DATAS_FROM_STATE);
        }catch (Exception e){
            db.execSQL(Tables.CREATE_TABLE_STATE);
        }
    }

    public long insertState(State state){
        deleteState();

        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(State.IDENVIRONMENT, state.getId_environment());
        contentValues.put(State.IDUSER, state.getId_user());

        long id = dbb.insert(State.TABLE_NAME, null, contentValues);

        return id;
    }

    public State getState(){
        State state = null;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {State.IDENVIRONMENT, State.IDUSER};
        Cursor cursor =db.query(State.TABLE_NAME, columns,null,null,null,null,null);

        while (cursor.moveToNext()){

            String id_environment = cursor.getString(cursor.getColumnIndex(State.IDENVIRONMENT));
            String id_user = cursor.getString(cursor.getColumnIndex(State.IDUSER));

            state = new State(id_environment, id_user);
        }

        return state;
    }
}