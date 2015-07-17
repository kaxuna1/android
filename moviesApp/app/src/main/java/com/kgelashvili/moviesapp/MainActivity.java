package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.kgelashvili.moviesapp.Classes.CustomHeaderMainMovieItem;
import com.kgelashvili.moviesapp.Classes.FloatingActionButton;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.cards.CustomThumbCard;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.kgelashvili.moviesapp.utils.SimpleSectionedListAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

public class MainActivity extends Activity {
    String currentMovieUrl = "";
    int currentMovieTime = 0;
    private int currentLoaded = 0;
    ArrayAdapter<Movie> adapter;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    boolean loadingMore = false;
    String keyWord = "";
    dbHelper dbHelper2=new dbHelper(MainActivity.this);


    private ListView mDrawerList;
    private DrawerLayout mDrawer;
    private CustomActionBarDrawerToggle mDrawerToggle;
    private int mCurrentTitle = com.kgelashvili.moviesapp.R.string.app_name;
    private int mSelectedFragment;
    SimpleSectionedListAdapter mSectionedAdapter;
    protected ActionMode mActionMode;
    private static String TAG = "MainActivity";


    ArrayList<Card> cards=new ArrayList<Card>();
    ArrayList<Card> cardsFav=new ArrayList<Card>();

    CardListView mListView;
    CardListView mListView2;
    CardArrayAdapter adapter2;
    CardArrayAdapter adapter3;


    final getMovies getmovies = new getMovies();
    final getMoviesFav getmoviesFav = new getMoviesFav();


    CardArrayAdapter adapter2Series;
    ArrayList<Card> cardsSeries =new ArrayList<Card>();
    private int currentLoadedSeries = 0;
    boolean loadingMoreSeries = false;
    String keyWordSeries = "";
    CardListView mListViewSeries;




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
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("კინოები");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("favorites");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("★");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("series");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("სერიალები");
        tabHost.addTab(tabSpec);

