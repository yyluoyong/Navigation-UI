package com.call.log.infinity.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.adapter.MainViewPagerAdapter;
import com.call.log.infinity.database.CallLogDatabase;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.database.CallLogModelDBFlow_Table;
import com.call.log.infinity.database.CopyDatabaseToSDCardUtil;
import com.call.log.infinity.database.UpdateDatabaseContactsUtil;
import com.call.log.infinity.utils.QueryContactsUtil;
import com.call.log.infinity.database.WriteCallLogToDatabaseUtil;
import com.call.log.infinity.fragment.UpdateFragmentObservable;
import com.call.log.infinity.utils.LogUtil;
import com.call.log.infinity.utils.PermissionUtil;
import com.call.log.infinity.view.ThemeDialog;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity
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

    @Override
    protected void onResume() {
        super.onPostResume();
        //设置主题
        setThemeAtStart();
    }

    //初始化工具栏
    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.toolbar_title));
        setSupportActionBar(mToolbar);
    }

    //初始化悬浮按钮
    private void createFloatingActionButton() {
        mFloatFB = (FloatingActionButton) findViewById(R.id.fab);
        mFloatFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //让ViewPager中的Fragment更新数据
                updateCallLogDatabase();
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
        mNavigationView.setItemIconTintList(null);
    }

    //初始化ViewPager
    private void createViewPager() {

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(mViewPagerAdapter);

        //设置页面缓存数量，防止重新加载
        mViewPager.setOffscreenPageLimit(getResources().getInteger(R.integer.TAB_COUNTS));
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
//            Toast.makeText(MainActivity.this, "点击'数据库可视化'按钮，功能待完善",
//                Toast.LENGTH_SHORT).show();
            QueryContactsUtil.queryPhoneNumberBelongTo(MainActivity.this, "15676399228",
                new QueryContactsUtil.OnQueryPhoneNumberBelongToListener() {
                    @Override
                    public void onQuerySuccess(String contactsName) {
                        LogUtil.d(TAG, "onQuerySuccess " + contactsName);
                    }

                    @Override
                    public void onQueryFailed() {

                    }
            });

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            QueryContactsUtil.queryPhoneNumberBelongTo(MainActivity.this, "123456",
                new QueryContactsUtil.OnQueryPhoneNumberBelongToListener() {
                    @Override
                    public void onQuerySuccess(String contactsName) {
                        LogUtil.d(TAG, "onQuerySuccess " + contactsName);
                    }

                    @Override
                    public void onQueryFailed() {

                    }
                });

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            QueryContactsUtil.queryPhoneNumberBelongTo(MainActivity.this, "18142559501",
                new QueryContactsUtil.OnQueryPhoneNumberBelongToListener() {
                    @Override
                    public void onQuerySuccess(String contactsName) {
                        LogUtil.d(TAG, "onQuerySuccess " + contactsName);
                    }

                    @Override
                    public void onQueryFailed() {

                    }
                });
        } else if (id == R.id.nav_refresh) {
            QueryContactsUtil.queryPhoneNumberAndContactsNameMap(MainActivity.this,
                new QueryContactsUtil.OnQueryPhoneNumberAndContactsNameMapListener() {
                    @Override
                    public void onQuerySuccess(HashMap<String, String> phoneNumberAndContactsName) {
                        UpdateDatabaseContactsUtil.updateDatabaseContacts(phoneNumberAndContactsName,
                            new UpdateDatabaseContactsUtil.OnUpdateDatabaseContactsListener() {
                                @Override
                                public void success() {
                                    UpdateFragmentObservable.getInstance().notifyFragmentUpdate();
                                }
                            });
                    }

                    @Override
                    public void onQueryFailed() {
                    }
                }
            );

            SQLite.update(CallLogModelDBFlow.class)
                .set(CallLogModelDBFlow_Table.contactsName.eq("未知"))
                .where(CallLogModelDBFlow_Table.phoneNumber.eq("15181637287"));
        } else if (id == R.id.nav_copy) {
            setCopyDatabaseListener();
        } else if (id == R.id.nav_export) {
            Toast.makeText(MainActivity.this, "点击'导出DB'按钮，功能待完善",
                Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_delete) {
            setClearDababaseTableListener();
        } else if (id == R.id.nav_theme) {
            setThemeListener();
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

        final ProgressDialog pgDialog = createProgressDialog(null, getString(R.string.updateDatabaseIng));
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                PermissionUtil.requestPermissions(MainActivity.this,
                    PermissionUtil.REQUEST_CODE, new String[]{Manifest.permission.READ_CALL_LOG},
                    new PermissionUtil.OnPermissionListener() {
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

                            WriteCallLogToDatabaseUtil dbTool = new WriteCallLogToDatabaseUtil();
                            final int countNewRecords = dbTool.getNewCallLogCount(cursor);

                            dbTool.asyncSaveToDatabase(
                                new WriteCallLogToDatabaseUtil.DBFlowDatabaseSaveCallback() {
                                    /**
                                     * 存储成功后的回调接口。（注：该方法在UI线程执行。）
                                     */
                                    @Override
                                    public void success() {
                                        pgDialog.dismiss();

                                        if (countNewRecords == 0) {
                                            Toast.makeText(MainActivity.this, getString(R.string.newRecordNull),
                                                Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this,
                                                String.format(getString(R.string.newRecordMessage), countNewRecords),
                                                Toast.LENGTH_LONG).show();
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
                            Toast.makeText(MainActivity.this, R.string.refusePermissionMessage,
                                Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }).start();
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

    /**
     * 复制数据库的监听事件。
     */
    private void setCopyDatabaseListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isSuccess = CopyDatabaseToSDCardUtil.copyDatabaseToSDCard(MainActivity.this
                    .getDatabasePath(CallLogDatabase.NAME + ".db").getPath());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess) {
                            String successString = String.format(getString(R.string.copyDatabaseSuccess),
                                getPackageName(), CallLogDatabase.NAME + ".db");
                            Toast.makeText(MainActivity.this, successString, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.copyDatabaseFailed),
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 清空数据库的监听事件，产生一个对话框。
     */
    private void setClearDababaseTableListener() {
        new MaterialDialog.Builder(this)
            .iconRes(R.drawable.ic_warning)
            .limitIconToDefaultSize() // limits the displayed icon size to 48dp
            .title(R.string.clearDatabaseTableTitle)
            .content(R.string.clearDatabaseTablePrompt, true)
            .positiveText(R.string.goOn)
            .negativeText(R.string.dialog_cancel)
            .neutralText(R.string.backupThenClear)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Delete.tables(CallLogModelDBFlow.class);
                    Toast.makeText(MainActivity.this, getString(R.string.clearDatabaseTableSuccess),
                        Toast.LENGTH_LONG).show();

                }
            })
            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    setCopyDatabaseListener();
                    Delete.tables(CallLogModelDBFlow.class);
                }
            })
            .show();
    }

    /**
     * 为选择主题设置监听事件。
     */
    private void setThemeListener() {
        ThemeDialog dialog = new ThemeDialog();
        dialog.show(getSupportFragmentManager(), getString(R.string.themeDialog));
    }

    /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        mToolbar.setBackgroundColor(MyApplication.getThemeColorPrimary());
        mTabLayout.setBackgroundColor(MyApplication.getThemeColorPrimary());
        mFloatFB.setBackgroundTintList(ColorStateList.valueOf(MyApplication.getThemeColorAccent()));

        View navHeaderLayout = (View) mNavigationView.findViewById(R.id.nav_header_layout);
        if (navHeaderLayout != null) {
            navHeaderLayout.setBackground(new ColorDrawable(MyApplication.getThemeColorPrimaryDark()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }
}
