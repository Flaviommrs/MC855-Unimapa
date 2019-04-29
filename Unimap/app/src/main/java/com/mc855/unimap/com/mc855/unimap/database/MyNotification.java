package com.mc855.unimap.com.mc855.unimap.database;

public class MyNotification {

    public  static String IDENVIRONMENT = "idEnvironment";
    public  static String TITLE = "title";
    public  static String MESSAGE = "message";
    public  static String RETURNPARAM = "returnParam";

    private int idEnvironment;
    private String title;
    private String message;
    private String returnParam;

    public MyNotification(int idEnvironment, String title, String message, String returnParam) {
        this.idEnvironment = idEnvironment;
        this.title = title;
        this.message = message;
        this.returnParam = returnParam;
    }

    public int getIdEnvironment() {
        return idEnvironment;
    }

    public void setIdEnvironment(int idEnvironment) {
        this.idEnvironment = idEnvironment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReturnParam() {
        return returnParam;
    }

    public void setReturnParam(String returnParam) {
        this.returnParam = returnParam;
    }
}