        FloatingActionButton mFab = new FloatingActionButton.Builder(this)
                .withColor(getResources().getColor(R.color.primary))
                .withDrawable(getResources().getDrawable(R.drawable.action_search))
                .withSize(72)
                .withMargins(0, 0, 16, 16)
                .create();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.searchBox)).requestFocus();
               tabHost.setCurrentTab(0);
                final EditText k=(EditText) findViewById(R.id.searchBox);
                k.post(new Runnable() {
                    public void run() {
                        k.requestFocusFromTouch();
                        InputMethodManager lManager = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        lManager.showSoftInput(k, 0);
                    }
                });
            }
        });
        adapter3=new CardArrayAdapter(MainActivity.this,cardsFav);
        adapter2=new CardArrayAdapter(MainActivity.this,cards);



        //adapter = new MovieListAdapter();


        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingMore = true;
                getmovies.doInBackground("" + currentLoaded);
                getmoviesFav.doInBackground("");

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
                cards.clear();
                adapter2.clear();
                currentLoaded = 0;
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mListView = (CardListView) findViewById(R.id.carddemo_list_cursor2);
        mListView2 = (CardListView) findViewById(R.id.carddemo_list_cursor3);
        if (mListView != null) {
            mListView.setAdapter(adapter2);
        }
        if (mListView2 != null) {
            mListView2.setAdapter(adapter3);
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

        final GetSeries getSeries=new GetSeries();

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingMoreSeries = true;
                getSeries.doInBackground("" + currentLoadedSeries);

            }
        });
        adapter2Series =new CardArrayAdapter(this, cardsSeries);
        EditText searchBoxSeries = (EditText) findViewById(R.id.searchBoxSeries);
        mListViewSeries = (CardListView) findViewById(R.id.seriesList);
        if (mListViewSeries != null) {
            mListViewSeries.setAdapter(adapter2Series);
        }
        searchBoxSeries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyWordSeries = ((EditText) findViewById(R.id.searchBoxSeries)).getText().toString().replace(" ", "%20");
                cardsSeries.clear();
                adapter2Series.clear();
                currentLoadedSeries = 0;
                adapter2Series.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mListViewSeries.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMoreSeries)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadingMoreSeries = true;
                            getSeries.doInBackground("" + currentLoadedSeries);
                        }
                    }).start();
                }
            }
        });


        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentTitle("testNotification");
        notificationBuilder.setContentTitle("test notification text");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification=notificationBuilder.build();
        NotificationManager notificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(8,notification);

    }

    public void addMovieToLoadidData(final Movie movie) {

        //adapter.add(movie);


        Card card = new Card(MainActivity.this);

        //Create a CardHeader
        CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(MainActivity.this,
                movie.getTitle_en(),movie.getRelease_date(),movie.getDescription().length()>50?movie.getDescription().substring(0,49):movie.getDescription());

        //Set the header title
        header.setTitle(movie.getTitle_en());

        card.addCardHeader(header);

        header.setOtherButtonVisible(true);

        //Add a callback
        header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
            @Override
            public void onButtonItemClick(Card card, View view) {
                Toast.makeText(MainActivity.this, "დაემატა ფავორიტებში", Toast.LENGTH_LONG).show();
                dbHelper2.createMovie(movie);
                Card card2 = new Card(MainActivity.this);

                //Create a CardHeader
                CustomHeaderMainMovieItem header2 = new CustomHeaderMainMovieItem(MainActivity.this,
                        movie.getTitle_en(), movie.getRelease_date(), movie.getDescription().length() > 50 ? movie.getDescription().substring(0, 49) : movie.getDescription());

                //Set the header title
                header2.setTitle(movie.getTitle_en());

                card2.addCardHeader(header2);

                header2.setOtherButtonVisible(true);
                header2.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
                    @Override
                    public void onButtonItemClick(Card card, View view) {
                        Toast.makeText(MainActivity.this, "დაემატა ფავორიტებში", Toast.LENGTH_LONG).show();
                        dbHelper2.deleteMovie(movie);
                        adapter3.remove(card);
                        adapter3.notifyDataSetChanged();
                    }
                });

                header2.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);


                //Create thumbnail
                //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

                CardThumbnail thumbnail2 = new CardThumbnail(MainActivity.this);

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
                adapter3.add(card2);
                card.getCardHeader().setOtherButtonVisible(false);

            }
        });

        //Use this code to set your drawable
        header.setOtherButtonDrawable(R.drawable.card_menu_button_other_add);




        //Create thumbnail
        //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

        CardThumbnail thumbnail=new CardThumbnail(MainActivity.this);

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
        adapter2.add(card);



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
            Intent i;
            i = new Intent(MainActivity.this, settingsActivity.class);

            startActivity(i);
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

    class getMoviesFav extends AsyncTask<String,ArrayList<Card>,ArrayList<Card>>{

        @Override
        protected ArrayList<Card> doInBackground(String... params) {
            populateFavorites();
            return null;
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
            Intent i;
            switch (position) {
                case 0:

                     i = new Intent(MainActivity.this, settingsActivity.class);

                    startActivity(i);
                    break;
            }
        }
    }

    private void populateFavorites(){

        List<Movie> moviesFavs=dbHelper2.getAllMovies();
        for(int i=0;i<moviesFavs.size();i++){
            final Movie movie=moviesFavs.get(i);
            Card card = new Card(MainActivity.this);

            //Create a CardHeader
            CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(MainActivity.this,
                    movie.getTitle_en(),movie.getRelease_date(),movie.getDescription().length()>50?movie.getDescription().substring(0,49):movie.getDescription());

            //Set the header title
            header.setTitle(movie.getTitle_en());

            card.addCardHeader(header);

            header.setOtherButtonVisible(true);

            //Add a callback
            header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
                @Override
                public void onButtonItemClick(Card card, View view) {
                    Toast.makeText(MainActivity.this, "დაემატა ფავორიტებში", Toast.LENGTH_LONG).show();
                    dbHelper2.deleteMovie(movie);
                    adapter3.remove(card);
                    adapter3.notifyDataSetChanged();
                }
            });

            //Use this code to set your drawable
            header.setOtherButtonDrawable(R.drawable.card_menu_button_other_dismiss);

            CardExpand expand = new CardExpand(MainActivity.this);

            card.addCardExpand(expand);



            //Create thumbnail
            //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

            CardThumbnail thumbnail=new CardThumbnail(MainActivity.this);

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
            adapter3.add(card);
            adapter3.notifyDataSetChanged();
        }
    }

    class GetSeries extends AsyncTask<String, ArrayList<Serie>, ArrayList<Serie>> {

        @Override
        protected ArrayList<Serie> doInBackground(String... strings) {

            MovieServices movieServices = new MovieServices();
            ArrayList<Serie> series = movieServices.getMainSeries(strings[0], "false", "1900", "2015", keyWordSeries);
            publishProgress(series);
            return series;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Serie>... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < values[0].size(); i++) {
                addSerieToLoadidData(values[0].get(i));
            }
            //adapter.notifyDataSetChanged();
            Log.d("moviesLog", "" + currentLoaded);
            currentLoadedSeries += 15;
            //populateMoviesListViev();
            loadingMoreSeries = false;
        }

    }
    public void addSerieToLoadidData(final Serie serie) {

        //adapter.add(movie);


        Card card = new Card(MainActivity.this);

        //Create a CardHeader
        CustomHeaderMainMovieItem header = new CustomHeaderMainMovieItem(MainActivity.this,
                serie.getTitle_en(),serie.getRelease_date(),serie.getDescription().length()>50?serie.getDescription().substring(0,49):serie.getDescription());

        //Set the header title
        header.setTitle(serie.getTitle_en());

        card.addCardHeader(header);

        //Add a callback


        //Use this code to set your drawable




        //Create thumbnail
        //CustomThumbCard thumb = new CustomThumbCard(MainActivity.this);

        CardThumbnail thumbnail=new CardThumbnail(MainActivity.this);

        thumbnail.setUrlResource(serie.getPoster());

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
                Movie selectedMovie = serie;

                Intent i = new Intent(MainActivity.this, serie_page_activity.class);
                i.putExtra("movieId", selectedMovie.getId());
                i.putExtra("description", selectedMovie.getDescription());
                i.putExtra("title", selectedMovie.getTitle_en());
                i.putExtra("date", selectedMovie.getRelease_date());
                i.putExtra("duration", selectedMovie.getDuration());
                i.putExtra("rating", selectedMovie.getImdb());
                i.putExtra("imdb", selectedMovie.getImdb_id());
                startActivity(i);
            }
        });
        adapter2Series.add(card);



    }
}
