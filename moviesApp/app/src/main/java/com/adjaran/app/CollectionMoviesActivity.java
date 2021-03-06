package com.adjaran.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adjaran.app.Classes.CustomHeaderMainMovieItem;
import com.adjaran.app.Classes.CustomThumbNail;
import com.adjaran.app.Classes.MovieServices;
import com.adjaran.app.model.Movie;
import com.adjaran.app.model.Serie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;


public class CollectionMoviesActivity extends AppCompatActivity {

    String id="";
    ArrayList<Card> cards=new ArrayList<Card>();
    CardArrayAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_movies);
        CardListView mListView=(CardListView)findViewById(R.id.carddemo_list_cursor2);
        final Bundle extras = getIntent().getExtras();
        id=extras.getString("id");
        adapter2=new CardArrayAdapter(this,cards);
        mListView.setAdapter(adapter2);
        new Thread(new Runnable() {
            @Override
            public void run() {

                new getMovies().doInBackground(id);
            }
        }).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection_movies, menu);
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

    class getMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getCollectionMovies(strings[0]);
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addMovieToLoadidData(values[0].get(i));
            }
        }

    }

    public void addMovieToLoadidData(final Movie movie) {

        //adapter.add(movie);




        Card card = new Card(this);


        //Create a CardHeader
        CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(this,
                movie.getTitle_en(),movie.getRelease_date(),movie.getDescription().length()>50?movie.getDescription().substring(0,49):movie.getDescription());

        //Set the header title
        header.setTitle(movie.getTitle_en());

        card.addCardHeader(header);

        //header.setOtherButtonVisible(true);

        //Add a callback


        //Use this code to set your drawable
        //header.setOtherButtonDrawable(R.drawable.card_menu_button_other_add);




        //Create thumbnail
        //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

        CustomThumbNail thumbnail=new CustomThumbNail(this);

        thumbnail.setCustomSource(new CardThumbnail.CustomSource() {
            @Override
            public String getTag() {
                return movie.getPoster();
            }

            @Override
            public Bitmap getBitmap() {
                try {

                    return Picasso.with(CollectionMoviesActivity.this).load(movie.getPoster()).resize(184, 276).get();
                } catch (Exception e) {
                    return null;
                }
            }
        });

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
                if(movie.type==0){
                    Movie selectedMovie = movie;
                    Intent i = new Intent(CollectionMoviesActivity.this, MoviePageActivity.class);
                    i.putExtra("movieId", selectedMovie.getMovieId());
                    i.putExtra("description", selectedMovie.getDescription());
                    i.putExtra("title", selectedMovie.getTitle_en());
                    i.putExtra("date", selectedMovie.getRelease_date());
                    i.putExtra("duration", selectedMovie.getDuration());
                    i.putExtra("rating", selectedMovie.getImdb());
                    i.putExtra("imdb", selectedMovie.getImdb_id());
                    i.putExtra("lang", selectedMovie.getLang());
                    i.putExtra("time", 0);
                    i.putExtra("Movie", selectedMovie);
                    startActivityForResult(i, 1);
                }else{
                    Serie serie=new Serie(movie.getMovieId(),movie.getTitle_en(),movie.getLink(),
                            movie.getPoster(),movie.getImdb(),
                            movie.getImdb_id(),movie.getRelease_date(),movie.getDescription(),
                            movie.getDuration(),movie.getLang());
                    Intent i = new Intent(CollectionMoviesActivity.this, serie_page_activity.class);
                    i.putExtra("movieId", serie.getMovieId());
                    i.putExtra("description", serie.getDescription());
                    i.putExtra("title", serie.getTitle_en());
                    i.putExtra("date", serie.getRelease_date());
                    i.putExtra("duration", serie.getDuration());
                    i.putExtra("rating", serie.getImdb());
                    i.putExtra("imdb", serie.getImdb_id());
                    i.putExtra("Serie",serie);
                    startActivityForResult(i, 1);
                }

            }
        });

        adapter2.add(card);




    }
}
