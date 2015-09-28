package com.adjara.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adjara.app.Classes.MovieServices;
import com.adjara.app.Classes.dbHelper;
import com.adjara.app.CollectionMoviesActivity;
import com.adjara.app.R;
import com.adjara.app.model.ColectionModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardGridView;

public class MovieColectionsFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
    private static View view;
    ArrayList<Card> cards;
    CardGridArrayAdapter mCardArrayAdapter;


    @InjectView(R.id.carddemo_grid_base1)
    CardGridView mListView;

    public MovieColectionsFragment() {

    }

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
        cards = new ArrayList<Card>();
        mCardArrayAdapter = new CardGridArrayAdapter(getActivity(), cards);

        mListView.setAdapter(mCardArrayAdapter);

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
        GplayGridCard card = new GplayGridCard(getActivity());
        card.headerTitle = colectionModel.getName();
        card.secondaryTitle = colectionModel.getCnt()+" ფილმი";
        card.thumbUrl="http://static.adjaranet.com/collections/thumb/" + colectionModel.getId() + "_big.jpg";
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent i = new Intent(getActivity(), CollectionMoviesActivity.class);
                i.putExtra("id",colectionModel.getId());
                startActivityForResult(i, 1);
            }
        });
        card.init();
        mCardArrayAdapter.add(card);



    }

    public class GplayGridCard extends Card {

        protected TextView mTitle;
        protected TextView mSecondaryTitle;
        protected RatingBar mRatingBar;
        protected int resourceIdThumbnail = -1;
        protected String thumbUrl;
        protected int count;

        protected String headerTitle;
        protected String secondaryTitle;

        public GplayGridCard(Context context) {
            super(context, R.layout.carddemo_gplay_inner_content);
        }

        public GplayGridCard(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        private void init() {
            //CardHeader header = new CardHeader(getContext());
            //header.setButtonOverflowVisible(true);
            //header.setTitle(headerTitle.length()>);

            //addCardHeader(header);

            GplayGridThumb thumbnail = new GplayGridThumb(getContext());

            thumbnail.setUrlResource(thumbUrl);
            addCardThumbnail(thumbnail);

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {


            TextView subtitle = (TextView) view.findViewById(R.id.carddemo_gplay_main_inner_subtitle);
            subtitle.setText(headerTitle);

        }

        class GplayGridThumb extends CardThumbnail {

            public GplayGridThumb(Context context) {
                super(context);
            }
            @Override
            public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                if (viewImage != null) {

                    if (parent!=null && parent.getResources()!=null){
                        DisplayMetrics metrics=parent.getResources().getDisplayMetrics();

                        int base = 125;

                        if (metrics!=null){
                            viewImage.getLayoutParams().width = (int)(base*metrics.density*1.4);
                            viewImage.getLayoutParams().height = (int)(base*metrics.density);
                        }else{
                            viewImage.getLayoutParams().width = 250;
                            viewImage.getLayoutParams().height = 350;
                        }
                    }
                }
            }
        }

    }

}
