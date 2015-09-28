package com.adjara.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.adjara.app.Classes.MovieServices;
import com.adjara.app.Classes.ScrollViewExt;
import com.adjara.app.Classes.ScrollViewListener;
import com.adjara.app.model.Actor;
import com.adjara.app.model.HistoryModel;
import com.adjara.app.model.Movie;
import com.adjara.app.model.MovieSerieLastMomentModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviePageActivity extends AppCompatActivity {

    //Uri video = Uri.parse("http://212.72.157.137/fast2/storage/10246/10246_Georgian_300.mp4");
    String movieId = null;
    String quality = "";
    String currentQuality = null;
    String langs = null;
    String lang = null;
    VideoView videoView;
    String videourl = "";

    String videoPath = "";
    int movieTime;
    private WebView webView;
    LinearLayout castLayout;
    LinearLayout relatedLayout;
    TabHost tabHost = null;
    ImageButton playButton = null;
    TextView director = null;
    String directorText = "";
    TextView gener = null;
    private static ProgressDialog progressDialog = null;
    String generText = "";
    Movie movie = null;
    private static Timer myTimer;
    MovieSerieLastMomentModel movieSerieLastMomentModel;
    boolean fromFullScreen=false;

    public MoviePageActivity() {
    }


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
        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ჩატვირთვა", true);
        progressDialog.setCancelable(true);
        setContentView(R.layout.moviepagelayout);
        movie = (Movie) getIntent().getSerializableExtra("Movie");
        List<MovieSerieLastMomentModel> movieSerieLastMomentModels = MovieSerieLastMomentModel.find(MovieSerieLastMomentModel.class, "movie_id = '" + movie.getMovieId() + "'");
        if (movieSerieLastMomentModels.size() > 0) {
            movieSerieLastMomentModel = movieSerieLastMomentModels.get(0);
        } else {
            movieSerieLastMomentModel = new MovieSerieLastMomentModel(movie.getMovieId());
            movieSerieLastMomentModel.time = 0;
            movieSerieLastMomentModel.save();
        }
        movieTime=movieSerieLastMomentModel.getTime();

        playButton = (ImageButton) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MoviePageActivity.this, "მიმდინარეობს ფილმის ჩატვირთვა", Toast.LENGTH_LONG).show();
            }
        });
        startVideoPlaying();
    }


    private void PlayVideo() {
        try {


            final Uri video = Uri.parse(videourl);
            //videoView.setMediaController(mediaController);

            videoView.setVideoURI(Uri.parse(videourl));
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    playButton.setVisibility(View.GONE);
                    videoView.seekTo(movieSerieLastMomentModel.getTime());
                    videoView.start();
                    if (progressDialog != null)
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    final Bundle extras = getIntent().getExtras();
                    ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.fullScreenButton)).setOnClickListener(new View.OnClickListener() {
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

                            if (myTimer != null) {
                                myTimer.cancel();
                                myTimer.purge();

                            }


                            startActivity(i);
                        }
                    });
                    myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            movieSerieLastMomentModel.time = videoView.getCurrentPosition();
                            movieSerieLastMomentModel.save();
                        }

                    }, 0, 1000);
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            movieTime = mediaPlayer.getCurrentPosition();

                            PlayVideo();

                            return true;
                        }
                    });
                    ScrollViewExt scrollViewExt = (ScrollViewExt) findViewById(R.id.scrollView2);

                    final MediaController mediaController = new MediaController(MoviePageActivity.this, true);

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

        } catch (Exception e) {
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

    class getMovieActorsAsync extends AsyncTask<String, ArrayList<Actor>, ArrayList<Actor>> {

        @Override
        protected ArrayList<Actor> doInBackground(String... strings) {
            ArrayList<Actor> actors;
            actors = new MovieServices().getMovieActors(strings[0]);
            quality = new MovieServices().getMovieQuality(strings[0]);
            langs = new MovieServices().getMovieLangs(strings[0]);

            videoPath = new MovieServices().getMoviePath(strings[0]);
            directorText = new MovieServices().getDirector(strings[0]);
            generText = new MovieServices().getJanrebi(strings[0]);

            movie.setLang(langs);

            HistoryModel historyModel = new HistoryModel(movie.getMovieId(), movie.getTitle_en(), movie.getLink(),
                    movie.getPoster(), movie.getImdb(), movie.getImdb_id(), movie.getRelease_date(), movie.getDescription(),
                    movie.getDuration(), movie.getLang()
            );

            historyModel.setType(1);

            List<HistoryModel> movieList = HistoryModel.find(HistoryModel.class, "movie_id = '" + historyModel.getMovieId() + "'");

            if (movieList.size() == 0) {
                historyModel.save();
            } else {
                movieList.get(0).delete();
                historyModel.save();
            }


            publishProgress(actors);
            return actors;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Actor>... values) {
            super.onProgressUpdate(values);
            try {
                lang = langs.split(",")[0];
                videourl = videoPath + movieId + "_" + lang + "_" + quality.split(",")[0] + ".mp4";
                //PlayVideo();
                director.setText("რეჟისორი: " + directorText);
                gener.setText(generText);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playButton.setVisibility(View.GONE);
                        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ჩატვირთვა", true);
                        progressDialog.setCancelable(true);
                        PlayVideo();
                    }
                });
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                for (int i = 0; i < values[0].size(); i++) {
                    addActorToCast(values[0].get(i));
                }

            } catch (Exception e) {
                Toast.makeText(MoviePageActivity.this, "გაწყდა კავშირი ინტერნეტთან", Toast.LENGTH_LONG).show();
                finish();
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
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(MoviePageActivity.this).load(movie.getPoster())
                                        .resize(184, 276).into((ImageView) viewImage);
                            }
                        })
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
                i.putExtra("Movie", movie);
                startActivityForResult(i, 1);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView) layout.findViewById(R.id.actorCardName)).setText(movie.getTitle_en());
        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    private void addActorToCast(final Actor actor) {

        LinearLayout linearLayout = castLayout;
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.actorontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        //.setTextOverImage(actor.actorName)
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(MoviePageActivity.this)
                                        .load("http://static.adjaranet.com/cast/" + actor.actorId + ".jpg")
                                        .resize(184,276)
                                        .error(getResources().getDrawable(R.drawable.both))
                                        .into((ImageView) viewImage);
                            }
                        })
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(MoviePageActivity.this, ActorMoviesActivity.class);
                i.putExtra("id", actor.actorId);

                startActivityForResult(i, 1);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.actorCard);
        ((TextView) layout.findViewById(R.id.actorCardName)).setText(actor.actorName);
        cardView.setCard(card);

        linearLayout.addView(layout);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        movieTime = resultCode;
        fromFullScreen=true;
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void onBackPressed() {
        if (tabHost.getCurrentTab() == 1) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {

                if (myTimer != null) {
                    myTimer.cancel();
                    myTimer.purge();
                }
                finish();

            }
        } else {

            if (myTimer != null) {
                myTimer.cancel();
                myTimer.purge();
            }
            finish();

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

    public void startVideoPlaying() {
        final Bundle extras = getIntent().getExtras();

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(extras.getString("title"));
        tabHost.addTab(tabSpec);
        director = (TextView) findViewById(R.id.directorText);
        gener = (TextView) findViewById(R.id.geners);
        tabSpec = tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("IMDb");
        tabHost.addTab(tabSpec);

        langs = extras.getString("lang");

        castLayout = (LinearLayout) findViewById(R.id.actorsLayout);
        relatedLayout = (LinearLayout) findViewById(R.id.relatedMoviesLayout);

        ((TextView) findViewById(R.id.durationTxt)).setText(extras.getString("duration") + "-სთ");

        final String value = extras.getString("movieId");
        movieId = extras.getString("movieId");


       /* ((mehdi.sakout.fancybuttons.FancyButton)findViewById(R.id.fullScreenButton)).setOnClickListener(new View.OnClickListener() {
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

                if(myTimer!=null){
                    myTimer.cancel();
                    myTimer.purge();

                }


                startActivityForResult(i, 0);
            }
        });*/
        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.watchLaterBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Movie> movieList = Movie.find(Movie.class, "movie_id = '" + movie.getMovieId() + "'");

                if (movieList.size() == 0) {
                    movie.save();
                    Toast.makeText(MoviePageActivity.this, "დაემატა სიაში", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MoviePageActivity.this, "უკვე დამატებულია სიაში", Toast.LENGTH_LONG).show();
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
                                if (myTimer != null) {
                                    myTimer.cancel();
                                    myTimer.purge();
                                }

                                movieSerieLastMomentModel.setTime(movieSerieLastMomentModel.getTime());
                                movieSerieLastMomentModel.save();
                                if (currentQuality == null) {
                                    currentQuality = quality.split(",")[0];
                                }
                                lang = langs.split(",")[which];
                                videourl = videoPath + value + "_" +
                                        lang + "_" + currentQuality + ".mp4";
                                progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ჩატვირთვა", true);
                                progressDialog.setCancelable(true);
                                PlayVideo();

                            }
                        });
                builder.create();
                builder.show();
            }
        });
        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.qualBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoviePageActivity.this);
                builder.setTitle("აირჩიეთ ხარისხი")
                        .setItems(quality.split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();
                                //videourl=currentEpisodes.get(position).getLink().replace("{L}", lang);
                                if (myTimer != null) {
                                    myTimer.cancel();
                                    myTimer.purge();
                                }
                                movieSerieLastMomentModel.setTime(videoView.getCurrentPosition());
                                movieSerieLastMomentModel.save();

                                currentQuality = quality.split(",")[which];

                                videourl = videoPath + value + "_" +
                                        lang + "_" + currentQuality + ".mp4";
                                progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ჩატვირთვა", true);
                                progressDialog.setCancelable(true);
                                PlayVideo();


                            }
                        });
                builder.create();
                builder.show();
            }
        });


        TextView date = (TextView) findViewById(R.id.movieDate);
        date.setText(extras.getString("date"));
        ((TextView) findViewById(R.id.imdbRating)).setText(extras.getString("rating"));
        ((ImageView) findViewById(R.id.imdbImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabHost.setCurrentTab(1);
            }
        });
        ((TextView) findViewById(R.id.descriptionTxt)).setText(extras.getString("description"));

        setTitle(extras.getString("title"));
        movieTime = extras.getInt("time");


        videoView = (VideoView) findViewById(R.id.myvideoview);


        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.imdb.com/title/" + extras.getString("imdb"));

    }

    public void stopVideoPlaying() {
        videoView.pause();
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideoPlaying();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVideoPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideoPlaying();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //playButton.setVisibility(View.VISIBLE);
        if (fromFullScreen) {
            PlayVideo();
            fromFullScreen=false;
        }
        //startVideoPlaying();
    }

}
