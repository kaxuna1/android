package com.kgelashvili.moviesapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.astuetz.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.CustomHeaderMainMovieItem;
import com.kgelashvili.moviesapp.Classes.FloatingActionButton;
import com.kgelashvili.moviesapp.Classes.MovieServices;
import com.kgelashvili.moviesapp.Classes.ScrollViewExt;
import com.kgelashvili.moviesapp.Classes.ScrollViewListener;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.broadcastreceivers.CheckEpisodesBroadcastReceiver;
import com.kgelashvili.moviesapp.cards.CustomThumbCard;
import com.kgelashvili.moviesapp.fragments.FavoritesPageFragment;
import com.kgelashvili.moviesapp.fragments.MainPageFragment;
import com.kgelashvili.moviesapp.fragments.MoviesGridFragment;
import com.kgelashvili.moviesapp.fragments.MoviesPageFragment;
import com.kgelashvili.moviesapp.fragments.SeriesPageFragment;
import com.kgelashvili.moviesapp.model.Movie;
import com.kgelashvili.moviesapp.model.Serie;
import com.kgelashvili.moviesapp.utils.SimpleSectionedListAdapter;
import com.nineoldandroids.animation.Animator;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity{
    /* @InjectView(R.id.toolbar)
    Toolbar toolbar;*/
    @InjectView(R.id.tabs)
    com.astuetz.PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    dbHelper dbHelper2=new dbHelper(MainActivity.this);
    private ListView mDrawerList;
    private DrawerLayout mDrawer;
    private CustomActionBarDrawerToggle mDrawerToggle;
    int mCurrentTitle=R.string.app_name;

    SimpleSectionedListAdapter mSectionedAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/bpg_square_mtavruli_2009.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        /*mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);*/
        //setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        //tabs.setTextColor(R.color.md_blue_grey_50);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);
        changeColor(getResources().getColor(R.color.primary));
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Parcelable k = adapter.saveState();
                adapter.restoreState(k,null);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs.setOnTabReselectedListener(new com.astuetz.PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        //tabs.setTextColor(R.color.md_white_1000);
        AlarmManager am=(AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(MainActivity.this, CheckEpisodesBroadcastReceiver.class);
        //intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent2, 0);


        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                intent2,
                PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp)
        {
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 30), pi);
        }
        MainActivity.this.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.gray_background));
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        _initMenu();


    }
    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        tabs.setTextColor(getResources().getColor(R.color.md_white_1000));
        tabs.setUnderlineColor(getResources().getColor(R.color.md_white_1000));
        tabs.setIndicatorColor(getResources().getColor(R.color.md_white_1000));
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        /*if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }*/

        oldBackground = ld;
        currentColor = newColor;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"მთავარი", "ფილმები","სერიალები", "ვაპირებ ყურებას","ფილმების კედელი"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:return MainPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar));
                case 1:return MoviesPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar),dbHelper2);
                case 2:return SeriesPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar));
                case 3:return FavoritesPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar),dbHelper2);
                case 4:return MoviesGridFragment.newInstance(position, findViewById(R.id.fragmentMainLinar), dbHelper2);
                default:return MainPageFragment.newInstance(position,findViewById(R.id.fragmentMainLinar));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YoYo.with(Techniques.ZoomInLeft).playOn(findViewById(R.id.fragmentMainLinar));
    }
    public static final String[] options = {
            "პარამეტრები"
    };
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
            //getActionBar().setTitle("კინოები");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //getActionBar().setTitle("მენიუ");
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }
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

}
