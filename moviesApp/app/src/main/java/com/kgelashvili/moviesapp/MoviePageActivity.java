package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.kgelashvili.moviesapp.Classes.FloatingActionButton;

public class MoviePageActivity extends Activity {

    //Uri video = Uri.parse("http://212.72.157.137/fast2/storage/10246/10246_Georgian_300.mp4");
    private static ProgressDialog progressDialog;

    VideoView videoView;
    String videourl="";
    int movieTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Bundle extras = getIntent().getExtras();
        final String value = extras.getString("movieId");
        videourl = "http://adjaranet.com/download.php?mid="+value+"&file="+value+"_"+(extras.getString("lang").split(",")[0])+"_300";
        setTitle(extras.getString("title"));
        movieTime=extras.getInt("time");
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
                        Intent i=new Intent(MoviePageActivity.this,FullScreenMovie.class);
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


        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        setContentView(R.layout.moviepagelayout);
        ((Button)findViewById(R.id.downloadButtonSerie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = videourl;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        ((Button)findViewById(R.id.langBtnSerie)).setOnClickListener(new View.OnClickListener() {
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

        videoView = (VideoView) findViewById(R.id.myvideoview);
        progressDialog = ProgressDialog.show(MoviePageActivity.this, "", "მიმდინარეობს ვიდეოს ჩატვირთა", true);
        progressDialog.setCancelable(true);
        PlayVideo();



    }
    private void PlayVideo()
    {
        try
        {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(MoviePageActivity.this);
            mediaController.setAnchorView(videoView);


            final Uri video = Uri.parse(videourl);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {
                    progressDialog.dismiss();
                    videoView.start();
                    videoView.seekTo(movieTime);
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            movieTime=mediaPlayer.getCurrentPosition();
                            Log.d("kaxaError","error");
                            PlayVideo();
                            return true;
                        }
                    });
                }
            });

        }
        catch(Exception e)
        {
            progressDialog.dismiss();
            System.out.println("Video Play Error :"+e.toString());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        movieTime=resultCode;
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCodekaxa", "" + requestCode);
        Log.d("resultCodekaxa",""+resultCode);

    }


}
