package com.mc855.unimap.com.mc855.unimap.database;

public final class Tables {

    public static final String DATABASE_NAME = "DADOS_CRM.db";    // Database Name
    public static final int DATABASE_Version = 13;    // Database Version

    public static final String CREATE_TABLE_ENVIRONMENT = "CREATE TABLE "+ Environment.TABLE_NAME +" ( "+
            Environment.ID         + " VARCHAR(60),"+
            Environment.TITLE      + " VARCHAR(40),"  +
            Environment.URLMAIN    + " VARCHAR(100)," +
            Environment.URLSERVICE + " VARCHAR(100)," +
            Environment.BACKGROUND_IMAGE_TOPMENU + " VARCHAR(100) ," +
            Environment.BACKGROUND_IMAGE_LOADING + " VARCHAR(100) ," +
            Environment.LOGO_IMAGE + " VARCHAR(100) ," +
            Environment.COLOR      + " VARCHAR(20));";

    public static final String CREATE_TABLE_USER = "CREATE TABLE "+ User.TABLE_NAME+" ("+
            User.ID             + " VARCHAR(60), "+
            User.IDENVIRONMENT  + " VARCHAR(60), " +
            User.NAME           + " VARCHAR(120)," +
            User.EMAIL          + " VARCHAR(60)," +
            User.SENHA          + " VARCHAR(50)," +
            User.TOKEN          + " VARCHAR(80)," +
            User.ICONUSER       + " VARCHAR(100));";


    public static final String CREATE_TABLE_STATE = "CREATE TABLE "+ State.TABLE_NAME+" ("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            State.IDENVIRONMENT     + " VARCHAR(60)," +
            State.IDUSER            + " VARCHAR(60) );";

    public static final String DELETE_TABLE_ENVIRONMENT = "DROP TABLE IF EXISTS " + Environment.TABLE_NAME + ";";
    public static final String DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + User.TABLE_NAME + ";";
    public static final String DELETE_TABLE_STATE = "DROP TABLE IF EXISTS " + State.TABLE_NAME + ";";

    public static final String DELETE_DATAS_FROM_STATE = "DELETE FROM "+ State.TABLE_NAME;
}
