package com.lxndr.yourmovies;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lxndr.yourmovies.fragment.FavouritesMoviesFragment;
import com.lxndr.yourmovies.fragment.IMovies;
import com.lxndr.yourmovies.fragment.MostPopularMoviesFragment;
import com.lxndr.yourmovies.fragment.TopRatedMoviesFragment;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_CRITERIA_KEY = "sort_criteria";

    private NavigationView navigationView;

    private Menu mMenu = null;

    @Config.SortCriteria
    int sortCriteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // restore sorted Criteria
        if ( savedInstanceState != null ) {

            if (savedInstanceState.containsKey(SORT_CRITERIA_KEY)) {

                int sortCr = savedInstanceState.getInt(SORT_CRITERIA_KEY);

                if (sortCr == 1) {
                    setSortCriteria(Config.SORT_BY_TOP_RATED);
                    handleNavigationMenuOptionSelected(1);

                } else if (sortCr == 2) {
                    setSortCriteria(Config.SORT_BY_FAVOURITES);
                    handleNavigationMenuOptionSelected(2);

                } else {
                    setSortCriteria(Config.SORT_BY_POPULARITY);
                    handleNavigationMenuOptionSelected(0);
                }
            }

        } else {
            int sortCr = TheApplication.getInstance().getSortCriteria();
            showMoviesFragment(sortCr);
            handleNavigationMenuOptionSelected(sortCr);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_CRITERIA_KEY, sortCriteria);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TheApplication.getInstance().getSortCriteria() == Config.SORT_BY_FAVOURITES) {
            ((IMovies) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onMoviesRefresh();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int sortCr = getSortCriteria();
        if (sortCr == 1) {
            setupMenuOptionSelected(menu, R.id.menu_top_rated);
        } else if (sortCr == 2) {
            setupMenuOptionSelected(menu, R.id.menu_favourites);
        } else {
            setupMenuOptionSelected(menu, R.id.menu_most_popular);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void showMoviesFragment(@Config.SortCriteria int sortCriteria) {

        // Create new fragment and transaction
        Fragment newFragment;
        setSortCriteria(sortCriteria);

        if (sortCriteria == Config.SORT_BY_TOP_RATED) {
            newFragment = new TopRatedMoviesFragment();
        } else if (sortCriteria == Config.SORT_BY_FAVOURITES) {
            newFragment = new FavouritesMoviesFragment();
        } else {
            newFragment = new MostPopularMoviesFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);


        mMenu = menu;
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            ((IMovies) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onMoviesRefresh();

        } else if (id == R.id.menu_most_popular && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_most_popular);
            handleNavigationMenuOptionSelected(0);

            showMoviesFragment(Config.SORT_BY_POPULARITY);

        } else if (id == R.id.menu_top_rated && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_top_rated);
            handleNavigationMenuOptionSelected(1);

            showMoviesFragment(Config.SORT_BY_TOP_RATED);

        } else if (id == R.id.menu_favourites && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_favourites);
            handleNavigationMenuOptionSelected(2);

            showMoviesFragment(Config.SORT_BY_FAVOURITES);
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleMenuOptionSelected(int id) {
        MenuItem mi = getMenuItemById(R.id.menu_top_rated);
        mi.setChecked(mi.getItemId() == id);
        mi = getMenuItemById(R.id.menu_most_popular);
        mi.setChecked(mi.getItemId() == id);
        mi = getMenuItemById(R.id.menu_favourites);
        mi.setChecked(mi.getItemId() == id);
    }



    private void setupMenuOptionSelected(Menu menu, int id) {
        MenuItem mi = menu.findItem(R.id.menu_top_rated);
        mi.setChecked(mi.getItemId() == id);
        mi = menu.findItem(R.id.menu_most_popular);
        mi.setChecked(mi.getItemId() == id);
        mi = menu.findItem(R.id.menu_favourites);
        mi.setChecked(mi.getItemId() == id);
    }

    private void handleNavigationMenuOptionSelected(int index) {
        navigationView.getMenu().getItem(0).setChecked(index == 0);
        navigationView.getMenu().getItem(1).setChecked(index == 1);
        navigationView.getMenu().getItem(2).setChecked(index == 2);
    }

    private MenuItem getMenuItemById(int id) {
        return mMenu.findItem(id);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_most_popular && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_most_popular);
            handleNavigationMenuOptionSelected(0);

            showMoviesFragment(Config.SORT_BY_POPULARITY);

        } else if (id == R.id.nav_top_rated && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_top_rated);
            handleNavigationMenuOptionSelected(1);

            showMoviesFragment(Config.SORT_BY_TOP_RATED);

        } else if (id == R.id.nav_favourites && !item.isChecked()) {
            handleMenuOptionSelected(R.id.menu_favourites);
            handleNavigationMenuOptionSelected(2);

            showMoviesFragment(Config.SORT_BY_FAVOURITES);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setSortCriteria(@Config.SortCriteria int value) {
        TheApplication.getInstance().setSortCriteria(value);
        this.sortCriteria = value;
    }

    private @Config.SortCriteria int getSortCriteria() {
        return sortCriteria;
    }

    public void restoreConfigurations(Bundle savedInstanceState) {

    }
}