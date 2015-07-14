package com.kgelashvili.moviesapp.Classes;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by KGelashvili on 7/10/2015.
 */
public class NetworkDAO {
    public String request(String uri) throws IOException {

        HttpGet httpGet=new HttpGet(uri);
        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        HttpClient httpClient =new DefaultHttpClient();
        String response=httpClient.execute(httpGet, responseHandler);
        return response;
    }
}
