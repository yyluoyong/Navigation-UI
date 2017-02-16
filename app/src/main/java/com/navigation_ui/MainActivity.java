package com.navigation_ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.navigation_ui.adapter.MainViewPagerAdapter;
import com.navigation_ui.fragment.CallLogFragment;
import com.navigation_ui.fragment.FragmentIndex;
import com.navigation_ui.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatFB;
    private NavigationView mNavigationView;

    private String[] mTabLayoutTitles;
    private List<Fragment> mFragments;
    private MainViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar(); //初始化工具栏

        createFloatingActionButton(); //初始化悬浮按钮

        createDrawerLayout(); //初始化主界面布局

        createNavigation(); //初始化左边弹出菜单

        createViewPager(); //初始化ViewPager

        createTab(); //初始化TabLayout
    }

    //初始化工具栏
    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    //初始化悬浮按钮
    private void createFloatingActionButton() {
        mFloatFB = (FloatingActionButton) findViewById(R.id.fab);
        mFloatFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //初始化主界面布局
    private void createDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    //初始化左边弹出菜单
    private void createNavigation() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    //初始化FragmentList
    private void createFragmentList() {
        int tabCounts = getResources().getInteger(R.integer.TAB_COUNTS);

        mFragments = new ArrayList<>();
        for (int i = 1; i <= tabCounts; i++) {
            mFragments.add(new CallLogFragment());
        }
    }

    //初始化ViewPager
    private void createViewPager() {
        mTabLayoutTitles = getResources().getStringArray(R.array.TAB_TITLES);
        createFragmentList();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        mViewPager.setAdapter(mViewPagerAdapter);
        //Listener
    }

    //初始化TabLayout
    private void createTab() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
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
            // Handle the camera action
            mViewPagerAdapter.updateFragment(0);
        } else if (id == R.id.nav_gallery) {
            mViewPagerAdapter.updateFragment(1);
        } else if (id == R.id.nav_slideshow) {
            mViewPagerAdapter.updateFragment(2);
        } else if (id == R.id.nav_manage) {
            mViewPagerAdapter.updateFragment(3);
            LogUtil.d("Adapter", "onNavigationItemSelected");
        } else if (id == R.id.nav_share) {
//            mViewPagerAdapter.updateFragment(0);
        } else if (id == R.id.nav_send) {
//            mViewPagerAdapter.updateFragment(1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
