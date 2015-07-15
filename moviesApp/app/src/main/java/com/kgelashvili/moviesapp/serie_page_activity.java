package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class serie_page_activity extends Activity {

    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_page_activity);
        final Bundle extras = getIntent().getExtras();
        id=extras.getString("movieId");

        final GetSeriesData getSeries=new GetSeriesData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSeries.doInBackground();

            }
        }).start();





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serie_page_activity, menu);
        return true;
    }

    private class GetSeriesData extends AsyncTask<String,Elements, Elements> {


        @Override
        protected Elements doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect("http://adjaranet.com/Movie/main?id="+id+"&serie=1&js=1").get();
                Elements newsHeadlines = doc.select("#sDiv1");
                publishProgress(newsHeadlines);
                Log.d("kaxaHtml",newsHeadlines.html());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Elements... values) {
            super.onProgressUpdate(values);
            setSeriesData(values[0]);

        }
    }

    private void setSeriesData(Elements value) {
        Log.d("kaxaHtml2",value.html());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
