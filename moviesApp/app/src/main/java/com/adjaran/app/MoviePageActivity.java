package com.adjaran.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import com.adjaran.app.Classes.MovieServices;
import com.adjaran.app.Classes.ScrollViewExt;
import com.adjaran.app.Classes.ScrollViewListener;
import com.adjaran.app.model.Actor;
import com.adjaran.app.model.HistoryModel;
import com.adjaran.app.model.Movie;
import com.adjaran.app.model.MovieSerieLastMomentModel;
import com.adjaran.app.util.SystemUiHider;
import com.adjaran.app.utils.MediaItem;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import mehdi.sakout.fancybuttons.FancyButton;
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
    String poster = "";

    String videoPath = "";
    int movieTime;
    private WebView webView;
    LinearLayout castLayout;
    LinearLayout relatedLayout;
    TabHost tabHost = null;
    TextView director = null;
    String directorText = "";
    TextView gener = null;
    private static ProgressDialog progressDialog = null;
    String generText = "";
    Movie movie = null;
    private static Timer myTimer;
    MovieSerieLastMomentModel movieSerieLastMomentModel;
    boolean fromFullScreen = false;
    File file1;
    public static final String LOG_TAG = "Android Downloader";
    int oldHeight;
    private SystemUiHider mSystemUiHider;
    private MediaItem mSelectedMedia;
    private static List<MediaItem> mediaList;

    //initialize our progress dialog/bar
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    //initialize root directory
    File rootDir = Environment.getExternalStorageDirectory();

    //defining file name and url
    public String fileName = "codeofaninja.jpg";
    public String fileURL = "https://lh4.googleusercontent.com/-HiJOyupc-tQ/TgnDx1_HDzI/AAAAAAAAAWo/DEeOtnRimak/s800/DSC04158.JPG";


    JCVideoPlayerStandard jCVideoPlayer;
    boolean contineuDownload = true;
    private CastContext mCastContext;

    JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
    SensorManager sensorManager;


    public MoviePageActivity() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            ViewGroup.LayoutParams params = ((RelativeLayout) findViewById(R.id.relativeLayout4)).getLayoutParams();
            oldHeight = params.height;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ((RelativeLayout) findViewById(R.id.relativeLayout4)).setLayoutParams(params);

            final View contentView = findViewById(R.id.myvideoview);
            mSystemUiHider = SystemUiHider.getInstance(this, contentView, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mSystemUiHider.setup();
            mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                @Override
                public void onVisibilityChange(boolean visible) {
                    if (visible == true) {
                        Handler mHideHandler = new Handler();
                        mHideHandler.removeCallbacks(mHideRunnable);
                        mHideHandler.postDelayed(mHideRunnable, 1000);
                    }

                }
            });
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                @Override
                public void onVisibilityChange(boolean visible) {

                }
            });
            ViewGroup.LayoutParams params = ((RelativeLayout) findViewById(R.id.relativeLayout4)).getLayoutParams();
            params.height = oldHeight;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ((RelativeLayout) findViewById(R.id.relativeLayout4)).setLayoutParams(params);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        }
    }

    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

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


        mediaList = new ArrayList<>();
        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ჩატვირთვა", true);
        progressDialog.setCancelable(true);
        setContentView(R.layout.moviepagelayout);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();
        jCVideoPlayer = (JCVideoPlayerStandard) findViewById(R.id.myvideoview);

        movie = (Movie) getIntent().getSerializableExtra("Movie");
        List<MovieSerieLastMomentModel> movieSerieLastMomentModels = MovieSerieLastMomentModel.find(MovieSerieLastMomentModel.class, "movie_id = '" + movie.getMovieId() + "'");
        if (movieSerieLastMomentModels.size() > 0) {
            movieSerieLastMomentModel = movieSerieLastMomentModels.get(0);
        } else {
            movieSerieLastMomentModel = new MovieSerieLastMomentModel(movie.getMovieId());
            movieSerieLastMomentModel.time = 0;
            movieSerieLastMomentModel.save();
        }
        movieTime = movieSerieLastMomentModel.getTime();
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);

        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        if (mCastSession != null)
            if (mCastSession.isConnected()) {
                Toast.makeText(MoviePageActivity.this, "თქვენ დაკავშირებული ხართ google cast მოწყობილობასთან!", Toast.LENGTH_LONG).show();
            }


        startVideoPlaying();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
               /* if (null != mSelectedMedia) {

                    if (mPlaybackState == PlaybackState.PLAYING) {
                        mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);
                        finish();
                        return;
                    } else {
                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                updatePlayButton(mPlaybackState);*/
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
               /* updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
                updatePlayButton(mPlaybackState);*/
                invalidateOptionsMenu();
            }
        };
    }


    private void PlayVideo() {
        try {
            //videoView.setMediaController(mediaController);


            Picasso.with(this).load(movie.getPoster()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    jCVideoPlayer.setUp(videourl
                            , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, movie.getTitle_en());
                    jCVideoPlayer.thumbImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

            /*videoView.setVideoURI(Uri.parse(videourl));
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    if (mCastSession != null) {
                        if (mCastSession.isConnected()) {

                            final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
                            remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
                                @Override
                                public void onStatusUpdated() {
                                    Intent intent = new Intent(MoviePageActivity.this, ExpandedControlsActivity.class);
                                    startActivity(intent);
                                    remoteMediaClient.removeListener(this);
                                }

                                @Override
                                public void onMetadataUpdated() {
                                }

                                @Override
                                public void onQueueStatusUpdated() {
                                }

                                @Override
                                public void onPreloadStatusUpdated() {
                                }

                                @Override
                                public void onSendingRemoteMediaRequest() {
                                }

                                @Override
                                public void onAdBreakStatusUpdated() {

                                }
                            });
                            remoteMediaClient.load(buildMediaInfo(mp.getDuration()), true, 0);

                        }
                    } else {
                        playButton.setVisibility(View.GONE);
                        videoView.seekTo(movieSerieLastMomentModel.getTime());
                        videoView.start();
                        if (progressDialog != null)
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        final Bundle extras = getIntent().getExtras();
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


                }
            });
*/

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


    @Override
    public void onStart() {
        super.onStart();

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
                PlayVideo();
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
                                        .resize(184, 276)
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
        fromFullScreen = true;
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void onBackPressed() {
        if (jCVideoPlayer.backPress()) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
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
        poster = extras.getString("poster");

        castLayout = (LinearLayout) findViewById(R.id.actorsLayout);
        relatedLayout = (LinearLayout) findViewById(R.id.relatedMoviesLayout);

        ((TextView) findViewById(R.id.durationTxt)).setText(extras.getString("duration") + "-სთ");

        final String value = extras.getString("movieId");
        movieId = extras.getString("movieId");
        ((FancyButton) findViewById(R.id.watchLaterBtn)).setOnClickListener(new View.OnClickListener() {
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                new getMovieActorsAsync().doInBackground(extras.getString("movieId"));
                new getMovieRelated().doInBackground(extras.getString("movieId"));
            }
        }).start();
        ((FancyButton) findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MoviePageActivity.this);
                builder.setTitle("აირჩიეთ ენა")
                        .setItems(langs.split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = 0;
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
        ((FancyButton) findViewById(R.id.qualBtn)).setOnClickListener(new View.OnClickListener() {
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


        //videoView = (VideoView) findViewById(R.id.myvideoview);


        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.imdb.com/title/" + extras.getString("imdb"));

    }

    public void stopVideoPlaying() {
        //videoView.pause();

        if (jCVideoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
            jCVideoPlayer.startButton.performClick ();
        } else
        if (jCVideoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PAUSE) {
            //TODO
        }
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideoPlaying();
        sensorManager.unregisterListener(sensorEventListener);

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
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (fromFullScreen) {
            PlayVideo();
            fromFullScreen = false;
        }
        //startVideoPlaying();
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }


        @Override
        protected String doInBackground(String... aurl) {

            try {
                //connecting to url
                Log.d("downloaderLink", aurl[0]);
                URL u = new URL(aurl[0]);
                URLConnection c = u.openConnection();
                //c.setUseCaches(true);
                int lenghtOfFile = c.getContentLength();
                //c.setDoOutput(true);
                //c.setReadTimeout(1000);

                c.connect();


                //lenghtOfFile is used for calculating download progress

                Log.d("filesize", "" + lenghtOfFile);

                //this is where the file will be seen after the download
                String fileName2 = rootDir + "/Movies/" + fileName;

                file1 = new File(rootDir + "/Movies/", fileName);
                FileOutputStream f = new FileOutputStream(file1);
                //file input is from the url
                InputStream in = c.getInputStream();

                //here’s the download code
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                contineuDownload = true;
                while ((len1 = in.read(buffer)) > 0) {
                    if (contineuDownload) {
                        total += len1; //total = total + len1
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        f.write(buffer, 0, len1);
                    } else {
                        file1.delete();
                        break;
                    }

                }
                f.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d(LOG_TAG, progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));

        }

        @Override
        protected void onPostExecute(String unused) {
            //dismiss the dialog after the file was downloaded
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

    //function to verify if directory exists
    public void checkAndCreateDirectory(String dirName) {
        File new_dir = new File(rootDir + dirName);
        if (!new_dir.exists()) {
            new_dir.mkdirs();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("მიმდინარეობს გადმოწერა…");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "გაუქმება", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contineuDownload = false;
                        dialog.cancel();
                    }
                });
                mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "გახსნა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file1.getPath()));
                        intent.setDataAndType(Uri.parse(file1.getPath()), "video/mp4");
                        MoviePageActivity.this.startActivity(intent);

                    }
                });
                mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (contineuDownload) {
                            mProgressDialog.show();
                        }

                    }
                });
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        contineuDownload = false;
                    }
                });
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }

    }


    private MediaInfo buildMediaInfo(long dur) {

    /*    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri uri = Uri.parse(videourl);
        retriever.setDataSource(this.getApplicationContext(), uri);
        String dur = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
*/

        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        //movieMetadata.addImage(new WebImage(Uri.parse(poster)));
        movieMetadata.putString(MediaMetadata.KEY_TITLE, movie.getTitle_en());
        movieMetadata.addImage(new WebImage(Uri.parse(movie.getPoster())));
        return new MediaInfo.Builder(videourl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setStreamDuration(dur)
                .build();
    }

}
