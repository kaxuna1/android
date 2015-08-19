package com.kgelashvili.moviesapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kgelashvili.moviesapp.Classes.dbHelper;
import com.kgelashvili.moviesapp.broadcastreceivers.CheckEpisodesBroadcastReceiver;
import com.kgelashvili.moviesapp.fragments.FavoritesPageFragment;
import com.kgelashvili.moviesapp.fragments.MainPageFragment;
import com.kgelashvili.moviesapp.fragments.MovieColectionsFragment;
import com.kgelashvili.moviesapp.fragments.MoviesGridFragment;
import com.kgelashvili.moviesapp.fragments.MoviesPageFragment;
import com.kgelashvili.moviesapp.fragments.SeriesPageFragment;
import com.kgelashvili.moviesapp.utils.SimpleSectionedListAdapter;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

        //_initMenu();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header2)
                .addProfiles(
                        //new ProfileDrawerItem().withName("Adjaranet").withEmail("").withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("ქართულად გახმოვანებული").withBadge("Ge"),
                        new PrimaryDrawerItem().withName("გამოწერილი სიახლეები").withIcon(FontAwesome.Icon.faw_newspaper_o),
                        new PrimaryDrawerItem().withName("ვაპირებ ყურებას").withIcon(FontAwesome.Icon.faw_thumbs_o_up),
                        new PrimaryDrawerItem().withName("ისტორია").withIcon(FontAwesome.Icon.faw_history),
                        new PrimaryDrawerItem().withName("პარამეტრები").withIcon(FontAwesome.Icon.faw_gears)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Intent i;
                        switch (position) {
                            case 4:

                                i = new Intent(MainActivity.this, settingsActivity.class);
                                startActivity(i);
                                break;
                            case 3:

                                i = new Intent(MainActivity.this, HistoryPageActivity.class);
                                startActivity(i);
                                break;
                            case 2:

                                i = new Intent(MainActivity.this, WatchLaterActivity.class);
                                startActivity(i);
                                break;
                            case 1:

                                i = new Intent(MainActivity.this, NewsCollectionActivity.class);
                                startActivity(i);
                                break;
                            case 0:

                                i = new Intent(MainActivity.this, GeoMoviesCollecitonActivity.class);
                                startActivity(i);
                                break;

                        }


                        return false;
                    }
                })
                .build();
        toolbar.setTitle("Adjaranet");
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



    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"მთავარი","კოლექციები", "ფილმები","სერიალები"};

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
                case 1:return MovieColectionsFragment.newInstance(position, findViewById(R.id.fragmentMainLinar), dbHelper2);
                case 2:return MoviesPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar),dbHelper2);
                case 3:return SeriesPageFragment.newInstance(position, findViewById(R.id.fragmentMainLinar));
                default:return MainPageFragment.newInstance(position,findViewById(R.id.fragmentMainLinar));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YoYo.with(Techniques.ZoomInLeft).playOn(findViewById(R.id.fragmentMainLinar));
    }


}
