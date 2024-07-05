package com.example.restaurant;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HTTPHandler {

    public HTTPHandler(){

    }

    public String getAccess(String url){
        String response = null;
        URL u = null;

        try {
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");

            InputStream i = new BufferedInputStream(conn.getInputStream());
//            response = new String(i.readAllBytes(), StandardCharsets.UTF_8);
            response = converttoString(i);
            //Log.d("REST01", "Result "+response);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String postAccess(String url, ArrayList<String> key, ArrayList value){
        String response = null;
        URL u = null;

        try {
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            JSONObject params = new JSONObject();
            for (int x = 0; x < key.size(); x++){
                params.put(key.get(x), value.get(x));
            }
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(params.toString());
            wr.flush();

            InputStream i = new BufferedInputStream(conn.getInputStream());
//            BufferedReader in = new BufferedReader(new InputStreamReader(i));
//            StringBuffer r = new StringBuffer();
//            while ((response=in.readLine())!=null){
//                r.append(response);
//            }
//            response = r.toString();

            response = converttoString(i);
            //Log.d("REST01", response);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String putAccess(String url, ArrayList<String> key, ArrayList value) {
        String response = null;
        URL u = null;

        try {
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            JSONObject params = new JSONObject();
            for (int x = 0; x < key.size(); x++) {
                params.put(key.get(x), value.get(x));
            }
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(params.toString());
            wr.flush();

            InputStream i = new BufferedInputStream(conn.getInputStream());
            response = converttoString(i);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String deleteAccess(String url) {
        String response = null;
        URL u = null;

        try {
            u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("DELETE");

            // Mengambil respons dari server
            InputStream i = new BufferedInputStream(conn.getInputStream());
            response = converttoString(i);

            // Jika perlu, bisa ditambahkan log atau handling respons dari server
            // Log.d("REST", "Result " + response);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }


    private String converttoString(InputStream i) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(i));
        StringBuilder sb = new StringBuilder();
        String dummy;

        while ((dummy=r.readLine())!=null){
            sb.append(dummy).append("\n");
        }
        return sb.toString();
    }
}
