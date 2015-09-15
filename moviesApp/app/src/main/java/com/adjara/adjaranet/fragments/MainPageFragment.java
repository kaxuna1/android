package com.adjara.adjaranet.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adjara.adjaranet.Classes.MovieServices;
import com.adjara.adjaranet.GeoMoviesCollecitonActivity;
import com.adjara.adjaranet.MoviePageActivity;
import com.adjara.adjaranet.R;
import com.adjara.adjaranet.model.Movie;
import com.adjara.adjaranet.model.Serie;
import com.adjara.adjaranet.serie_page_activity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

    @InjectView(R.id.topMoviesLayout2)
    LinearLayout topMoviesLayout2;

    @InjectView(R.id.mainfragmentframe)
    FrameLayout mainfragmentframe;

    public static MainPageFragment newInstance(int position, View view) {
        MainPageFragment f = new MainPageFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        MainPageFragment.view = view;
        return f;
    }

    @OnClick(R.id.textView5)

    public void openGeoMovies() {
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
        final getGeoMovies geoMovies = new getGeoMovies();
        final getPremierMovies premierMovies = new getPremierMovies();
        final getLatestEpisodes latestEpisodes = new getLatestEpisodes();
        final getNewAddedMovies newAddedMovies = new getNewAddedMovies();
        new Thread(new Runnable() {
            @Override
            public void run() {
                geoMovies.doInBackground("");



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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new getGeoEpisodes().doInBackground("");

                }
            }).start();

        }

    }

    private void addGeoMovieToColection(final Movie movie) {

        LinearLayout linearLayout = geoMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(movie.getPoster()).resize(184,276).into((ImageView) viewImage);
                            }
                        })
                        //.setTextOverImage(movie.getTitle_en())
                        //.useDrawableUrl(movie.getPoster())
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();
        card.setDrawableIdCardThumbnail(R.drawable.logo);

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

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
                i.putExtra("Movie", selectedMovie);
                startActivityForResult(i, 1);

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
            new Thread(new Runnable() {
                @Override
                public void run() {

                    new getLatestEpisodes().doInBackground("");


                }
            }).start();

        }

    }

    private void addPremiereMovieToColection(final Movie movie) {
        LinearLayout linearLayout = premierMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(movie.getPoster()).resize(184, 276).into((ImageView) viewImage);
                            }
                        })
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

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
                i.putExtra("Movie", selectedMovie);
                startActivityForResult(i, 1);


            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);

        cardView.setCard(card);

        linearLayout.addView(layout);

    }

    class getTopMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getTopMovies();
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addTopMovieToColection(values[0].get(i));
            }

        }

    }

    private void addTopMovieToColection(final Movie movie) {
        LinearLayout linearLayout = topMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(movie.getPoster()).resize(184, 276).into((ImageView) viewImage);
                            }
                        })
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
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
                i.putExtra("Movie", selectedMovie);
                startActivityForResult(i, 1);


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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new getTopMovies().doInBackground("");

                }
            }).start();

        }

    }

    private void addNewAddedMoviesToColection(final Movie movie) {
        LinearLayout linearLayout = newAddedMoviesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage(movie.getTitle_en())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(movie.getPoster()).resize(184, 276).into((ImageView) viewImage);
                            }
                        })
                                //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large_icon, actions)
                        .build();


        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

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
                i.putExtra("Movie", selectedMovie);
                startActivityForResult(i, 1);

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
            new Thread(new Runnable() {
                @Override
                public void run() {

                    new getNewAddedMovies().doInBackground("");

                }
            }).start();

        }

    }

    private void addLatestEpisodeToColection(final Serie serie) {


        LinearLayout linearLayout = newEpisodesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage("Se "+serie.getLastSes()+" Ep "+serie.getLastEp())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(serie.getPoster()).resize(184, 276).into((ImageView) viewImage);
                            }
                        })
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
                i.putExtra("Serie", serie);
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new getPremierMovies().doInBackground("");


                }
            }).start();

        }

    }

    private void addGeoEpisodeToColection(final Serie serie) {

        LinearLayout linearLayout = geoSeriesLayout2;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.movieontop, null, false);


        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        //.setTextOverImage("Se "+serie.getLastSes()+" Ep "+serie.getLastEp())
                        .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                            @Override
                            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                Picasso.with(getActivity()).load(serie.getPoster()).resize(184,276).into((ImageView) viewImage);
                            }
                        })
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
                i.putExtra("Serie", serie);
                startActivity(i);
            }
        });
        CardViewNative cardView = (CardViewNative) layout.findViewById(R.id.movieCard);
        cardView.setCard(card);

        linearLayout.addView(layout);

    }


}
