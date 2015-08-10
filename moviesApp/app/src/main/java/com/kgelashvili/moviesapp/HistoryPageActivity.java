package com.kgelashvili.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.CustomHeaderMainMovieItem;
import com.kgelashvili.moviesapp.Classes.CustomThumbNail;
import com.kgelashvili.moviesapp.model.HistoryModel;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.nineoldandroids.animation.Animator;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class HistoryPageActivity extends AppCompatActivity {

    CardListView mListView;
    CardArrayAdapter adapter3;
    ArrayList<Card> cardsFav;
    getMoviesFav getMoviesFav=new getMoviesFav();

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

        setContentView(R.layout.activity_history_page);
        cardsFav=new ArrayList<Card>();
        mListView=(CardListView)findViewById(R.id.carddemo_list_cursor2);
        adapter3=new CardArrayAdapter(this, cardsFav);
        mListView.setAdapter(adapter3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter3.clear();
                getMoviesFav.doInBackground("");
            }
        }).run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_page, menu);
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
    class getMoviesFav extends AsyncTask<String,ArrayList<Card>,ArrayList<Card>> {

        @Override
        protected ArrayList<Card> doInBackground(String... params) {
            populateFavorites();
            return null;
        }
    }

    private void populateFavorites(){

        List<HistoryModel> historyModels=Select.from(HistoryModel.class).orderBy("Id DESC").limit("30").list();
        for(int i=0;i<historyModels.size();i++){

            final HistoryModel historyModel=historyModels.get(i);
            final Card card = new Card(HistoryPageActivity.this);


            //Create a CardHeader
            CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(this,
                    historyModel.getTitle_en(),historyModel.getRelease_date(),historyModel.getDescription().length()>50
                    ?historyModel.getDescription().substring(0,49):historyModel.getDescription());

            //Set the header title
            header.setTitle(historyModel.getTitle_en());

            //Add a callback


            //Use this code to set your drawable
            header.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);

            card.addCardHeader(header);

            //Add a callback


            //Use this code to set your drawable




            //Create thumbnail
            //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

            CustomThumbNail thumbnail=new CustomThumbNail(this);

            thumbnail.setUrlResource(historyModel.getPoster());

            //Set URL resource
            //thumb.setUrlResource(movie.getPoster());


            //Error Resource ID
            thumbnail.setErrorResource(R.drawable.ic_error_loadingorangesmall);

            //Add thumbnail to a card
            card.addCardThumbnail(thumbnail);



            //Set card in the cardView


            //Set card in the cardVie

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {

                    YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(historyModel.getType()==2){
                                Serie serie=new Serie(historyModel.getMovieId(),historyModel.getTitle_en(),historyModel.getLink(),
                                        historyModel.getPoster(),historyModel.getImdb(),
                                        historyModel.getImdb_id(),historyModel.getRelease_date(),historyModel.getDescription(),
                                        historyModel.getDuration(),historyModel.getLang());
                                Intent i = new Intent(HistoryPageActivity.this, serie_page_activity.class);
                                i.putExtra("movieId", serie.getMovieId());
                                i.putExtra("description", serie.getDescription());
                                i.putExtra("title", serie.getTitle_en());
                                i.putExtra("date", serie.getRelease_date());
                                i.putExtra("duration", serie.getDuration());
                                i.putExtra("rating", serie.getImdb());
                                i.putExtra("imdb", serie.getImdb_id());
                                i.putExtra("Serie",serie);
                                startActivityForResult(i, 1);
                            }else{
                                Movie movie=new Movie(historyModel.getMovieId(),historyModel.getTitle_en(),historyModel.getLink(),
                                        historyModel.getPoster(),historyModel.getImdb(),
                                        historyModel.getImdb_id(),historyModel.getRelease_date(),historyModel.getDescription(),
                                        historyModel.getDuration(),historyModel.getLang());
                                Intent i = new Intent(HistoryPageActivity.this, MoviePageActivity.class);
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

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(findViewById(R.id.historyMain));
                }
            });
            adapter3.add(card);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cardsFav=new ArrayList<Card>();
        mListView=(CardListView)findViewById(R.id.carddemo_list_cursor2);
        adapter3=new CardArrayAdapter(this, cardsFav);
        mListView.setAdapter(adapter3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter3.clear();
                getMoviesFav.doInBackground("");
            }
        }).run();
        YoYo.with(Techniques.ZoomInLeft).playOn(findViewById(R.id.historyMain));

    }
}
