package com.navigation_ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.navigation_ui.R;
import com.navigation_ui.adapter.MainViewPagerAdapter;
import com.navigation_ui.database.CallLogModelDBFlow;
import com.navigation_ui.database.WriteCallLogToDatabaseTool;
import com.navigation_ui.fragment.view.pager.UpdateFragmentObservable;
import com.navigation_ui.tools.PermissionUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FloatingActionButton mFloatFB;
    private NavigationView mNavigationView;

    private MainViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化工具栏
        createToolbar();

        //初始化悬浮按钮
        createFloatingActionButton();

        //初始化主界面布局
        createDrawerLayout();

        //初始化左边弹出菜单
        createNavigation();

        //初始化ViewPager
        createViewPager();

        //初始化TabLayout
        createTab();

        //更新数据库
        updateCallLogDatabase();
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
                //让ViewPager中的Fragment更新数据
                UpdateFragmentObservable.getInstance().notifyFragmentUpdate();
            }
        });
    }

    //初始化主界面布局
    private void createDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //初始化左边弹出菜单
    private void createNavigation() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    //初始化ViewPager
    private void createViewPager() {

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(mViewPagerAdapter);

        //设置页面缓存数量，防止重新加载
        mViewPager.setOffscreenPageLimit(getResources().getInteger(R.integer.TAB_COUNTS));

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
        if (id == R.id.action_search) {
            Snackbar.make(mToolbar, "搜索功能待完善", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_table) {
            Toast.makeText(MainActivity.this, "点击'数据库可视化'按钮，功能待完善",
                Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_refresh) {
            Toast.makeText(MainActivity.this, "点击'刷新DB联系人'按钮，功能待完善",
                Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_copy) {
            Toast.makeText(MainActivity.this, "点击'复制DB'按钮，功能待完善",
                Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_export) {
            Toast.makeText(MainActivity.this, "点击'导出DB'按钮，功能待完善",
                Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_delete) {
            Delete.tables(CallLogModelDBFlow.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 更新通话记录数据库。
     */
    private void updateCallLogDatabase() {

        final String[] queryProjection = new String[]{
            CallLog.Calls.CACHED_NAME,  //姓名
            CallLog.Calls.NUMBER,       //号码
            CallLog.Calls.TYPE,         //呼入/呼出(2)/未接
            CallLog.Calls.DATE,         //拨打时间
            CallLog.Calls.DURATION};    //通话时长

        final ProgressDialog pgDialog = createProgressDialog(null, "正在更新数据库，请稍后...");
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                PermissionUtils.requestPermissions(MainActivity.this,
                    PermissionUtils.REQUEST_CODE, new String[]{Manifest.permission.READ_CALL_LOG},
                    new PermissionUtils.OnPermissionListener() {
                        /**
                         * 进行读取通话记录并存储到数据的任务
                         */
                        @Override
                        public void onPermissionGranted() {
                            Cursor cursor = null;

                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                                cursor = MainActivity.this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                                    queryProjection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                            }

                            WriteCallLogToDatabaseTool dbTool = new WriteCallLogToDatabaseTool();
                            final int countNewRecords = dbTool.getNewCallLogCount(cursor);

                            dbTool.asyncSaveToDatabase(
                                new WriteCallLogToDatabaseTool.DBFlowDatabaseSaveCallback() {
                                /**
                                 * 存储成功后的回调接口。（注：该方法在UI线程执行。）
                                 */
                                @Override
                                public void success() {
                                    pgDialog.dismiss();

                                    if (countNewRecords == 0) {
                                        Toast.makeText(MainActivity.this, "无新的通话记录！",
                                            Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "新增 " + countNewRecords +
                                            " 条通话记录", Toast.LENGTH_LONG).show();
                                    }

                                    UpdateFragmentObservable.getInstance().notifyFragmentUpdate();
                                }
                            });
                        }

                        /**
                         * 见PermissionUtils类的“说明一”
                         */
                        @Override
                        public void onPermissionDenied() {
                            Toast.makeText(MainActivity.this, "您拒绝了权限申请，功能无法使用",
                                Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        //使用PermissionUtils处理动态权限申请
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 一个圆圈的进度对话框
     * @param title
     * @param msg
     * @return
     */
    private ProgressDialog createProgressDialog(String title, String msg) {

        ProgressDialog pgDialog = new ProgressDialog(MainActivity.this);

        pgDialog.setTitle(title);
        pgDialog.setMessage(msg);
        pgDialog.setCancelable(false);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(false);

        return pgDialog;
    }
}
