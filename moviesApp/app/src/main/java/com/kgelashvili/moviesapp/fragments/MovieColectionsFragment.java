package com.kgelashvili.moviesapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kgelashvili.moviesapp.Classes.MaterialCardArrayAdapter;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.R;
import com.kgelashvili.moviesapp.model.ColectionModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

public class MovieColectionsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
    private static View view;
    ArrayList<Card> cards=new ArrayList<Card>();
    MaterialCardArrayAdapter adapter2;


    @InjectView(R.id.carddemo_list_cursor2)
    CardListView mListView;

    public static MovieColectionsFragment newInstance(int position,View view,dbHelper dbHelper2) {
        MovieColectionsFragment f = new MovieColectionsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        MovieColectionsFragment.view=view;
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_colections, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);


        adapter2=new MaterialCardArrayAdapter(getActivity(),cards);
        mListView.setAdapter(adapter2);

        new Thread(new Runnable() {
            @Override
            public void run() {

                new GetColections().doInBackground("");
            }
        }).start();



        return rootView;
    }
    class GetColections extends AsyncTask<String, ArrayList<ColectionModel>, ArrayList<ColectionModel>> {

        @Override
        protected ArrayList<ColectionModel> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<ColectionModel> colections = movieServices.getColections();
            publishProgress(colections);

            return colections;
        }

        @Override
        protected void onProgressUpdate(ArrayList<ColectionModel>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addColectionToLoadidData(values[0].get(i));
            }
        }

    }

    public void addColectionToLoadidData(final ColectionModel colectionModel) {

        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(getActivity())
                        .setTextOverImage(colectionModel.getName())
                        .useDrawableUrl("http://static.adjaranet.com/collections/thumb/" + colectionModel.getId() + "_big.jpg")
                        .build();
        adapter2.add(card);



    }

}
