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
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.FloatingActionButton;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.model.Actor;
import com.kgelashvili.moviesapp.model.Movie;
import com.nineoldandroids.animation.Animator;

import org.w3c.dom.Text;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviePageActivity extends Activity {

    //Uri video = Uri.parse("http://212.72.157.137/fast2/storage/10246/10246_Georgian_300.mp4");
    private static ProgressDialog progressDialog;

    VideoView videoView;
    String videourl = "";
    int movieTime = 0;
    private WebView webView;
    LinearLayout castLayout;
    TabHost tabHost=null;

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
        setContentView(R.layout.moviepagelayout);
        final Bundle extras = getIntent().getExtras();

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec=tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator(extras.getString("title"));
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("ინფორმაცია ფილმზე");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("movie");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("IMDB");
        tabHost.addTab(tabSpec);


        castLayout=(LinearLayout)findViewById(R.id.actorsLayout);



        final String value = extras.getString("movieId");
        final FloatingActionButton mFab = new FloatingActionButton.Builder(MoviePageActivity.this)
                .withColor(getResources().getColor(R.color.primary))
                .withDrawable(getResources().getDrawable(R.drawable.androidfullscreen))
                .withSize(72)
                .withMargins(0, 0, 16, 16)
                .create();
        mFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (tabHost.getCurrentTab() == 0) {
                    mFab.show();
                }else{
                    mFab.hide();
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
            }
        }).start();
        ((mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoviePageActivity.this);
                builder.setTitle("აირჩიეთ ენა")
                        .setItems(extras.getString("lang").split(","), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                movieTime = videoView.getCurrentPosition();
                                String lang = extras.getString("lang").split(",")[which];
                                videourl = "http://adjaranet.com/download.php?mid=" + value + "&file=" + value + "_" + lang + "_300";
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
        date.setText(extras.getString("date"));
        ((TextView) findViewById(R.id.imdbRating)).setText(extras.getString("rating"));
        ((ImageView) findViewById(R.id.imdbImg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + extras.getString("imdb")));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.descriptionTxt)).setText(extras.getString("description"));
        videourl = "http://adjaranet.com/download.php?mid="
                + value + "&file=" + value + "_" + (extras.getString("lang").split(",")[0]) + "_300";
        setTitle(extras.getString("title"));
        movieTime = extras.getInt("time");


        videoView = (VideoView) findViewById(R.id.myvideoview);
        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
        progressDialog.setCancelable(true);
        PlayVideo();

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

    private void PlayVideo() {
        try {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);


            final Uri video = Uri.parse(videourl);
            //videoView.setMediaController(mediaController);
            Log.d("moviePageLink", videourl);
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
                            Log.d("kaxaError", "error");
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
            System.out.println("Video Play Error :" + e.toString());
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
            publishProgress(actors);
            return actors;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Actor>... values) {
            super.onProgressUpdate(values);
            Log.d("kaxaGeo1", "kaxaGeo1");
            for (int i = 0; i < values[0].size(); i++) {
                addActorToCast(values[0].get(i));
            }

        }
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
        Log.d("requestCodekaxa", "" + requestCode);
        Log.d("resultCodekaxa", "" + resultCode);

    }

    public void onBackPressed() {
        if(tabHost.getCurrentTab()==2){
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
