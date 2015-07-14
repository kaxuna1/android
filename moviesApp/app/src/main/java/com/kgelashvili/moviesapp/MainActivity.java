package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.model.Movie;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {
    String currentMovieUrl="";
    int currentMovieTime=0;
    private int currentLoaded=0;
    ArrayAdapter<Movie> adapter;
    ArrayList<Movie> movies=new ArrayList<Movie>();
    boolean loadingMore=false;
    String keyWord="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("კინოები");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("favorites");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("ფავორიტები");
        tabHost.addTab(tabSpec);

        final getMovies getmovies = new getMovies();
        adapter=new MovieListAdapter();
        ListView moviesListView=(ListView)findViewById(R.id.moviesListView);

        moviesListView.setAdapter(adapter);

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                loadingMore=true;
                getmovies.doInBackground(""+currentLoaded);

            }
        })
                .start();
        EditText searchBox=(EditText)findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyWord=((EditText)findViewById(R.id.searchBox)).getText().toString().replace(" ","%20");
                movies.clear();
                adapter.clear();
                currentLoaded=0;
                adapter.notifyDataSetChanged();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        moviesListView.setTextFilterEnabled(true);
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Movie selectedMovie=movies.get(position);

                Intent i=new Intent(MainActivity.this,MoviePageActivity.class);
                i.putExtra("movieId",selectedMovie.getId());
                i.putExtra("description",selectedMovie.getDescription());
                i.putExtra("title",selectedMovie.getTitle_en());
                i.putExtra("date",selectedMovie.getRelease_date());
                i.putExtra("duration",selectedMovie.getDuration());
                i.putExtra("rating",selectedMovie.getImdb());
                i.putExtra("imdb",selectedMovie.getImdb_id());
                i.putExtra("lang",selectedMovie.getLang());
                i.putExtra("time",0);
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
        });


    }

    public void addMovieToLoadidData(Movie movie){

        adapter.add(movie);
    }

    private class MovieListAdapter extends ArrayAdapter<Movie>{
        public MovieListAdapter(){
            super(MainActivity.this, R.layout.itemview, movies);
        }
        @Override
        public View getView(int position,View view,ViewGroup viewGroup){
            if(view==null)
                view=getLayoutInflater().inflate(R.layout.itemview,viewGroup,false);
            final Movie currentMovie=movies.get(position);
            TextView description=(TextView)view.findViewById(R.id.movieDescription);

            TextView name=(TextView)view.findViewById(R.id.movieName);
            TextView languages=(TextView)view.findViewById(R.id.languages);
            description.setText(currentMovie.getDescription().length()>101?currentMovie.getDescription()
                    .substring(0,100):currentMovie.getDescription());

            name.setText(currentMovie.getTitle_en());
            languages.setText(currentMovie.getLang());
            new DownloadImageTask((ImageView) view.findViewById(R.id.imageView))
                    .execute(currentMovie.getPoster());

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
            ArrayList<Movie> movies = movieServices.getMainMovies(strings[0], "false", "1900", "2015",keyWord);
            publishProgress(movies);

            return movies;
        }
        @Override
        protected void onProgressUpdate(ArrayList<Movie>... values){
            super.onProgressUpdate(values);
            for (int i=0;i<values[0].size();i++){
                addMovieToLoadidData(values[0].get(i));
            }
            //adapter.notifyDataSetChanged();
            Log.d("moviesLog",""+currentLoaded);
            currentLoaded+=15;
            //populateMoviesListViev();
            loadingMore=false;
        }

    }


}
