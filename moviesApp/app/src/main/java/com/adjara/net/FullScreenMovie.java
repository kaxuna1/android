package com.adjara.net;

import com.adjara.net.model.MovieSerieLastMomentModel;
import com.adjara.net.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullScreenMovie extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    VideoView videoView;
    String videourl="";
    int movieTime=0;
    String movieId="";
    private static Timer myTimer;
    MovieSerieLastMomentModel movieSerieLastMomentModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_screen_movie);


        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullScreenMovieView);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        final Bundle extras = getIntent().getExtras();
        String value = extras.getString("movieId");
        videoView=((VideoView)findViewById(R.id.fullScreenMovieView));
        videourl=extras.getString("link");
        movieTime=extras.getInt("time");
        movieId=extras.getString("movieId");
        PlayVideo();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }
    private void PlayVideo()
    {
        try
        {
            //getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(FullScreenMovie.this);
            mediaController.setAnchorView(videoView);


            Uri video = Uri.parse(videourl);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            //videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {


                    List<MovieSerieLastMomentModel> movieSerieLastMomentModels=MovieSerieLastMomentModel.find(MovieSerieLastMomentModel.class, "movie_id = '"+movieId+"'");
                    if(movieSerieLastMomentModels.size()>0){
                        movieSerieLastMomentModel=movieSerieLastMomentModels.get(0);
                    }else{
                        movieSerieLastMomentModel=new MovieSerieLastMomentModel(movieId);
                        movieSerieLastMomentModel.time=0;
                        movieSerieLastMomentModel.save();
                    }
                    videoView.seekTo(movieSerieLastMomentModel.time);
                    videoView.start();
                    videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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

                }
            });

        }
        catch(Exception e)
        {


        }

    }

    public void onBackPressed(){

        if(myTimer!=null){
            myTimer.cancel();
            myTimer.purge();
        }
        setResult(videoView.getCurrentPosition());

        finish();
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}