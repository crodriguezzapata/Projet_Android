package com.example.rc611000.mymap;

import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rc611000 on 24/03/2017.
 */

public class WebService {
    Gson gson;

    public WebService(){
        Gson gson = new Gson();
    }

    public InputStream sendRequest(URL url) throws Exception{
        try{
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return  urlConnection.getInputStream();
            }
        }
        catch (Exception e){
            Log.e("WebService","Erreur de Connexion");
            throw new Exception("");
        }
        return null;
    }
}
