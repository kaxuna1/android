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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.FloatingActionButton;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.ScrollViewExt;
import com.kgelashvili.moviesapp.Classes.ScrollViewListener;
import com.kgelashvili.moviesapp.cards.CustomThumbCard;
import com.kgelashvili.moviesapp.model.Actor;
import com.kgelashvili.moviesapp.model.Episode;
import com.kgelashvili.moviesapp.model.HistoryModel;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.MovieSerieLastMomentModel;
import com.kgelashvili.moviesapp.model.Season;
import com.kgelashvili.moviesapp.model.Serie;
import com.kgelashvili.moviesapp.model.SeriesDataModel;
import com.nineoldandroids.animation.Animator;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class serie_page_activity extends AppCompatActivity {
    private static ProgressDialog progressDialog;
    String id;
    String qual;
    String lang;
    ArrayList<Season> seasons;
    int currentSeason=1;
    int currentEpisode=0;
    String currentLangs="";
    String currentLang="";
    ArrayList<Episode> currentEpisodes=new ArrayList<Episode>();
    String videourl="";
    VideoView videoView;
    int movieTime=0;
    EpisodesListAdapter adapter;
    Serie serie;
    LinearLayout castLayout;
    TabHost tabHost=null;
    private WebView webView;
    ListView episodesListView;
    LinearLayout relatedLayout;
    MovieSerieLastMomentModel movieSerieLastMomentModel;
    ImageButton playButton=null;
    TextView director=null;
    String directorText="";
    TextView gener=null;
    String generText="";


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
        playButton=(ImageButton)findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(serie_page_activity.this, "მიმდინარეობს ფილმის ჩატვირთვა", Toast.LENGTH_LONG).show();
            }
        });


        startPlayer();




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        movieTime=resultCode;
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void PlayVideo() {
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
                    playButton.setVisibility(View.GONE);
                    videoView.start();
                    progressDialog.dismiss();
                    videoView.seekTo(movieTime);
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            movieTime = mediaPlayer.getCurrentPosition();

                            PlayVideo();
                            return true;
                        }
                    });

                    ScrollViewExt scrollViewExt=(ScrollViewExt)findViewById(R.id.scrollView2);

                    final MediaController mediaController = new MediaController(serie_page_activity.this,true);

                    scrollViewExt.setScrollViewListener(new ScrollViewListener() {
                        @Override
                        public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
                            mediaController.hide();
                        }
                    });

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

    private class GetSeriesDataJSON extends AsyncTask<String,SeriesDataModel, SeriesDataModel> {


        @Override
        protected SeriesDataModel doInBackground(String... params) {
            Document doc = null;
            SeriesDataModel seriesDataModel=new SeriesDataModel();
            seriesDataModel.jsonObject = new MovieServices().getSerieDataJson(id);
            try {
                doc = Jsoup.connect("http://adjaranet.com/Movie/main?id="+id+"&serie=1&js=1").get();
                Elements newsHeadlines = doc.select("#episodesDiv");
                seriesDataModel.elements=newsHeadlines;
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress(seriesDataModel);


            return null;
        }
        @Override
        protected void onProgressUpdate(SeriesDataModel... values) {
            super.onProgressUpdate(values);
            setSeriesDataJSON(values[0]);

        }
    }

    private void setSeriesDataJSON(SeriesDataModel value) {
        JSONObject jsonValue=value.jsonObject;
        Elements elementValue=value.elements;
        int i=1;
        Iterator<?> keys = jsonValue.keys();
        /*while( keys.hasNext() ) {
            String key = (String)keys.next();
            if(isNumeric(key)){
                Season season =new Season("Season "+key);
                try {
                    JSONObject seasonEpisodesJSON=jsonValue.getJSONObject(key);
                    Iterator<?> keysEpisodes = seasonEpisodesJSON.keys();
                    while (keysEpisodes.hasNext()){
                        String keyEpisode =(String)keysEpisodes.next();
                        JSONObject episodeJSONObject=seasonEpisodesJSON.getJSONObject(keyEpisode);
                        Episode episode=new Episode(episodeJSONObject.getString(""))
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }*/



        while (elementValue.select("#sDiv"+i).hasAttr("class")){

            int f=0;
            Season season =new Season("Season "+i);

            int episodes=elementValue.select("#sDiv"+i).select("span").size();
            while (f<episodes){
                Episode episode=new Episode(elementValue.select("#sDiv"+i).select("span").get(f).attr("data-href"),
                        elementValue.select("#sDiv"+i).select("span").get(f).attr("data-lang"),"სერია  "+(f+1));
                try {
                    JSONObject episodeJSON=jsonValue.getJSONObject(""+i).getJSONObject(""+(f+1));
                    episode.setName(f+". "+episodeJSON.getString("name"));
                    episode.setQual(episodeJSON.getString("quality"));
                    episode.setLang(episodeJSON.getString("lang"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                season.addEpisode(episode);
                f++;
            }
            seasons.add(season);
            i++;
        }
        serie.lastSes=seasons.size();
        serie.lastEp=seasons.get(seasons.size()-1).getEpisodes().size();

        final String[] seasonNames=new String[seasons.size()];
        for(int k=0;k<seasons.size();k++){
            seasonNames[k]=seasons.get(k).getName();
        }
        adapter.clear();
        for(int e=0;e<seasons.get(movieSerieLastMomentModel.getSeason()).getEpisodes().size();e++){
            adapter.add(seasons.get(0).getEpisode(e));
        }


        //playLastEpisode

        currentLang=currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLang().split(",")[0];
        videourl=currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLink().replace("{L}",currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLang().split(",")[0]);
        qual = currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getQual().split(",")[0];
        videourl.replaceAll("_\\d+\\.", "_" + qual + ".");
        movieSerieLastMomentModel.setSerie(movieSerieLastMomentModel.getSerie());

        //getActionBar().setTitle(extras.getString("title") + " " + currentEpisodes.get(position).getName());
        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                builder.setTitle("აირჩიეთ ენა")
                        .setItems(currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLang().split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();

                                lang = currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLang().split(",")[which];
                                videourl=currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getLink().replace("{L}",lang);
                                if(qual==null){
                                    qual = currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getQual().split(",")[0];
                                }
                                videourl.replaceAll("_\\d+\\.","_"+qual+".");
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
        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.qualBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                builder.setTitle("აირჩიეთ ხარისხი")
                        .setItems(currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getQual().split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();
                                //videourl=currentEpisodes.get(position).getLink().replace("{L}", lang);

                                qual = currentEpisodes.get(movieSerieLastMomentModel.getSerie()).getQual().split(",")[which];
                                videourl.replaceAll("_\\d+\\.","_"+qual+".");

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

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.setVisibility(View.GONE);
                progressDialog = ProgressDialog.show(serie_page_activity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
                progressDialog.setCancelable(true);
                PlayVideo();
            }
        });












        //setListViewHeightBasedOnChildren(episodesListView);

        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.seasonChangeBtn)).setOnClickListener(new View.OnClickListener() {
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
                                //setListViewHeightBasedOnChildren(episodesListView);
                                currentSeason=(which);
                                movieSerieLastMomentModel.setSeason(currentSeason);
                                movieSerieLastMomentModel.setSerie(0);
                                movieSerieLastMomentModel.save();

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

    public void onBackPressed() {
        if(tabHost.getCurrentTab()==1){
            if(webView.canGoBack()){
                webView.goBack();
            }else{
                YoYo.with(Techniques.SlideOutRight).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        finish();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).duration(500).playOn(findViewById(R.id.mainfragmentframe));
            }
        }else{
            YoYo.with(Techniques.SlideOutRight).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).duration(500).playOn(findViewById(R.id.mainfragmentframe));
        }


    }


    class getSerieActorsAsync extends AsyncTask<String,ArrayList<Actor>,ArrayList<Actor>>{

        @Override
        protected ArrayList<Actor> doInBackground(String... strings) {
            ArrayList<Actor> actors;
            actors=new MovieServices().getSerieActors(strings[0]);

            directorText=new MovieServices().getDirector(strings[0]);
            generText=new MovieServices().getJanrebi(strings[0]);

            publishProgress(actors);
            return actors;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Actor>... values) {
            super.onProgressUpdate(values);
            director.setText(directorText);
            gener.setText(generText);
            for (int i = 0; i < values[0].size(); i++) {
                addActorToCast(values[0].get(i));
            }

        }
    }

    private void addActorToCast(final Actor actor) {

        LinearLayout linearLayout = castLayout;
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.actorontop, null, false);




        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        //.setTextOverImage(actor.actorName)
                        .useDrawableUrl("http://static.adjaranet.com/cast/"+actor.actorId+".jpg")
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(serie_page_activity.this, ActorMoviesActivity.class);
                i.putExtra("id",actor.actorId);

                startActivityForResult(i, 1);

            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView)layout.findViewById(R.id.actorCardName)).setText(actor.actorName);
        cardView.setCard(card);

        linearLayout.addView(layout);
    }


    class getMovieRelated extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getRelateMovies(strings[0]);
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addMovieToRelatedData(values[0].get(i));
            }
        }

    }

    private void addMovieToRelatedData(final Movie movie) {
        LinearLayout linearLayout = relatedLayout;
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.actorontop, null, false);
        final Serie serie=new Serie(movie.getMovieId(),movie.getTitle_en(),movie.getLink(),movie.getPoster(),movie.getImdb(),movie.getImdb_id(),
                movie.getRelease_date(),movie.getDescription(),movie.getDuration(),movie.getLang());



        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        //.setTextOverImage(actor.actorName)
                        .useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {


                Intent i = new Intent(serie_page_activity.this, serie_page_activity.class);
                i.putExtra("movieId", serie.getMovieId());
                i.putExtra("description", serie.getDescription());
                i.putExtra("title", serie.getTitle_en());
                i.putExtra("date", serie.getRelease_date());
                i.putExtra("duration", serie.getDuration());
                i.putExtra("rating", serie.getImdb());
                i.putExtra("imdb", serie.getImdb_id());
                i.putExtra("lang", serie.getLang());
                i.putExtra("Serie",serie);
                i.putExtra("time", 0);
                i.putExtra("Serie",serie);
                startActivity(i);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView)layout.findViewById(R.id.actorCardName)).setText(movie.getTitle_en());
        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    private void startPlayer(){
        final Bundle extras = getIntent().getExtras();
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec=tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(extras.getString("title"));
        tabHost.addTab(tabSpec);


        tabSpec=tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("IMDB");
        tabHost.addTab(tabSpec);

        relatedLayout=(LinearLayout)findViewById(R.id.relatedMoviesLayout);
        castLayout=(LinearLayout)findViewById(R.id.actorsLayout);
        director=(TextView)findViewById(R.id.directorText);
        gener=(TextView)findViewById(R.id.geners);
        seasons=new ArrayList<Season>();

        id=extras.getString("movieId");
        // getActionBar().setTitle(extras.getString("title"));


        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.downloadBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!videourl.isEmpty()) {
                    String url = videourl;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Toast.makeText(serie_page_activity.this, "გადმოწერისთვის აირჩიეთ სეზონი და ეპიზოდი", Toast.LENGTH_LONG).show();

                }

            }
        });
        serie= (Serie) getIntent().getSerializableExtra("Serie");
        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.getNewsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Serie serie = (Serie) getIntent().getSerializableExtra("Serie");
                List<Serie> serieList = Serie.find(Serie.class, "movie_id='" + serie.movieId + "'");

                if (serieList.size() == 0) {
                    serie.save();
                    Toast.makeText(serie_page_activity.this, "სიახლეები გამოიწერა სერიალ " + serie.getTitle_en() + "-ისთვის", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(serie_page_activity.this, "სიახლეები უკვე გამოიწერა სერიალ " + serie.getTitle_en() + "-ისთვის", Toast.LENGTH_LONG).show();
                }

            }
        });
        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.fullScreenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(serie_page_activity.this, FullScreenMovie.class);
                i.putExtra("movieId", extras.getString("movieId"));
                i.putExtra("description", extras.getString("description"));
                i.putExtra("title", extras.getString("title"));
                i.putExtra("date", extras.getString("date"));
                i.putExtra("duration", extras.getString("duration"));
                i.putExtra("rating", extras.getString("rating"));
                i.putExtra("imdb", extras.getString("imdb"));
                i.putExtra("time", videoView.getCurrentPosition());
                i.putExtra("link", videourl);


                startActivityForResult(i, 0);

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



        final GetSeriesDataJSON getSeries=new GetSeriesDataJSON();

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.imdb.com/title/" + extras.getString("imdb"));

        List<MovieSerieLastMomentModel> movieSerieLastMomentModels=MovieSerieLastMomentModel.find(MovieSerieLastMomentModel.class, "movie_id = '"+serie.getMovieId()+"'");
        if(movieSerieLastMomentModels.size()>0){
            movieSerieLastMomentModel=movieSerieLastMomentModels.get(0);
        }else{
            movieSerieLastMomentModel=new MovieSerieLastMomentModel(serie.getMovieId());
            movieSerieLastMomentModel.time=0;
            movieSerieLastMomentModel.setSeason(0);
            movieSerieLastMomentModel.setSerie(0);
            movieSerieLastMomentModel.save();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSeries.doInBackground();
                new getSerieActorsAsync().doInBackground(serie.getMovieId());
                new getMovieRelated().doInBackground(extras.getString("movieId"));
            }
        }).start();


        episodesListView = (ListView)findViewById(R.id.listViewSerie);
        adapter=new EpisodesListAdapter();
        videoView = (VideoView) findViewById(R.id.myserieview);
        episodesListView.setAdapter(adapter);
        episodesListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        episodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                currentLang=currentEpisodes.get(position).getLang().split(",")[0];
                videourl=currentEpisodes.get(position).getLink().replace("{L}",currentEpisodes.get(position).getLang().split(",")[0]);
                qual = currentEpisodes.get(position).getQual().split(",")[0];
                videourl.replaceAll("_\\d+\\.","_"+qual+".");
                movieSerieLastMomentModel.setSerie(position);
                movieSerieLastMomentModel.save();
                progressDialog = ProgressDialog.show(serie_page_activity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
                progressDialog.setCancelable(true);
                //getActionBar().setTitle(extras.getString("title") + " " + currentEpisodes.get(position).getName());
                ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                        builder.setTitle("აირჩიეთ ენა")
                                .setItems(currentEpisodes.get(position).getLang().split(","), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        movieTime = videoView.getCurrentPosition();

                                        lang = currentEpisodes.get(position).getLang().split(",")[which];
                                        videourl=currentEpisodes.get(position).getLink().replace("{L}",lang);
                                        if(qual==null){
                                            qual = currentEpisodes.get(position).getQual().split(",")[0];
                                        }
                                        videourl.replaceAll("_\\d+\\.","_"+qual+".");
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
                ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.qualBtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(serie_page_activity.this);
                        builder.setTitle("აირჩიეთ ხარისხი")
                                .setItems(currentEpisodes.get(position).getQual().split(","), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        movieTime = videoView.getCurrentPosition();
                                        //videourl=currentEpisodes.get(position).getLink().replace("{L}", lang);

                                        qual = currentEpisodes.get(position).getQual().split(",")[which];
                                        videourl.replaceAll("_\\d+\\.","_"+qual+".");

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

        HistoryModel historyModel=new HistoryModel(serie.getMovieId(),serie.getTitle_en(),serie.getLink(),
                serie.getPoster(),serie.getImdb(),serie.getImdb_id(),serie.getRelease_date(),serie.getDescription(),
                serie.getDuration(),serie.getLang()
        );

        historyModel.setType(2);

        List<HistoryModel> movieList=HistoryModel.find(HistoryModel.class, "movie_id = '"+historyModel.getMovieId()+"'");

        if(movieList.size()==0) {
            historyModel.save();
        }else{
            movieList.get(0).delete();
            historyModel.save();
        }


    }

    public void stopVideoPlaying(){
        videoView.pause();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopVideoPlaying();
    }
    @Override
    protected void onStop(){
        super.onStop();
        stopVideoPlaying();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopVideoPlaying();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(videoView!=null){
            videoView.resume();
        }
        //startVideoPlaying();
    }
}
