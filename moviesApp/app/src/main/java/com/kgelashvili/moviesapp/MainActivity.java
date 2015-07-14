package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.cards.CustomThumbCard;
import com.kgelashvili.moviesapp.cards.GplayCard;
import com.kgelashvili.moviesapp.cards.SuggestedCard;
import com.kgelashvili.moviesapp.db.CardCursorContract;
import com.kgelashvili.moviesapp.fragment.BaseFragment;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.utils.SimpleSectionedListAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class MainActivity extends Activity {
    String currentMovieUrl = "";
    int currentMovieTime = 0;
    private int currentLoaded = 0;
    ArrayAdapter<Movie> adapter;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    boolean loadingMore = false;
    String keyWord = "";


    private ListView mDrawerList;
    private DrawerLayout mDrawer;
    private CustomActionBarDrawerToggle mDrawerToggle;
    private int mCurrentTitle = com.kgelashvili.moviesapp.R.string.app_name;
    private int mSelectedFragment;
    private BaseFragment mBaseFragment;
    SimpleSectionedListAdapter mSectionedAdapter;
    protected ActionMode mActionMode;
    private static String TAG = "MainActivity";




    CardListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.gray_background));

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout2);

        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        _initMenu();

        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("კინოები");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("favorites");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("ფავორიტები");
        tabHost.addTab(tabSpec);


        final getMovies getmovies = new getMovies();
        adapter = new MovieListAdapter();


        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingMore = true;
                getmovies.doInBackground("" + currentLoaded);

            }
        })
                .start();
        EditText searchBox = (EditText) findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyWord = ((EditText) findViewById(R.id.searchBox)).getText().toString().replace(" ", "%20");
                movies.clear();
                adapter.clear();
                currentLoaded = 0;
                adapter.notifyDataSetChanged();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mListView = (CardListView) findViewById(R.id.carddemo_list_cursor2);
        if (mListView != null) {
            mListView.setAdapter(adapter);
        }




         /*ListView moviesListView = (ListView) findViewById(R.id.moviesListView);*/

        /*moviesListView.setAdapter(adapter);*/
       /* moviesListView.setTextFilterEnabled(true);
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Movie selectedMovie = movies.get(position);

                Intent i = new Intent(MainActivity.this, MoviePageActivity.class);
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
        moviesListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadingMore = true;
                            getmovies.doInBackground("" + currentLoaded);
                        }
                    }).start();
                }
            }
        });*/
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadingMore = true;
                            getmovies.doInBackground("" + currentLoaded);
                        }
                    }).start();
                }
            }
        });


    }

    public void addMovieToLoadidData(Movie movie) {

        adapter.add(movie);
    }

    private class MovieListAdapter extends ArrayAdapter<Movie> {
        public MovieListAdapter() {
            super(MainActivity.this, R.layout.itemview, movies);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.itemmaterial, viewGroup, false);
            final Movie currentMovie = movies.get(position);

            Card card = new Card(MainActivity.this);

            //Create a CardHeader
            CardHeader header = new CardHeader(MainActivity.this);

            //Set the header title
            header.setTitle(getString(R.string.demo_header_basetitle));

            card.addCardHeader(header);

            //Create thumbnail
            CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

            //Set URL resource
            thumb.setUrlResource(currentMovie.getPoster());

            //Error Resource ID
            thumb.setErrorResource(R.drawable.ic_error_loadingorangesmall);

            //Add thumbnail to a card
            card.addCardThumbnail(thumb);



            //Set card in the cardView


            //Set card in the cardVie

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Movie selectedMovie = currentMovie;

                    Intent i = new Intent(MainActivity.this, MoviePageActivity.class);
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
            CardView cardView = (CardView) view.findViewById(R.id.movieItemId);
            cardView.setCard(card);



            return view;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Log.d("moviesLog", "" + currentLoaded);
            currentLoaded += 15;
            //populateMoviesListViev();
            loadingMore = false;
        }

    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

        public CustomActionBarDrawerToggle(Activity mActivity, DrawerLayout mDrawerLayout) {
            super(
                    mActivity,
                    mDrawerLayout,
                    com.kgelashvili.moviesapp.R.drawable.ic_navigation_drawer,
                    com.kgelashvili.moviesapp.R.string.app_name,
                    mCurrentTitle);
        }

        @Override
        public void onDrawerClosed(View view) {
            getActionBar().setTitle("კინოები");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            getActionBar().setTitle("მენიუ");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    public static final String[] options = {
            "პარამეტრები"
    };

    private void _initMenu() {

        mDrawerList = (ListView) findViewById(R.id.drawer2);

        if (mDrawerList != null) {
            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                    R.layout.demo_activity_menuitem, options);

            List<SimpleSectionedListAdapter.Section> sections =
                    new ArrayList<SimpleSectionedListAdapter.Section>();


            SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections.size()];
            mSectionedAdapter = new SimpleSectionedListAdapter(this, R.layout.demo_activity_menusection, mAdapter);
            mSectionedAdapter.setSections(sections.toArray(dummy));

            mDrawerList.setAdapter(mSectionedAdapter);
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        }

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // Highlight the selected item, update the title, and close the drawer
            // update selected item and title, then close the drawer
            position = mSectionedAdapter.sectionedPositionToPosition(position);
            mDrawer.closeDrawer(mDrawerList);

            switch (position) {
                case 0:

                    Intent i = new Intent(MainActivity.this, settingsActivity.class);

                    startActivity(i);
                    break;

            }
        }
    }



}
