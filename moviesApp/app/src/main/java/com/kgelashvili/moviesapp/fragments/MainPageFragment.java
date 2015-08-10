package com.kgelashvili.moviesapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.MoviePageActivity;
import com.kgelashvili.moviesapp.R;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.kgelashvili.moviesapp.serie_page_activity;
import com.kgelashvili.moviesapp.GeoMoviesCollecitonActivity;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by KGelashvili on 7/24/2015.
 */
public class MainPageFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private static View view;

    @InjectView(R.id.geoMoviesLayout2)
    LinearLayout geoMoviesLayout2;

    @InjectView(R.id.geoSeriesLayout2)
    LinearLayout geoSeriesLayout2;

    @InjectView(R.id.premierMoviesLayout2)
    LinearLayout premierMoviesLayout2;

    @InjectView(R.id.newAddedMoviesLayout2)
    LinearLayout newAddedMoviesLayout2;

    @InjectView(R.id.newEpisodesLayout2)
    LinearLayout newEpisodesLayout2;

    @InjectView(R.id.mainfragmentframe)
    FrameLayout mainfragmentframe;

    public static MainPageFragment newInstance(int position,View view) {
        MainPageFragment f = new MainPageFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        MainPageFragment.view=view;
        return f;
    }
    @OnClick(R.id.textView5)
    public void openGeoMovies(){
        Intent i = new Intent(getActivity(), GeoMoviesCollecitonActivity.class);
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mainfragment, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        final getGeoMovies geoMovies=new getGeoMovies();
        final getPremierMovies premierMovies=new getPremierMovies();
        final getLatestEpisodes latestEpisodes=new getLatestEpisodes();
        final getNewAddedMovies newAddedMovies=new getNewAddedMovies();
        new Thread(new Runnable() {
            @Override
            public void run() {
                geoMovies.doInBackground("");
                premierMovies.doInBackground("");
                latestEpisodes.doInBackground("");
                newAddedMovies.doInBackground("");
                new getGeoEpisodes().doInBackground("");


            }
        }).start();

        return rootView;
    }

    class getGeoMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getLatestGeoMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addGeoMovieToColection(values[0].get(i));
            }

        }

    }

    private void addGeoMovieToColection(final Movie movie) {

        LinearLayout linearLayout = geoMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);



        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Movie selectedMovie = movie;

                        Intent i = new Intent(getActivity(), MoviePageActivity.class);
                        i.putExtra("movieId", selectedMovie.getMovieId());
                        i.putExtra("description", selectedMovie.getDescription());
                        i.putExtra("title", selectedMovie.getTitle_en());
                        i.putExtra("date", selectedMovie.getRelease_date());
                        i.putExtra("duration", selectedMovie.getDuration());
                        i.putExtra("rating", selectedMovie.getImdb());
                        i.putExtra("imdb", selectedMovie.getImdb_id());
                        i.putExtra("lang", selectedMovie.getLang());
                        i.putExtra("time", 0);
                        i.putExtra("Movie",selectedMovie);
                        startActivityForResult(i, 1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(MainPageFragment.view);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);
        cardView.setCard(card);

        linearLayout.addView(layout);
    }

    class getPremierMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getPremierMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addPremiereMovieToColection(values[0].get(i));
            }

        }

    }

    private void addPremiereMovieToColection(final Movie movie) {
        LinearLayout linearLayout = premierMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
        IconSupplementalAction t1 = new IconSupplementalAction(getActivity(), R.id.ic3);



        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();




        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Movie selectedMovie = movie;

                        Intent i = new Intent(getActivity(), MoviePageActivity.class);
                        i.putExtra("movieId", selectedMovie.getMovieId());
                        i.putExtra("description", selectedMovie.getDescription());
                        i.putExtra("title", selectedMovie.getTitle_en());
                        i.putExtra("date", selectedMovie.getRelease_date());
                        i.putExtra("duration", selectedMovie.getDuration());
                        i.putExtra("rating", selectedMovie.getImdb());
                        i.putExtra("imdb", selectedMovie.getImdb_id());
                        i.putExtra("lang", selectedMovie.getLang());
                        i.putExtra("time", 0);
                        i.putExtra("Movie",selectedMovie);
                        startActivityForResult(i, 1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(MainPageFragment.view);

            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);

        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    class getNewAddedMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getNewAddedMovies();
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);

            for (int i = 0; i < values[0].size(); i++) {
                addNewAddedMoviesToColection(values[0].get(i));
            }

        }

    }

    private void addNewAddedMoviesToColection(final Movie movie) {
        LinearLayout linearLayout = newAddedMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);




        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Movie selectedMovie = movie;

                        Intent i = new Intent(getActivity(), MoviePageActivity.class);
                        i.putExtra("movieId", selectedMovie.getMovieId());
                        i.putExtra("description", selectedMovie.getDescription());
                        i.putExtra("title", selectedMovie.getTitle_en());
                        i.putExtra("date", selectedMovie.getRelease_date());
                        i.putExtra("duration", selectedMovie.getDuration());
                        i.putExtra("rating", selectedMovie.getImdb());
                        i.putExtra("imdb", selectedMovie.getImdb_id());
                        i.putExtra("lang", selectedMovie.getLang());
                        i.putExtra("time", 0);
                        i.putExtra("Movie",selectedMovie);
                        startActivityForResult(i, 1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(MainPageFragment.view);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);
        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    class getLatestEpisodes extends AsyncTask<String, ArrayList<Serie>, ArrayList<Serie>> {

        @Override
        protected ArrayList<Serie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Serie> series = movieServices.getNewEpisodes();

            publishProgress(series);

            return series;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Serie>... values) {
            super.onProgressUpdate(values);

            for (int i = 0; i < values[0].size(); i++) {
                addLatestEpisodeToColection(values[0].get(i));
            }

        }

    }

    private void addLatestEpisodeToColection(final Serie serie) {



        LinearLayout linearLayout = newEpisodesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);





        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage("Se "+serie.getLastSes()+" Ep "+serie.getLastEp())
                        .useDrawableUrl(serie.getPoster())
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Serie selectedMovie = serie;

                Intent i = new Intent(getActivity(), serie_page_activity.class);
                i.putExtra("movieId", selectedMovie.getMovieId());
                i.putExtra("description", selectedMovie.getDescription());
                i.putExtra("title", selectedMovie.getTitle_en());
                i.putExtra("date", selectedMovie.getRelease_date());
                i.putExtra("duration", selectedMovie.getDuration());
                i.putExtra("rating", selectedMovie.getImdb());
                i.putExtra("imdb", selectedMovie.getImdb_id());
                i.putExtra("lang", selectedMovie.getLang());
                i.putExtra("time", 0);
                i.putExtra("Serie",serie);
                startActivity(i);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);
        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    class getGeoEpisodes extends AsyncTask<String, ArrayList<Serie>, ArrayList<Serie>> {

        @Override
        protected ArrayList<Serie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Serie> series = movieServices.getGeoEpisodes();
            publishProgress(series);

            return series;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Serie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addGeoEpisodeToColection(values[0].get(i));
            }

        }

    }

    private void addGeoEpisodeToColection(final Serie serie) {

        LinearLayout linearLayout = geoSeriesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);





        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage("Se "+serie.getLastSes()+" Ep "+serie.getLastEp())
                        .useDrawableUrl(serie.getPoster())
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Serie selectedMovie = serie;

                Intent i = new Intent(getActivity(), serie_page_activity.class);
                i.putExtra("movieId", selectedMovie.getMovieId());
                i.putExtra("description", selectedMovie.getDescription());
                i.putExtra("title", selectedMovie.getTitle_en());
                i.putExtra("date", selectedMovie.getRelease_date());
                i.putExtra("duration", selectedMovie.getDuration());
                i.putExtra("rating", selectedMovie.getImdb());
                i.putExtra("imdb", selectedMovie.getImdb_id());
                i.putExtra("lang", selectedMovie.getLang());
                i.putExtra("time", 0);
                i.putExtra("Serie",serie);
                startActivity(i);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);
        cardView.setCard(card);

        linearLayout.addView(layout);

    }





}
