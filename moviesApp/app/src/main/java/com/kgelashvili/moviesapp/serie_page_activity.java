package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.kgelashvili.moviesapp.Classes.FloatingActionButton;
import com.kgelashvili.moviesapp.cards.CustomThumbCard;
import com.kgelashvili.moviesapp.model.Episode;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Season;
import com.kgelashvili.moviesapp.model.Serie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class serie_page_activity extends Activity {
    private static ProgressDialog progressDialog;
    String id;
    ArrayList<Season> seasons;
    int currentSeason=1;
    String currentLangs="";
    String currentLang="";
    ArrayList<Episode> currentEpisodes=new ArrayList<Episode>();
    String videourl="";
    VideoView videoView;
    int movieTime=0;
    EpisodesListAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/bpg_square_mtavruli_2009.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_serie_page_activity);
        seasons=new ArrayList<Season>();
        final Bundle extras = getIntent().getExtras();
        id=extras.getString("movieId");
       // getActionBar().setTitle(extras.getString("title"));
        final FloatingActionButton mFab = new FloatingActionButton.Builder(serie_page_activity.this)
                .withColor(getResources().getColor(R.color.primary))
                .withDrawable(getResources().getDrawable(R.drawable.androidfullscreen))
                .withSize(72)
                .withMargins(0, 0, 16, 16)
                .create();
        ((Button)findViewById(R.id.downloadBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = videourl;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        TextView date=(TextView)findViewById(R.id.movieDate);
        date.setText(extras.getString("date"));
        ((TextView) findViewById(R.id.imdbRating)).setText(extras.getString("rating"));
        ((ImageView)findViewById(R.id.imdbImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + extras.getString("imdb")));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.descriptionTxt)).setText(extras.getString("description"));


        mFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
            public void onClick(View v) {
                Intent i=new Intent(serie_page_activity.this,FullScreenMovie.class);
                i.putExtra("movieId",extras.getString("movieId"));
                i.putExtra("description",extras.getString("description"));
                i.putExtra("title",extras.getString("title"));
                i.putExtra("date",extras.getString("date"));
                i.putExtra("duration",extras.getString("duration"));
                i.putExtra("rating",extras.getString("rating"));
                i.putExtra("imdb",extras.getString("imdb"));
                i.putExtra("time", videoView.getCurrentPosition());
                i.putExtra("link",videourl);


                startActivityForResult(i, 0);
            }
        });
        final GetSeriesData getSeries=new GetSeriesData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSeries.doInBackground();

            }
        }).start();

        ListView episodesListView=(ListView)findViewById(R.id.listViewSerie);
        adapter=new EpisodesListAdapter();
        videoView = (VideoView) findViewById(R.id.myserieview);
        episodesListView.setAdapter(adapter);
        episodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                currentLang=currentEpisodes.get(position).getLang().split(",")[0];
                videourl=currentEpisodes.get(position).getLink().replace("{L}",currentEpisodes.get(position).getLang().split(",")[0]);
                Log.d("currentSerieUrl",videourl);
                progressDialog = ProgressDialog.show(serie_page_activity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
                progressDialog.setCancelable(true);
                //getActionBar().setTitle(extras.getString("title") + " " + currentEpisodes.get(position).getName());
                ((Button)findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                        builder.setTitle("აირჩიეთ ენა")
                                .setItems(currentEpisodes.get(position).getLang().split(","), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        movieTime = videoView.getCurrentPosition();
                                        String lang = currentEpisodes.get(position).getLang().split(",")[which];
                                        videourl=currentEpisodes.get(position).getLink().replace("{L}",lang);
                                        Uri video = Uri.parse(videourl);
                                        videoView.setVideoURI(video);
                                        videoView.requestFocus();
                                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                            public void onPrepared(MediaPlayer mp) {
                                                progressDialog.dismiss();
                                                videoView.start();
                                                videoView.seekTo(movieTime);
                                            }
                                        });



                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                });
                PlayVideo();
            }
        });




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        movieTime=resultCode;
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void PlayVideo()
    {
        try
        {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);



            final Uri video = Uri.parse(videourl);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {
                    videoView.start();
                    progressDialog.dismiss();
                    videoView.seekTo(movieTime);
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            movieTime = mediaPlayer.getCurrentPosition();
                            Log.d("kaxaError", "error");
                            PlayVideo();
                            return true;
                        }
                    });
                    MediaController mediaController = new MediaController(serie_page_activity.this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                }
            });

        }
        catch(Exception e)
        {
            System.out.println("Video Play Error :" + e.toString());
            //finish();
        }

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
                Elements newsHeadlines = doc.select("#episodesDiv");
                publishProgress(newsHeadlines);
                //Log.d("kaxaHtml",newsHeadlines.html());
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
        //Log.d("kaxaHtml2",value.select("#sDiv2"));
        int i=1;

        while (value.select("#sDiv"+i).hasAttr("class")){
           //Log.d("kaxaHTML"+i,value.select("#sDiv"+i).html());
            //Log.d("kaxaHtml" + i, value.select("#sDiv" + i).select("span").get(1).attr("data-href"));
            int f=0;
            Season season =new Season("Season "+i);

            int episodes=value.select("#sDiv"+i).select("span").size();
            while (f<episodes){
                Log.d("kaxaHtml"+f,value.select("#sDiv"+i).select("span").get(f).attr("data-href"));

                season.addEpisode(new Episode(value.select("#sDiv"+i).select("span").get(f).attr("data-href"),
                        value.select("#sDiv"+i).select("span").get(f).attr("data-lang"),"სერია  "+(f+1)));
                f++;
            }
            seasons.add(season);
            i++;
        }
        final String[] seasonNames=new String[seasons.size()];
        for(int k=0;k<seasons.size();k++){
            seasonNames[k]=seasons.get(k).getName();
        }
        adapter.clear();
        for(int e=0;e<seasons.get(0).getEpisodes().size();e++){
            adapter.add(seasons.get(0).getEpisode(e));
        }

        ((Button) findViewById(R.id.seasonChangeBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                builder.setTitle("აირჩიეთ სეზონი")
                        .setItems(seasonNames, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                adapter.clear();
                                for(int e=0;e<seasons.get(which).getEpisodes().size();e++){
                                    adapter.add(seasons.get(which).getEpisode(e));
                                }
                                currentSeason=(which);

                            }
                        });
                builder.create();
                builder.show();
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class EpisodesListAdapter extends ArrayAdapter<Episode> {
        public EpisodesListAdapter() {
            super(serie_page_activity.this, R.layout.itemview, currentEpisodes);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.episodeslistitem, viewGroup, false);
            final Episode currentEpisode = currentEpisodes.get(position);
            ((TextView)view.findViewById(R.id.episodeTitle)).setText(currentEpisode.getName());
            return view;
        }
    }
}
