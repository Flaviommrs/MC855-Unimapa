package com.mc855.unimap.com.mc855.unimap.database;

public class State {

    public static final String TABLE_NAME = "state";
    public static final String IDENVIRONMENT = "id_environment";
    public static final String IDUSER = "id_user";

    private String id_environment;
    private String id_user;

    public State(String id_environment, String id_user) {
        this.id_environment = id_environment;
        this.id_user = id_user;
    }

    public String getId_environment() {
        return id_environment;
    }

    public void setId_environment(String id_environment) {
        this.id_environment = id_environment;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
