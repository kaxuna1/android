package com.kgelashvili.moviesapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kgelashvili.moviesapp.Classes.CustomHeaderMainMovieItem;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.MoviePageActivity;
import com.kgelashvili.moviesapp.R;
import com.kgelashvili.moviesapp.model.Movie;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by KGelashvili on 7/27/2015.
 */
public class MoviesPageFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private int position;
    String keyWord = "";
    boolean loadingMore = true;
    private static View view;
    private static dbHelper dbHelper2;
    private int currentLoaded = 0;
    ArrayList<Card> cards=new ArrayList<Card>();
    CardArrayAdapter adapter2;
    final getMovies getmovies = new getMovies();

    @InjectView(R.id.searchBox)
    EditText searchBox;

    @InjectView(R.id.carddemo_list_cursor2)
    CardListView mListView;


    public static MoviesPageFragment newInstance(int position,View view,dbHelper dbHelper2) {
        MoviesPageFragment f = new MoviesPageFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        MoviesPageFragment.view=view;
        MoviesPageFragment.dbHelper2=dbHelper2;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviepagefragment, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        adapter2=new CardArrayAdapter(getActivity(),cards);
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
                    if (!loadingMore){
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
                    if(!loadingMore){
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

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyWord = searchBox.getText().toString().replace(" ", "%20");
                cards.clear();
                adapter2.clear();
                currentLoaded = 0;
                //loadMore();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }

    class getMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            Log.d("kinoLoad", "gamodzaxda");
            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getMainMovies(strings[0], "false", "1900", "2015", keyWord);
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addMovieToLoadidData(values[0].get(i));
            }
            //adapter.notifyDataSetChanged();
            Log.d("moviesLogKaxa","gamodzaxda");
            Log.d("moviesLog", "" + currentLoaded);
            currentLoaded += 15;
            //populateMoviesListViev();
            loadingMore = false;
        }

    }
    
    public void addMovieToLoadidData(final Movie movie) {

        //adapter.add(movie);


        Card card = new Card(getActivity());

        //Create a CardHeader
        CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(getActivity(),
                movie.getTitle_en(),movie.getRelease_date(),movie.getDescription().length()>50?movie.getDescription().substring(0,49):movie.getDescription());

        //Set the header title
        header.setTitle(movie.getTitle_en());

        card.addCardHeader(header);

        header.setOtherButtonVisible(true);

        //Add a callback
        header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
            @Override
            public void onButtonItemClick(Card card, View view) {
                Toast.makeText(getActivity(), "დაემატა ფავორიტებში", Toast.LENGTH_LONG).show();
                dbHelper2.createMovie(movie);
                Card card2 = new Card(getActivity());

                //Create a CardHeader
                CustomHeaderMainMovieItem header2 = new CustomHeaderMainMovieItem(getActivity(),
                        movie.getTitle_en(), movie.getRelease_date(), movie.getDescription().length() > 50 ? movie.getDescription().substring(0, 49) : movie.getDescription());

                //Set the header title
                header2.setTitle(movie.getTitle_en());

                card2.addCardHeader(header2);

                header2.setOtherButtonVisible(true);
                header2.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
                    @Override
                    public void onButtonItemClick(Card card, View view) {
                        Toast.makeText(getActivity(), "დაემატა ფავორიტებში", Toast.LENGTH_LONG).show();
                        dbHelper2.deleteMovie(movie);
                        //TODO seamless adding/deleting to favorites tab
                        //adapter3.remove(card);
                        //adapter3.notifyDataSetChanged();
                    }
                });

                header2.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);


                //Create thumbnail
                //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

                CardThumbnail thumbnail2 = new CardThumbnail(getActivity());

                thumbnail2.setUrlResource(movie.getPoster());

                //Set URL resource
                //thumb.setUrlResource(movie.getPoster());


                //Error Resource ID
                thumbnail2.setErrorResource(R.drawable.ic_error_loadingorangesmall);

                //Add thumbnail to a card
                card2.addCardThumbnail(thumbnail2);


                //Set card in the cardView


                //Set card in the cardVie

                card2.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        Movie selectedMovie = movie;

                        Intent i = new Intent(getActivity(), MoviePageActivity.class);
                        i.putExtra("movieId", selectedMovie.getId());
                        i.putExtra("description", selectedMovie.getDescription());
                        i.putExtra("title", selectedMovie.getTitle_en());
                        i.putExtra("date", selectedMovie.getRelease_date());
                        i.putExtra("duration", selectedMovie.getDuration());
                        i.putExtra("rating", selectedMovie.getImdb());
                        i.putExtra("imdb", selectedMovie.getImdb_id());
                        i.putExtra("lang", selectedMovie.getLang());
                        i.putExtra("time", 0);
                        startActivity(i);
                    }
                });
                //// TODO: 7/27/2015
                //adapter3.add(card2);
                card.getCardHeader().setOtherButtonVisible(false);

            }
        });

        //Use this code to set your drawable
        header.setOtherButtonDrawable(R.drawable.card_menu_button_other_add);




        //Create thumbnail
        //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

        CardThumbnail thumbnail=new CardThumbnail(getActivity());

        thumbnail.setUrlResource(movie.getPoster());

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

                Intent i = new Intent(getActivity(), MoviePageActivity.class);
                i.putExtra("movieId", selectedMovie.getId());
                i.putExtra("description", selectedMovie.getDescription());
                i.putExtra("title", selectedMovie.getTitle_en());
                i.putExtra("date", selectedMovie.getRelease_date());
                i.putExtra("duration", selectedMovie.getDuration());
                i.putExtra("rating", selectedMovie.getImdb());
                i.putExtra("imdb", selectedMovie.getImdb_id());
                i.putExtra("lang", selectedMovie.getLang());
                i.putExtra("time", 0);
                startActivity(i);
            }
        });
        adapter2.add(card);




    }
}
