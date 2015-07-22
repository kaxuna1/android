package com.kgelashvili.moviesapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Switch;

import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.model.Movie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CheckNewEpisodes extends Service {

    public CheckNewEpisodes() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showNotificationNewMovies();
        return 1;
    }

    public void showNotificationNewMovies() {

        Log.d("notificationd3","image");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("notificationd2","image");
                new getNewAddedMovies().doInBackground("");

            }
        }).start();





    }
    public class NotificationImageAsyncTask extends AsyncTask<String, ArrayList<Bitmap>, ArrayList<Bitmap>> {


        @Override protected ArrayList<Bitmap> doInBackground(String... params) {
            Log.d("notificationd1", "image");
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
                    Log.d("notificationd4","image");
                }
                bitmaps.add(bm);
            }

            publishProgress(bitmaps);
            return bitmaps;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Bitmap>... value) {
            super.onProgressUpdate(value);
            Log.d("notificationd", "image");





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
            Log.d("kaxaGeo1", "kaxaGeo1");
            for (int i = 0; i < 5; i++) {
                posters[i]=values[0].get(i).getPoster();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("notificationd2","image");
                    ArrayList<Bitmap> bitmaps=new NotificationImageAsyncTask().doInBackground(posters);
                    final RemoteViews contentView=new RemoteViews(CheckNewEpisodes.this.getPackageName(), R.layout.notification);
                    for(int i=0;i<5;i++){
                        Bitmap bm=bitmaps.get(i);
                        int id;
                        switch (i){
                            case 0:id=R.id.imageButton31;
                                break;
                            case 1:id=R.id.imageButton32;
                                break;
                            case 2:id=R.id.imageButton33;
                                break;
                            case 3:id=R.id.imageButton34;
                                break;
                            case 4:id=R.id.imageButton35;
                                break;
                            default:id=R.id.imageButton31;
                                break;
                        }
                        contentView.setImageViewBitmap(id, bm);
                        Intent intent2 = new Intent(CheckNewEpisodes.this, MoviePageActivity.class);
                        intent2.putExtra("movieId", values[0].get(i).getId());
                        intent2.putExtra("description", values[0].get(i).getDescription());
                        intent2.putExtra("title", values[0].get(i).getTitle_en());
                        intent2.putExtra("date", values[0].get(i).getRelease_date());
                        intent2.putExtra("duration", values[0].get(i).getDuration());
                        intent2.putExtra("rating", values[0].get(i).getImdb());
                        intent2.putExtra("imdb", values[0].get(i).getImdb_id());
                        intent2.putExtra("lang", values[0].get(i).getLang());
                        intent2.putExtra("time", 0);

                       /* PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
                        contentView.setOnClickPendingIntent(id,pi);*/
                        contentView.setOnClickFillInIntent(id,intent2);

                    }
                    Intent targetIntent = new Intent(CheckNewEpisodes.this, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(CheckNewEpisodes.this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(CheckNewEpisodes.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("My Notification Title")
                                            //.setContentText("Something interesting happened")
                                            //.addAction(R.drawable.full_screen, "გახსნა", contentIntent)
                                    .setAutoCancel(true)
                                    .setContent(contentView);
                    int NOTIFICATION_ID = 1;



                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                    nManager.notify(NOTIFICATION_ID, builder.build());
                    AlarmManager am=(AlarmManager)CheckNewEpisodes.this.getSystemService(Context.ALARM_SERVICE);
                    Intent intent2 = new Intent(CheckNewEpisodes.this, CheckEpisodesBroadcastReceiver.class);
                    //intent.putExtra(ONE_TIME, Boolean.TRUE);
                    PendingIntent pi = PendingIntent.getBroadcast(CheckNewEpisodes.this, 0, intent2, 0);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60*30), pi);
                }
            }).start();

        }

    }
}
