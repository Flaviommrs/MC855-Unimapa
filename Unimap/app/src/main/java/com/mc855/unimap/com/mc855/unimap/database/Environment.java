package com.mc855.unimap.com.mc855.unimap.database;

public class Environment {

    public static final String TABLE_NAME = "environment";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String URLMAIN = "urlMain";
    public static final String URLSERVICE = "urlService";
    public static final String BACKGROUND_IMAGE_TOPMENU = "background_image_topmenu";
    public static final String BACKGROUND_IMAGE_LOADING = "background_image_loading";
    public static final String LOGO_IMAGE = "logo_image";
    public static final String COLOR = "color";

    private String id;
    private String title;
    private String urlMain;
    private String urlService;
    private String background_image_topmenu;
    private String background_image_loading;
    private String logo_image;
    private String color;

    public Environment(String id, String title, String urlMain, String urlService, String background_image_topmenu, String background_image_loading, String logo_image, String color) {
        this.id = id;
        this.title = title;
        this.urlMain = urlMain;
        this.urlService = urlService;
        this.background_image_topmenu = background_image_topmenu;
        this.background_image_loading = background_image_loading;
        this.logo_image = logo_image;
        this.color = color;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlMain() {
        return urlMain;
    }

    public void setUrlMain(String urlMain) {
        this.urlMain = urlMain;
    }

    public String getUrlService() {
        return urlService;
    }

    public void setUrlService(String urlService) {
        this.urlService = urlService;
    }

    public String getBackground_image_topmenu() {
        return background_image_topmenu;
    }

    public void setBackground_image_topmenu(String background_image_topmenu) {
        this.background_image_topmenu = background_image_topmenu;
    }

    public String getBackground_image_loading() {
        return background_image_loading;
    }

    public void setBackground_image_loading(String background_image_loading) {
        this.background_image_loading = background_image_loading;
    }

    public String getLogo_image() {
        return logo_image;
    }

    public void setLogo_image(String logo_image) {
        this.logo_image = logo_image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
