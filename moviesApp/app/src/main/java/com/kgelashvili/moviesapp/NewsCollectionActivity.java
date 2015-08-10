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
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.kgelashvili.moviesapp.utils.GeoMoviesCollecitonActivity;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NewsCollectionActivity extends AppCompatActivity {


    CardListView mListView;
    CardArrayAdapter adapter3;
    ArrayList<Card> cardsFav=new ArrayList<Card>();
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
        setContentView(R.layout.activity_news_collection);
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
        getMenuInflater().inflate(R.menu.menu_news_collection, menu);
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

        List<Serie> seriesList=Serie.listAll(Serie.class);
        for(int i=0;i<seriesList.size();i++){

            final Serie serie=seriesList.get(i);
            final Card card = new Card(NewsCollectionActivity.this);


            //Create a CardHeader
            CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(this,
                    serie.getTitle_en(),serie.getRelease_date(),serie.getDescription().length()>50?serie.getDescription().substring(0,49):serie.getDescription());

            //Set the header title
            header.setTitle(serie.getTitle_en());
            header.setOtherButtonVisible(true);

            //Add a callback
            header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
                @Override
                public void onButtonItemClick(Card card, View view) {
                    Toast.makeText(NewsCollectionActivity.this, "წაიშალა ვაპირებ ყურებას სიიდან და შეწყდა სიახლეების გამოწერა", Toast.LENGTH_LONG).show();
                    serie.delete();
                    adapter3.remove(card);
                    adapter3.notifyDataSetChanged();
                }
            });

            //Use this code to set your drawable
            header.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);

            card.addCardHeader(header);

            //Add a callback


            //Use this code to set your drawable




            //Create thumbnail
            //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

            CardThumbnail thumbnail=new CardThumbnail(this);

            thumbnail.setUrlResource(serie.getPoster());

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
                            Serie selectedMovie = serie;

                            Intent i = new Intent(NewsCollectionActivity.this, serie_page_activity.class);
                            i.putExtra("movieId", selectedMovie.getMovieId());
                            i.putExtra("description", selectedMovie.getDescription());
                            i.putExtra("title", selectedMovie.getTitle_en());
                            i.putExtra("date", selectedMovie.getRelease_date());
                            i.putExtra("duration", selectedMovie.getDuration());
                            i.putExtra("rating", selectedMovie.getImdb());
                            i.putExtra("imdb", selectedMovie.getImdb_id());
                            i.putExtra("Serie",serie);
                            startActivityForResult(i, 1);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(findViewById(R.id.newsColectionMain));
                }
            });
            adapter3.add(card);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YoYo.with(Techniques.ZoomInLeft).playOn(findViewById(R.id.newsColectionMain));
    }

}
