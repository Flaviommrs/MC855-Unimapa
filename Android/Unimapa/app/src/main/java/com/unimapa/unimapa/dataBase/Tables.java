package com.unimapa.unimapa.dataBase;

import com.unimapa.unimapa.domain.Mapa;
import com.unimapa.unimapa.domain.User;

public final class Tables {

    public static final String DATABASE_NAME = "DADOS_UNIMAPA.db";    // Database Name
    public static final int DATABASE_Version = 7;    // Database Version


    public static final String CREATE_TABLE_USER = "CREATE TABLE "+ User.TABLE_NAME +" ("+
            "id VARCHAR(60) PRIMARY KEY, "+
            User.USERNAME       + " VARCHAR(120)," +
            User.NAME   + " VARCHAR(60)," +
            User.TOKEN   + " VARCHAR(120))";


    public static final String CREATE_TABLE_MAPA = "CREATE TABLE "+ Mapa.TABLE_NAME +" ("+
            "id INTEGER PRIMARY KEY, "+
            Mapa.NAME    + " VARCHAR(60)," +
            Mapa.POSTS   + " INTEGER," +
            Mapa.TIPO    + " VARCHAR(60));";

    public static final String DELETE_TABLE_MAPA = "DROP TABLE IF EXISTS " + Mapa.TABLE_NAME + ";";
    public static final String DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + User.TABLE_NAME + ";";

}
