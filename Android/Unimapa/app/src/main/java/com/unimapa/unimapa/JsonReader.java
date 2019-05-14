package com.unimapa.unimapa;

import android.content.Context;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.unimapa.unimapa.domain.Mapa;

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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

    private final Context context;

    public JsonReader(Context context) {
        this.context = context;
    }


    public static ArrayList<Mapa> getMapas(String url){
        ArrayList<Mapa> mapas = new ArrayList<Mapa>();
        try {
            JSONArray mapasJson = readJsonArray(url);

            for(int i = 0; i < mapasJson.length(); i++) {
                Gson gson = new Gson(); // Or use new GsonBuilder().create();

                Mapa mapa = gson.fromJson(String.valueOf(mapasJson.getJSONObject(i)), Mapa.class); // deserializes json into target2
                mapa.setSelected(false);
                mapas.add(mapa);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException ex) {
            ex.printStackTrace();
        }

        return mapas;
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

    private static String readAll(Reader rd) throws IOException {

        //TODO: para dar permeissao para acesso http
        /*if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        StringBuilder sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
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


}
