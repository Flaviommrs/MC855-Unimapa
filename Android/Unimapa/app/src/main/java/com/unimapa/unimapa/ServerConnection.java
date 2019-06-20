package com.unimapa.unimapa;

import android.content.Context;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.unimapa.unimapa.dataBase.UserDataBase;
import com.unimapa.unimapa.domain.Mapa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerConnection {

    private Context context;

    public ServerConnection(Context context) {
        this.context = context;
    }

    //public static final String BASE_URL = "http://a01fa1b5.ngrok.io";
    public static final String BASE_URL = "https://ac820fm2ig.execute-api.us-east-1.amazonaws.com";

    public ArrayList<Mapa> getMapas(){
        ArrayList<Mapa> mapas = new ArrayList<Mapa>();
        try {
            System.out.println(sendJson("/maps", "", "GET"));
            JSONArray mapasJson = sendJson("/maps", "", "GET").getJSONArray("maps");

            for(int i = 0; i < mapasJson.length(); i++) {
                Gson gson = new Gson(); // Or use new GsonBuilder().create();

                Mapa mapa = gson.fromJson(String.valueOf(mapasJson.getJSONObject(i)), Mapa.class); // deserializes json into target2
                mapa.setSelected(false);
                mapas.add(mapa);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return mapas;
    }

    public JSONArray readJsonArray(String params, String json, String method) throws JSONException{

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        UserDataBase UDB = new UserDataBase(context);
        String token = UDB.getToken();

        try {
            String jsonText = "";
            URL url = new URL(BASE_URL + params);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod(method);
            connection.connect();

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json);
            wr.flush();
            wr.close();

            if (connection.getResponseCode() < 400) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String jT = readAll(reader);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(jT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;

            }else {
                System.out.println("ERROO ao pfazer o post, ERRO_CODIGO: " + connection.getResponseCode());
                return null;
            }

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

    private String readAll(Reader rd) throws IOException {

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

    public JSONObject getJson(String params) throws JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;


        try {
            String jsonText = "";
            URL url = new URL(params);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            connection.setRequestMethod("POST");
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

    public JSONObject sendJson(String params, String json, String method) throws JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        UserDataBase UDB = new UserDataBase(context);
        String token = UDB.getToken();

        try {
            String jsonText = "";
            URL url = new URL(BASE_URL + params);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            //connection.setRequestProperty("Cookie", login("http://ec2-54-189-74-87.us-west-2.compute.amazonaws.com:8080/j_spring_security_check?j_username=df@email.com&j_password=df"));
            connection.setRequestMethod(method);
            connection.connect();

            if(method.equals("POST")) {
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();
            }


            if (connection.getResponseCode() < 400) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    jsonText += line;
                    buffer.append(line + "\n");
                }

                return new JSONObject(jsonText);

            }else {
                System.out.println("ERROO ao pfazer o post, ERRO_CODIGO: " + connection.getResponseCode());
                return null;
            }

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
