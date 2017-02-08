package com.navigation_ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.navigation_ui.Fragment.FragmentButton;
import com.navigation_ui.Fragment.FragmentIndex;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragmentButton, fragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_camera); //初始化选择第一个按钮
        navigationView.setNavigationItemSelectedListener(this);

        /*
        初始化第一个按钮对应的Fragment
         */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragmentButton == null) {
            fragmentButton = new FragmentButton();
            fragmentTransaction.add(R.id.content_main, fragmentButton);
            fragmentTransaction.commit();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            handleNavCamera();
        }
        else if (id == R.id.nav_gallery) {
            handleNavGallery();
        }
        else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 隐藏已经初始化的Fragment
     * @param tran
     */
    private void hideFragment(FragmentTransaction tran) {
        if (fragmentButton != null) {
            tran.hide(fragmentButton);
        }
        if (fragmentIndex != null) {
            tran.hide(fragmentIndex);
        }
    }

    /**
     * 处理第一个按钮的事件
     */
    private void handleNavCamera() {

        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();

        hideFragment(tran);

        if (fragmentButton == null) {
            fragmentButton = new FragmentButton();
            tran.add(R.id.content_main, fragmentButton);
        }
        else {
            tran.show(fragmentButton);
        }

        tran.commit();
    }

    /**
     * 处理第二个按钮的事件
     */
    private void handleNavGallery() {

        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();

        hideFragment(tran);

        if (fragmentIndex == null) {
            fragmentIndex = new FragmentIndex();
            tran.add(R.id.content_main, fragmentIndex);
        }
        else {
            tran.show(fragmentIndex);
        }

        tran.commit();
    }
}
