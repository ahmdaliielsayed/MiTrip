package com.mi.mitrip.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mi.mitrip.MapPackage.ShowHistoryMap;
import com.mi.mitrip.R;
import com.mi.mitrip.historypackage.HistoryFragment;
import com.mi.mitrip.fragment.ProfileFragment;
import com.mi.mitrip.upcomingpackage.UpcomingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private FloatingActionButton fab;
    Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_UPCOMING = "Upcoming";
    private static final String TAG_HISTORY = "History";
    private static final String TAG_PROFILE = "Profile";
    private static final String TAG_SYNC = "Sync";
    private static final String TAG_SIGNOUT = "Sign out";
    public static String CURRENT_TAG = TAG_UPCOMING;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    CircleImageView initialPhoto;
    TextView txtViewName, txtViewMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        // up arrow and transform to navigation drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // change the icon
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        // change title
        getSupportActionBar().setTitle("My Trips");
         */

        mHandler = new Handler();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);
        fab = findViewById(R.id.fab);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TripActivity.class));
            }
        });

        // load nav menu header data
        //loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });*/

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_UPCOMING;
            loadUpcomingFragment();
        }

        loadUserInformation();
    }

    private void loadUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            NavigationView navigationView = findViewById(R.id.navView);
            View header = navigationView.getHeaderView(0);

            initialPhoto = header.findViewById(R.id.initialPhoto);
            txtViewName = header.findViewById(R.id.txtViewName);
            txtViewMail = header.findViewById(R.id.txtViewMail);

            if (user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(initialPhoto);
            }

            if (user.getDisplayName() != null){
                txtViewName.setText(user.getDisplayName());
            }

            if (user.getEmail() != null){
                txtViewMail.setText(user.getEmail());
            }
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    /*
    private void loadNavHeader() {
        // name, website
        txtName.setText("Ravi Tamada");
        txtWebsite.setText("www.androidhive.info");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }
    */

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadUpcomingFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // upcoming
                UpcomingFragment upcomingFragment = new UpcomingFragment();
                return upcomingFragment;
            case 1:
                // history
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;
            case 2:
                // profile
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            /*case 3:
                // sync fragment
                SyncFragment syncFragment = new SyncFragment();
                return syncFragment;

            case 4:
                // signout
                SignoutFragment signoutFragment = new SignoutFragment();
                return signoutFragment;*/
            default:
                return new UpcomingFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.upcoming:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_UPCOMING;
                        break;
                    case R.id.history:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_HISTORY;
                        break;
                    case R.id.historyMap:
                        /*navItemIndex = 2;
                        CURRENT_TAG = TAG_PROFILE;*/
//                        Intent openMapActivity =new Intent(HomeActivity.this, MapsActivity.class);
//                        startActivity(openMapActivity);
                        Intent openMapActivity = new Intent(HomeActivity.this, ShowHistoryMap.class);
                        openMapActivity.putExtra("key", "allHistoryTrips");
                        openMapActivity.putExtra("source", "");
                        openMapActivity.putExtra("destination", "");
                        startActivity(openMapActivity);

                        break;

                    /*case R.id.sync:
                        navItemIndex = 3; Intent openMapActivity =new Intent(HomeActivity.this, MapsActivity.class);
                        startActivity(openMapActivity);

                        CURRENT_TAG = TAG_SYNC;
                        break;
                    case R.id.logout:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SIGNOUT;
                        break;
                     */
                    case R.id.logout:
                        // launch new intent instead of loading fragment
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                        drawerLayout.closeDrawers();
                        finish();
                        return true;
                    /*case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                     */
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                Log.i("INDExxxxxxxxxxxx = ", navItemIndex + "");

                loadUpcomingFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_UPCOMING;
                loadUpcomingFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        /*if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
         */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.item_tips:
                startActivity(new Intent(this, TipsActivity.class));
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                drawerLayout.closeDrawers();
                finish();
                return true;
        }

        /*
        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }
        */
        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
}
