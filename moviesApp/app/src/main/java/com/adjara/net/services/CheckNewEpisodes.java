package com.adjara.net.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.adjara.net.broadcastreceivers.CheckEpisodesBroadcastReceiver;
import com.adjara.net.Classes.MovieServices;
import com.adjara.net.MainActivity;
import com.adjara.net.MoviePageActivity;
import com.adjara.net.R;
import com.adjara.net.model.Episode;
import com.adjara.net.model.Movie;
import com.adjara.net.model.Season;
import com.adjara.net.model.Serie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

public class CheckNewEpisodes extends Service {
    private static final String MyOnClick = "myOnClickTag";


    public CheckNewEpisodes() {

    }
    SharedPreferences preferences;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        AlarmManager am=(AlarmManager)CheckNewEpisodes.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(CheckNewEpisodes.this, CheckEpisodesBroadcastReceiver.class);
        //intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60*60*preferences.getInt("interval",6)), pi);


        if(preferences.getBoolean("newMovies",true))
        showNotificationNewAddedMovies();
        if(preferences.getBoolean("premiereMovies",true))
        showNotificationPremiereMovies();
        if(preferences.getBoolean("newGeoMovies",true))
        showNotificationNewGeoMovies();



        new Thread(new Runnable() {
            @Override
            public void run() {

                new CheckForNewSeriesAsync().doInBackground("");
            }
        }).run();



