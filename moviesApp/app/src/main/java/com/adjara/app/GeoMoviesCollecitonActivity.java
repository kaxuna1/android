package com.adjara.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.adjara.app.Classes.CustomHeaderMainMovieItem;
import com.adjara.app.Classes.CustomThumbNail;
import com.adjara.app.Classes.JanrebiData;
import com.adjara.app.Classes.MovieServices;
import com.adjara.app.model.Janri;
import com.adjara.app.model.Movie;
import com.adjara.app.model.MovieAndSerie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GeoMoviesCollecitonActivity extends AppCompatActivity {

    boolean loadingMore = true;
    private int currentLoaded = 0;
    ArrayList<Card> cards = new ArrayList<Card>();
    CardArrayAdapter adapter2;
    final getMovies getmovies = new getMovies();
    CardListView mListView;
    ArrayList<Janri> currentJanrebi;


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
        setContentView(R.layout.activity_geo_movies_colleciton);
        currentJanrebi = new ArrayList<Janri>();
        final ArrayList<Janri> janrebi = new JanrebiData().getJanrebi();
        LinearLayout janrebiLayout = (LinearLayout) findViewById(R.id.janrebi);
        for (int i = 0; i < janrebi.size(); i++) {

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(janrebi.get(i).getName());
            final int finalI = i;
            checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //Toast.makeText(getActivity(), "ჟანრი "+janrebi.get(finalI).getName()+" "+isChecked, Toast.LENGTH_SHORT).show();
                            if (isChecked) {
                                currentJanrebi.add(janrebi.get(finalI));

                            } else {
                                currentJanrebi.remove(janrebi.get(finalI));
                            }
                            Log.d("janrebi", getJanrebi());
                            cards.clear();
                            adapter2.clear();
                            currentLoaded = 0;
                        }
                    }
            );
            janrebiLayout.addView(checkBox);

        }
        mListView = (CardListView) findViewById(R.id.carddemo_list_cursor2);
        adapter2 = new CardArrayAdapter(this, cards);
        mListView.setAdapter(adapter2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingMore = true;
                getmovies.doInBackground("" + currentLoaded);
            }
        }).start();
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    if (!loadingMore) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                loadingMore = true;
                                getmovies.doInBackground("" + currentLoaded);


                            }
                        }).start();
                    }
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    if (!loadingMore) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                loadingMore = true;
                                getmovies.doInBackground("" + currentLoaded);
                            }
                        }).start();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geo_movies_colleciton, menu);
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

    class getMovies extends AsyncTask<String, ArrayList<MovieAndSerie>, ArrayList<MovieAndSerie>> {

        @Override
        protected ArrayList<MovieAndSerie> doInBackground(String... strings) {
            Log.d("kinoLoad", "gamodzaxda");
            MovieServices movieServices = new MovieServices();
            ArrayList<MovieAndSerie> movies = movieServices.getMainGeo(strings[0], getJanrebi());
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<MovieAndSerie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addMovieToLoadidData(values[0].get(i));
            }
            //adapter.notifyDataSetChanged();
            Log.d("moviesLogKaxa", "gamodzaxda");
            Log.d("moviesLog", "" + currentLoaded);
            currentLoaded += 30;
            //populateMoviesListViev();
            loadingMore = false;
        }

    }

    public void addMovieToLoadidData(final MovieAndSerie movie) {

        //adapter.add(movie);


        Card card = new Card(GeoMoviesCollecitonActivity.this);


        //Create a CardHeader
        CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(this,
                movie.getTitle_en(), movie.getRelease_date()
                , movie.getDescription().length() > 50 ? movie.getDescription().substring(0, 49) : movie.getDescription());

        //Set the header title
        header.setTitle(movie.getTitle_en());

        card.addCardHeader(header);

        //header.setOtherButtonVisible(true);

        //Add a callback


        //Use this code to set your drawable
        //header.setOtherButtonDrawable(R.drawable.card_menu_button_other_add);


        //Create thumbnail
        //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

        CustomThumbNail thumbnail = new CustomThumbNail(this);

        thumbnail.setCustomSource(new CardThumbnail.CustomSource() {
            @Override
            public String getTag() {
                return movie.getPoster();
            }

            @Override
            public Bitmap getBitmap() {
                try {

                    return Picasso.with(GeoMoviesCollecitonActivity.this).load(movie.getPoster()).resize(184, 276).get();
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

                Movie selectedMovie = movie;

                Intent i = new Intent(GeoMoviesCollecitonActivity.this, MoviePageActivity.class);
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

            }
        });

        adapter2.add(card);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    String getJanrebi() {
        String value = "";
        for (int i = 0; i < currentJanrebi.size(); i++) {
            value += "searchTags%5B%5D=" + currentJanrebi.get(i).getValue() + "&";
        }


        return value;
    }

}
