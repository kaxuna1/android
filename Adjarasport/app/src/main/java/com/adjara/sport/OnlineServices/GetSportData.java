package com.adjara.sport.OnlineServices;

import com.adjara.sport.Models.SportVideModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vakhtanggelashvili on 8/22/15.
 */
public class GetSportData {
    ArrayList<SportVideModel> getNewVideos(){
        ArrayList<SportVideModel> sportVideModels=new ArrayList<SportVideModel>();
        String url ="http://sport.adjaranet.com/req/jsondata/req.php?reqId=newVideos";
        NetworkDAO networkDAO=new NetworkDAO();
        try {
            String rawMoviesData=networkDAO.request(url);
            JSONArray array=new JSONArray(rawMoviesData);

            for (int i=0;i<array.length();i++){
                JSONObject videoJ=array.getJSONObject(i);

                SportVideModel video=new SportVideModel();
                video.id=videoJ.getString("id");
                video.date=videoJ.getString("date");
                video.category_id=videoJ.getString("category_id");
                video.name=videoJ.getString("name");
                video.file_url=videoJ.getString("file_url");
                video.views=videoJ.getString("views");


                sportVideModels.add(video);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return sportVideModels;

    }
}