        return 1;
    }

    private void checkForNewEpisodes() {
        final List<Serie> seriesToBeChecked=Serie.listAll(Serie.class);
        for(int e=0;e<seriesToBeChecked.size();e++){
            final Serie serieToBeChecked=seriesToBeChecked.get(e);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Season> seasons=new ArrayList<Season>();
                    Elements value=new GetSeriesData().doInBackground(serieToBeChecked.getMovieId());
                    int i=1;
                    while (value.select("#sDiv"+i).hasAttr("class")){
                        int f=0;
                        Season season =new Season("Season "+i);

                        int episodes=value.select("#sDiv"+i).select("span").size();
                        while (f<episodes){
                            season.addEpisode(new Episode(value.select("#sDiv"+i).select("span").get(f).attr("data-href"),
                                    value.select("#sDiv"+i).select("span").get(f).attr("data-lang"),"სერია  "+(f+1)));
                            f++;
                        }
                        seasons.add(season);
                        i++;
                    }
                    int lastSes=seasons.size();
                    int lastEp=seasons.get(seasons.size()-1).getEpisodes().size();

                    if(serieToBeChecked.lastSes<lastSes){
                        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                                .setContentTitle(serieToBeChecked.getTitle_en()) // title for notification
                                .setContentText("გამოვიდა ახალის სეზონი "+lastSes) // message for notification
                                .setAutoCancel(true); // clear notification after click
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(Integer.parseInt(serieToBeChecked.getMovieId()), mBuilder.build());
                    }
                    if(serieToBeChecked.lastEp<lastEp&&serieToBeChecked.lastSes==lastSes){
                        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                                .setContentTitle(serieToBeChecked.getTitle_en()) // title for notification
                                .setContentText("გამოვიდა ახალის ეპიზოდი "+lastEp) // message for notification
                                .setAutoCancel(true); // clear notification after click
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(Integer.parseInt(serieToBeChecked.getMovieId()), mBuilder.build());
                    }
                    serieToBeChecked.setLastSes(lastSes);
                    serieToBeChecked.setLastEp(lastEp);
                    serieToBeChecked.save();

                }
            }).start();


        }


    }

    private void showNotificationNewGeoMovies() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                new getNewGeoMovies().doInBackground("");

            }
        }).start();
    }

    private void showNotificationPremiereMovies() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                new getPremiereMovies().doInBackground("");

            }
        }).start();

    }

    public void showNotificationNewAddedMovies() {



        new Thread(new Runnable() {
            @Override
            public void run() {
                new getNewAddedMovies().doInBackground("");

            }
        }).start();





    }


    public class NotificationImageAsyncTask extends AsyncTask<String, ArrayList<Bitmap>, ArrayList<Bitmap>> {


        @Override protected ArrayList<Bitmap> doInBackground(String... params) {

            ArrayList<Bitmap> bitmaps=new ArrayList<>();
            for(int i=0;i<5;i++){
                Bitmap bm = null;
                try {
                    URL aURL = new URL(params[i]);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                }
                bitmaps.add(bm);
            }

            publishProgress(bitmaps);
            return bitmaps;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Bitmap>... value) {
            super.onProgressUpdate(value);





        }

    }

    class getNewAddedMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getNewAddedMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(final ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            final String[] posters=new String[5];

            final Movie[] movies2=values[0].toArray(new Movie[values[0].size()]);
            for (int i = 0; i < 5; i++) {
                posters[i]=movies2[i].getPoster();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<Bitmap> bitmaps=new NotificationImageAsyncTask().doInBackground(posters);
                    final RemoteViews contentView=new RemoteViews(CheckNewEpisodes.this.getPackageName(), R.layout.notification);
                    for(int f=0;f<4;f++){
                        Bitmap bm=bitmaps.get(f);
                        int id = 0;
                        switch (f){
                            case 0:id=R.id.imageButton31;
                                break;
                            case 1:id=R.id.imageButton32;
                                break;
                            case 2:id=R.id.imageButton33;
                                break;
                            case 3:id=R.id.imageButton34;
                                break;
                        }
                        contentView.setImageViewBitmap(id, bm);
                        Intent intent2 = new Intent(CheckNewEpisodes.this, MoviePageActivity.class);
                        intent2.putExtra("movieId", movies2[f].getMovieId());
                        intent2.putExtra("description", movies2[f].getDescription());
                        intent2.putExtra("title", movies2[f].getTitle_en());
                        intent2.putExtra("date", movies2[f].getRelease_date());
                        intent2.putExtra("duration", movies2[f].getDuration());
                        intent2.putExtra("rating", movies2[f].getImdb());
                        intent2.putExtra("imdb", movies2[f].getImdb_id());
                        intent2.putExtra("lang", movies2[f].getLang());
                        intent2.putExtra("time", 0);
                        intent2.putExtra("Movie", movies2[f]);


                        //Intent intent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(CheckNewEpisodes.this, f, intent2,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        contentView.setOnClickPendingIntent(id, pendingIntent);






                       /* PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
                        contentView.setOnClickPendingIntent(id,pi);*/


                    }
                    Intent targetIntent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(CheckNewEpisodes.this, 0,
                            targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification foregroundNote;

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(CheckNewEpisodes.this);
                    foregroundNote=
                            builder.setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("My Notification Title")
                                            //.setContentText("Something interesting happened")
                                            //.addAction(R.drawable.full_screen, "გახსნა", contentIntent)
                                    .setAutoCancel(true)
                                    .setContent(contentView).setContentIntent(contentIntent).build();
                    foregroundNote.bigContentView=contentView;

                    int NOTIFICATION_ID = 1;

                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                    nManager.notify(NOTIFICATION_ID, foregroundNote);


                }
            }).start();

        }

    }

    class getPremiereMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getPremierMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(final ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            final String[] posters=new String[5];

            final Movie[] movies2=values[0].toArray(new Movie[values[0].size()]);
            for (int i = 0; i < 5; i++) {
                posters[i]=movies2[i].getPoster();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<Bitmap> bitmaps=new NotificationImageAsyncTask().doInBackground(posters);
                    final RemoteViews contentView=new RemoteViews(CheckNewEpisodes.this.getPackageName(), R.layout.notification);
                    for(int f=0;f<4;f++){
                        Bitmap bm=bitmaps.get(f);
                        int id = 0;
                        switch (f){
                            case 0:id=R.id.imageButton31;
                                break;
                            case 1:id=R.id.imageButton32;
                                break;
                            case 2:id=R.id.imageButton33;
                                break;
                            case 3:id=R.id.imageButton34;
                                break;
                        }
                        contentView.setImageViewBitmap(id, bm);
                        contentView.setTextViewText(R.id.textViewNotification, "პრემიერები");
                        Intent intent2 = new Intent(CheckNewEpisodes.this, MoviePageActivity.class);
                        intent2.putExtra("movieId", movies2[f].getMovieId());
                        intent2.putExtra("description", movies2[f].getDescription());
                        intent2.putExtra("title", movies2[f].getTitle_en());
                        intent2.putExtra("date", movies2[f].getRelease_date());
                        intent2.putExtra("duration", movies2[f].getDuration());
                        intent2.putExtra("rating", movies2[f].getImdb());
                        intent2.putExtra("imdb", movies2[f].getImdb_id());
                        intent2.putExtra("lang", movies2[f].getLang());
                        intent2.putExtra("time", 0);
                        intent2.putExtra("Movie", movies2[f]);


                        //Intent intent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(CheckNewEpisodes.this, f*2,
                                intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                        contentView.setOnClickPendingIntent(id, pendingIntent);






                       /* PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
                        contentView.setOnClickPendingIntent(id,pi);*/


                    }
                    Intent targetIntent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(CheckNewEpisodes.this, 0, targetIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification foregroundNote;

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(CheckNewEpisodes.this);
                    foregroundNote=
                            builder.setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("My Notification Title")
                                            //.setContentText("Something interesting happened")
                                            //.addAction(R.drawable.full_screen, "გახსნა", contentIntent)
                                    .setAutoCancel(true)
                                    .setContent(contentView).setContentIntent(contentIntent).build();
                    foregroundNote.bigContentView=contentView;

                    int NOTIFICATION_ID = 2;

                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                    nManager.notify(NOTIFICATION_ID, foregroundNote);


                }
            }).start();

        }

    }

    class getNewGeoMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getLatestGeoMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(final ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            final String[] posters=new String[5];

            final Movie[] movies2=values[0].toArray(new Movie[values[0].size()]);
            for (int i = 0; i < 5; i++) {
                posters[i]=movies2[i].getPoster();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<Bitmap> bitmaps=new NotificationImageAsyncTask().doInBackground(posters);
                    final RemoteViews contentView=new RemoteViews(CheckNewEpisodes.this.getPackageName(), R.layout.notification);
                    for(int f=0;f<4;f++){
                        Bitmap bm=bitmaps.get(f);
                        int id = 0;
                        switch (f){
                            case 0:id=R.id.imageButton31;
                                break;
                            case 1:id=R.id.imageButton32;
                                break;
                            case 2:id=R.id.imageButton33;
                                break;
                            case 3:id=R.id.imageButton34;
                                break;
                        }
                        contentView.setImageViewBitmap(id, bm);
                        contentView.setTextViewText(R.id.textViewNotification,"ახალი ფილმები ქართულად");
                        Intent intent2 = new Intent(CheckNewEpisodes.this, MoviePageActivity.class);
                        intent2.putExtra("movieId", movies2[f].getMovieId());
                        intent2.putExtra("description", movies2[f].getDescription());
                        intent2.putExtra("title", movies2[f].getTitle_en());
                        intent2.putExtra("date", movies2[f].getRelease_date());
                        intent2.putExtra("duration", movies2[f].getDuration());
                        intent2.putExtra("rating", movies2[f].getImdb());
                        intent2.putExtra("imdb", movies2[f].getImdb_id());
                        intent2.putExtra("lang", movies2[f].getLang());
                        intent2.putExtra("time", 0);
                        intent2.putExtra("Movie", movies2[f]);


                        //Intent intent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(CheckNewEpisodes.this, f*3, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                        contentView.setOnClickPendingIntent(id, pendingIntent);






                       /* PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
                        contentView.setOnClickPendingIntent(id,pi);*/


                    }
                    Intent targetIntent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(CheckNewEpisodes.this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification foregroundNote;

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(CheckNewEpisodes.this);
                    foregroundNote=
                            builder.setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("My Notification Title")
                                            //.setContentText("Something interesting happened")
                                            //.addAction(R.drawable.full_screen, "გახსნა", contentIntent)
                                    .setAutoCancel(true)
                                    .setContent(contentView).setContentIntent(contentIntent).build();
                    foregroundNote.bigContentView=contentView;

                    int NOTIFICATION_ID = 3;

                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                    nManager.notify(NOTIFICATION_ID, foregroundNote);


                }
            }).start();

        }

    }

    class CheckForNewSeriesAsync extends AsyncTask<String,ArrayList<Card>,ArrayList<Card>> {

        @Override
        protected ArrayList<Card> doInBackground(String... params) {
            checkForNewEpisodes();
            return null;
        }
    }

    private class GetSeriesData extends AsyncTask<String,Elements, Elements> {


        @Override
        protected Elements doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect("http://adjaranet.com/Movie/main?id=" + params[0] + "&serie=1&js=1").get();
                Elements newsHeadlines = doc.select("#episodesDiv");
                return newsHeadlines;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
