package com.kgelashvili.moviesapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.CustomHeaderMainMovieItem;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.MoviePageActivity;
import com.kgelashvili.moviesapp.R;
import com.kgelashvili.moviesapp.model.Movie;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by KGelashvili on 7/27/2015.
 */
public class FavoritesPageFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
    private static View view;
    private static dbHelper dbHelper2;
    CardArrayAdapter adapter3;
    ArrayList<Card> cardsFav=new ArrayList<Card>();
    getMoviesFav getMoviesFav=new getMoviesFav();
    @InjectView(R.id.favoritesList)
    CardListView mListView;
    public static FavoritesPageFragment newInstance(int position,View view,dbHelper dbHelper2) {
        FavoritesPageFragment f = new FavoritesPageFragment();
        Bundle b = new Bundle();
        FavoritesPageFragment.view=view;
        FavoritesPageFragment.dbHelper2=dbHelper2;
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        FavoritesPageFragment.view=view;
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favoritesfragment, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        adapter3=new CardArrayAdapter(getActivity(), cardsFav);
        mListView.setAdapter(adapter3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter3.clear();
                getMoviesFav.doInBackground("");
            }
        }).run();
        return rootView;
    }


    class getMoviesFav extends AsyncTask<String,ArrayList<Card>,ArrayList<Card>> {

        @Override
        protected ArrayList<Card> doInBackground(String... params) {
            populateFavorites();
            return null;
        }
    }
    private void populateFavorites(){

        List<Movie> moviesFavs=dbHelper2.getAllMovies();
        for(int i=0;i<moviesFavs.size();i++){
            final Movie movie=moviesFavs.get(i);
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
                    Toast.makeText(getActivity(), "წაიშალა ფავორიტებიდან", Toast.LENGTH_LONG).show();
                    dbHelper2.deleteMovie(movie);
                    adapter3.remove(card);
                    adapter3.notifyDataSetChanged();
                }
            });

            //Use this code to set your drawable
            header.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);

            CardExpand expand = new CardExpand(getActivity());

            card.addCardExpand(expand);



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
                    YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
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
                            startActivityForResult(i, 1);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(FavoritesPageFragment.view);

                }
            });
            adapter3.add(card);
            adapter3.notifyDataSetChanged();
        }
    }

}
