package com.adjaran.app.Classes;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KGelashvili on 7/10/2015.
 */
public class NetworkDAO {
    private final OkHttpClient client = new OkHttpClient();

    public String request(String uri) throws IOException {
        Request request = new Request.Builder()
                .url(uri)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().string();
    }
    public InputStream requestWithTempFile(String uri) throws IOException {

        URL url = new URL("http://www.android.com/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        return new BufferedInputStream(urlConnection.getInputStream());
    }


}
