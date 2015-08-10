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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kgelashvili.moviesapp.model.Actor;
import com.kgelashvili.moviesapp.model.HistoryModel;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.nineoldandroids.animation.Animator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviePageActivity extends Activity {

    //Uri video = Uri.parse("http://212.72.157.137/fast2/storage/10246/10246_Georgian_300.mp4");
    private static ProgressDialog progressDialog;
    String movieId=null;
    String quality="";
    String currentQuality=null;
    String langs=null;
    String lang=null;
    VideoView videoView;
    String videourl = "";
    int movieTime = 0;
    private WebView webView;
    LinearLayout castLayout;
    LinearLayout relatedLayout;
    TabHost tabHost=null;
    Movie movie=null;

    public MoviePageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/bpg_square_mtavruli_2009.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.moviepagelayout);
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
        movie= (Movie) getIntent().getSerializableExtra("Movie");
        langs=extras.getString("lang");

        castLayout=(LinearLayout)findViewById(R.id.actorsLayout);
        relatedLayout=(LinearLayout)findViewById(R.id.relatedMoviesLayout);

        ((TextView)findViewById(R.id.durationTxt)).setText(extras.getString("duration") + "-სთ");

        final String value = extras.getString("movieId");
        movieId=extras.getString("movieId");


        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.fullScreenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MoviePageActivity.this, FullScreenMovie.class);
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
        ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.watchLaterBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Movie> movieList = Movie.find(Movie.class, "movie_id = '" + movie.getMovieId() + "'");

                if (movieList.size() == 0) {
                    movie.save();
                    Toast.makeText(MoviePageActivity.this, "დაემატა ვაპირებ ყრებას სიაში", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MoviePageActivity.this, "უკვე არსებობს ვაპირებ ყრებას სიაში", Toast.LENGTH_LONG).show();
                }
            }
        });
        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.downloadButtonSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = videourl;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                new getMovieActorsAsync().doInBackground(extras.getString("movieId"));
                new getMovieRelated().doInBackground(extras.getString("movieId"));
            }
        }).start();
        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoviePageActivity.this);
                builder.setTitle("აირჩიეთ ენა")
                        .setItems(langs.split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();
                                if (currentQuality == null) {
                                    currentQuality = quality.split(",")[0];
                                }
                                lang = langs.split(",")[which];
                                videourl = "http://adjaranet.com/download.php?mid=" + value + "&file=" + value + "_" +
                                        lang + "_" + currentQuality;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MoviePageActivity.this);
                builder.setTitle("აირჩიეთ ხარისხი")
                        .setItems(quality.split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();
                                //videourl=currentEpisodes.get(position).getLink().replace("{L}", lang);

                                currentQuality = quality.split(",")[which];

                                videourl = "http://adjaranet.com/download.php?mid=" + value + "&file=" + value + "_" +
                                        lang + "_" + currentQuality;
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


        TextView date = (TextView) findViewById(R.id.movieDate);
        date.setText("(" + extras.getString("date") + ")");
        ((TextView) findViewById(R.id.imdbRating)).setText(extras.getString("rating"));
        ((ImageView) findViewById(R.id.imdbImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + extras.getString("imdb")));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.descriptionTxt)).setText(extras.getString("description"));

        setTitle(extras.getString("title"));
        movieTime = extras.getInt("time");


        videoView = (VideoView) findViewById(R.id.myvideoview);
        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
        progressDialog.setCancelable(true);


        YoYo.with(Techniques.SlideInRight).duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        webView = (WebView) findViewById(R.id.webView1);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient());
                        webView.loadUrl("http://www.imdb.com/title/" + extras.getString("imdb"));
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(findViewById(R.id.moviePageRelative));




    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);


            final Uri video = Uri.parse(videourl);
            //videoView.setMediaController(mediaController);

            videoView.setVideoURI(Uri.parse(videourl));
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    progressDialog.dismiss();
                    videoView.start();
                    videoView.seekTo(movieTime);
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            movieTime = mediaPlayer.getCurrentPosition();
                            PlayVideo();

                            return true;
                        }
                    });
                    MediaController mediaController = new MediaController(MoviePageActivity.this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);


                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_page, menu);
        return true;
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

    class getMovieActorsAsync extends AsyncTask<String,ArrayList<Actor>,ArrayList<Actor>>{

        @Override
        protected ArrayList<Actor> doInBackground(String... strings) {
            ArrayList<Actor> actors;
            actors=new MovieServices().getMovieActors(strings[0]);
            quality=new MovieServices().getMovieQuality(strings[0]);
            langs=new MovieServices().getMovieLangs(strings[0]);
            movie.setLang(langs);

            HistoryModel historyModel=new HistoryModel(movie.getMovieId(),movie.getTitle_en(),movie.getLink(),
                    movie.getPoster(),movie.getImdb(),movie.getImdb_id(),movie.getRelease_date(),movie.getDescription(),
                    movie.getDuration(),movie.getLang()
            );

            historyModel.setType(1);

            List<HistoryModel> movieList=HistoryModel.find(HistoryModel.class, "movie_id = '"+historyModel.getMovieId()+"'");

            if(movieList.size()==0) {
                historyModel.save();
            }else{
                movieList.get(0).delete();
                historyModel.save();
            }



            publishProgress(actors);
            return actors;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Actor>... values) {
            super.onProgressUpdate(values);

            videourl = "http://adjaranet.com/download.php?mid="
                    + movieId + "&file=" + movieId + "_" + (langs.split(",")[0]) + "_"+quality.split(",")[0];
            PlayVideo();
            for (int i = 0; i < values[0].size(); i++) {
                addActorToCast(values[0].get(i));
            }

        }


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




        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        //.setTextOverImage(actor.actorName)
                        .useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                Intent i = new Intent(MoviePageActivity.this, MoviePageActivity.class);
                i.putExtra("movieId", movie.getMovieId());
                i.putExtra("description", movie.getDescription());
                i.putExtra("title", movie.getTitle_en());
                i.putExtra("date", movie.getRelease_date());
                i.putExtra("duration", movie.getDuration());
                i.putExtra("rating", movie.getImdb());
                i.putExtra("imdb", movie.getImdb_id());
                i.putExtra("lang", movie.getLang());
                i.putExtra("time", 0);
                i.putExtra("Movie",movie);
                startActivityForResult(i, 1);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView)layout.findViewById(R.id.actorCardName)).setText(movie.getTitle_en());
        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    private void addActorToCast(Actor actor) {

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

            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView)layout.findViewById(R.id.actorCardName)).setText(actor.actorName);
        cardView.setCard(card);

        linearLayout.addView(layout);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        movieTime = resultCode;
        super.onActivityResult(requestCode, resultCode, data);


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
                }).duration(500).playOn(findViewById(R.id.moviePageRelative));
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
            }).duration(500).playOn(findViewById(R.id.moviePageRelative));
        }


    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean result = false;

        /* ... */
            // Return false to proceed loading page, true to interrupt loading

            return result;
        }
    }

}
