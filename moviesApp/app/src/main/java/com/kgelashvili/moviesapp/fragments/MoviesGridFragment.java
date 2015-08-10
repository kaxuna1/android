package com.kgelashvili.moviesapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.kgelashvili.moviesapp.Classes.DemoAdapter;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.MoviePageActivity;
import com.kgelashvili.moviesapp.R;
import com.kgelashvili.moviesapp.model.DemoItem;
import com.kgelashvili.moviesapp.model.Movie;
import com.koushikdutta.ion.Ion;
import com.nineoldandroids.animation.Animator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by vakhtanggelashvili on 8/9/15.
 */
public class MoviesGridFragment extends Fragment{
    private static final String ARG_POSITION = "position";
    private int position;
    private static View view;
    private DemoAdapter adapter;
    private int currentOffset;
    boolean loadingMore = true;
    final List<AsymmetricItem> items = new ArrayList<>();
    private int currentLoaded = 0;
    ArrayList<Movie> movies=new ArrayList<Movie>();

    @InjectView(R.id.listView)
    com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView listView;

    public static MoviesGridFragment newInstance(int position,View view,dbHelper dbHelper2) {
        MoviesGridFragment f = new MoviesGridFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        MoviesGridFragment.view=view;
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviesgridfragment, container, false);
        ButterKnife.inject(this, rootView);
        ViewCompat.setElevation(rootView, 50);

        //listView.setRequestedColumnWidth(Utils.dpToPx(getActivity(), 60));
        listView.setBackgroundColor(getResources().getColor(R.color.grey));
        listView.setAllowReordering(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingMore = true;
                new getMovies().doInBackground("" + currentLoaded);
            }
        }).start();
        // initialize your items array
        adapter = new DefaultListAdapter(getActivity(), new ArrayList<DemoItem>());
        listView.setRequestedColumnCount(4);
        listView.determineColumns();
        listView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), 1));

        listView.setAdapter(getNewAdapter());
        listView.setDebugging(true);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    if (!loadingMore) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                loadingMore = true;
                                new getMovies().doInBackground("" + currentLoaded);


                            }
                        }).start();
                    }

                }
            }
        });


        return rootView;
    }
    private List<DemoItem> getMoreItems(int qty) {
        List<DemoItem> items = new ArrayList<>();

        for (int i = 0; i < qty; i++) {
            int colSpan = Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            // int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = colSpan;
            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
            items.add(item);
        }

        currentOffset += qty;

        return items;
    }
    private AsymmetricGridViewAdapter<?> getNewAdapter() {
        return new AsymmetricGridViewAdapter<>(getActivity(), listView, adapter);
    }
    class getMovies extends AsyncTask<String, ArrayList<Movie>, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            MovieServices movieServices = new MovieServices();
            ArrayList<Movie> movies = movieServices.getMainMovies(strings[0], "false", "1900", "2015", "", "");
            publishProgress(movies);

            return movies;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values) {
            super.onProgressUpdate(values);
            addMovieToLoadidData(values[0]);

            //adapter.notifyDataSetChanged();

            currentLoaded += 30;
            //populateMoviesListViev();
            loadingMore = false;
        }

    }

    private void addMovieToLoadidData(ArrayList<Movie> value) {
        movies.addAll(value);
        List<DemoItem> items = new ArrayList<>();
        for(int i=0;i<value.size();i++){

            int colSpan = Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            // int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = colSpan;
            DemoItem item = new DemoItem(colSpan, rowSpan, currentOffset + i);
            items.add(item);
        }
        currentOffset += value.size();

        adapter.appendItems(items);

    }

    class DefaultListAdapter extends ArrayAdapter<DemoItem> implements DemoAdapter {

        private final LayoutInflater layoutInflater;

        public DefaultListAdapter(Context context, List<DemoItem> items) {
            super(context, 0, items);
            layoutInflater = LayoutInflater.from(context);
        }

        public DefaultListAdapter(Context context) {
            super(context, 0);
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView,@NotNull ViewGroup parent) {
            View v;

            final Movie currentMovie=movies.get(position);
            DemoItem item = getItem(position);
            boolean isRegular = getItemViewType(position) == 0;

            if (convertView == null) {
                v = layoutInflater.inflate(
                        isRegular ? R.layout.adapter_item : R.layout.adapter_item_odd, parent, false);
            } else {
                v = convertView;
            }

            ImageView imageView;
            if (isRegular) {
                imageView = (ImageView) v.findViewById(R.id.imageGrid);
            } else {
                imageView = (ImageView) v.findViewById(R.id.imageGrid);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.ZoomOutLeft).duration(500).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Movie selectedMovie = currentMovie;

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

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(MoviesGridFragment.view);
                }
            });

            //view.setBackgroundColor(getResources().getColor(R.color.md_green_A100));
            Ion.with(getContext()).load(currentMovie.getPoster()).intoImageView(imageView);


            return v;
        }

        @Override public int getViewTypeCount() {
            return 2;
        }

        @Override public int getItemViewType(int position) {
            return position % 2 == 0 ? 1 : 0;
        }

        public void appendItems(List<DemoItem> newItems) {
            addAll(newItems);
            notifyDataSetChanged();
        }

        public void setItems(List<DemoItem> moreItems) {
            clear();
            appendItems(moreItems);
        }
    }

}
