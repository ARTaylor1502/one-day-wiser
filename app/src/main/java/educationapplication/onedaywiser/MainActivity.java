package educationapplication.onedaywiser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Session mSession;
    private TextView mAchievementPoints;
    private TextView mParticipationPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSession = new Session(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        mAchievementPoints = (TextView) findViewById(R.id.achievement_points);
        mParticipationPoints = (TextView) findViewById(R.id.participation_points);

        mAchievementPoints.setText(mSession.getAchievementPoints());
        mParticipationPoints.setText(mSession.getParticipationPoints());

        Fragment dashboardFragment = new DashboardFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main,dashboardFragment,dashboardFragment.getTag()).commit();
    }

    protected void onDestroy(){
        super.onDestroy();
        mSession.endSession();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();

        switch(id){
            case R.id.nav_dashboard:
                DashboardFragment dashboardFragment = new DashboardFragment();
                manager.beginTransaction().replace(R.id.content_main,dashboardFragment,dashboardFragment.getTag()).commit();
                break;
            case R.id.nav_lessons:
                SubjectsFragment subjectsFragment = new SubjectsFragment();
                manager.beginTransaction().replace(R.id.content_main,subjectsFragment,subjectsFragment.getTag()).commit();
                break;
            case R.id.nav_group_leaderboard:
                GroupFragment groupFragment = new GroupFragment();
                manager.beginTransaction().replace(R.id.content_main,groupFragment,groupFragment.getTag()).commit();
                break;
            case R.id.nav_trophies:
                TrophiesFragment trophiesFragment = new TrophiesFragment();
                manager.beginTransaction().replace(R.id.content_main,trophiesFragment,trophiesFragment.getTag()).commit();
                break;
            case R.id.nav_aquarium:
                AquariumFragment aquariumFragment = new AquariumFragment();
                manager.beginTransaction().replace(R.id.content_main,aquariumFragment,aquariumFragment.getTag()).commit();
                break;
            case R.id.nav_shop:
                ShopFragment ShopFragment = new ShopFragment();
                manager.beginTransaction().replace(R.id.content_main,ShopFragment,ShopFragment.getTag()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setParticipationPoints(String participationPoints){
        mParticipationPoints.setText(participationPoints);
    }

    public void setAchievementPoints(String achievementPoints){
        mAchievementPoints.setText(achievementPoints);
    }

}
