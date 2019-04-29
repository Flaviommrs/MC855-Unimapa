package com.mc855.unimap.com.mc855.unimap.database;

public class User {

    public static final String TABLE_NAME = "user";
    public static final String ID = "id";
    public static final String IDENVIRONMENT = "idenvironment";
    public static final String NAME = "name";
    public static final String SENHA = "senha";
    public static final String EMAIL = "email";
    public static final String TOKEN = "token";
    public static final String ICONUSER = "iconuser";

    private String id;
    private String idEnvironment;
    private String name;
    private String email;
    private String senha;
    private String token;
    private String iconUser;


    public User(String id, String idEnvironment, String name, String email, String senha, String token, String iconUser) {
        this.id = id;
        this.idEnvironment = idEnvironment;
        this.name = name;
        this.email = email;
        this.senha = senha;
        this.token = token;
        this.iconUser = iconUser;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEnvironment() { return idEnvironment; }

    public void setIdEnvironment(String idEnvironment) { this.idEnvironment = idEnvironment; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconUser() {
        return iconUser;
    }

    public void setIconUser(String iconUser) {
        this.iconUser = iconUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", idEnvironment='" + idEnvironment + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", iconUser='" + iconUser + '\'' +
                '}';
    }


}
