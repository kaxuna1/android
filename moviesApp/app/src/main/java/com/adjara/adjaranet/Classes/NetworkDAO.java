package com.adjara.adjaranet.Classes;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

/**
 * Created by KGelashvili on 7/10/2015.
 */
public class NetworkDAO {
    public String request(String uri) throws IOException {

        int timeoutConnection = 5000;

// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 4000;

// set timeout parameters for HttpClient
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);


        HttpGet httpGet=new HttpGet(uri);
        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        HttpClient httpClient =new DefaultHttpClient(httpParameters);

        String response=httpClient.execute(httpGet, responseHandler);
        return response;
    }
    public String requestWithTempFile(String uri) throws IOException {

        HttpGet httpGet=new HttpGet(uri);
        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        HttpClient httpClient =new DefaultHttpClient();
        String response=httpClient.execute(httpGet, responseHandler);
        return response;
    }


}
