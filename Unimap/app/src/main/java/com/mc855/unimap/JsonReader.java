package com.mc855.unimap;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

    private final Context context;

    public JsonReader(Context context) {
        this.context = context;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONArray readJsonArray(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = null;
            try {
                json = new JSONArray(jsonText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONObject getJson(String params) throws JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;


        try {
            String jsonText = "";
            URL url = new URL(params);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            connection.setRequestProperty("Content-Type", "application/json");



            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                jsonText += line;
                buffer.append(line+"\n");
                //Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
            }

            return new JSONObject(jsonText);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String login(String URL_STRING, String urlServer){
        try {
            CookieManager cookieManager = new CookieManager();

            CookieHandler.setDefault(cookieManager);
            URL url = new URL(urlServer + URL_STRING);
            System.out.println("URL: " + url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.getContent();
            CookieStore cookieStore = cookieManager.getCookieStore();
            List cookieList = cookieStore.getCookies();
            // iterate HttpCookie object
            for (int i = 0; i < cookieList.size(); i++){
                System.out.println("Domain: " + cookieList.get(i).toString());
                connection.disconnect();
                return  cookieList.get(i).toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
/*
    public String reLogin(String idUser){
        UserDataBase UDB = new UserDataBase(context);

        User user = UDB.findById(idUser);
        //TODO: String idsession = login("/j_spring_security_check?j_username=" + user.getEmail() + "&j_password=" + user.getSenha(),
        String token = login("/j_spring_security_check?j_username=gunter@email.com&j_password=senhaboa", getUrlServer(user.getIdEnvironment()));

        if(token != null){
            //alert(token);
            if(token.contains("JSESSIONID")) {
                //TODO: UDB.updateToken(token, idUser);
                return token;
            }
        }

        return UDB.getToken(idUser);
    }

    private String getUrlServer(String idEnvironment) {
        EnvironmentDataBase EDB = new EnvironmentDataBase(context);
        String url = EDB.findById(idEnvironment).getUrlMain();
        System.out.println("URLServer: " + url.substring(0, url.indexOf("/", 7)));
        return url.substring(0, url.indexOf("/", 7));
    }*/

}
