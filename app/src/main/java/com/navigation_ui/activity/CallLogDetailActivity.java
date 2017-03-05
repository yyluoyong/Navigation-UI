package com.navigation_ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.navigation_ui.MyApplication;
import com.navigation_ui.R;
import com.navigation_ui.adapter.CallLogDetailRecyclerViewAdapter;
import com.navigation_ui.adapter.CallLogDetailRecyclerViewAdapter.PhoneNumberItemModel;
import com.navigation_ui.constant.ViewPagerPosition;
import com.navigation_ui.database.CallLogModelDBFlow;
import com.navigation_ui.database.CallLogModelDBFlow_Table;
import com.navigation_ui.utils.LogUtil;
import com.navigation_ui.utils.PermissionUtil;
import com.navigation_ui.utils.PhoneNumberFormatter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

public class CallLogDetailActivity extends AppCompatActivity {
    static final String TAG = "CallLogDetailActivity";

    //联系人的姓名
    public static final String CONTACTS_NAME = "CONTACTS_NAME";
    //电话号码
    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    private RecyclerView mRecyclerView;
    private CallLogDetailRecyclerViewAdapter mRecyclerViewAdapter;

    //通话记录item列表
    private List<CallLogModelDBFlow> mCallLogModelDBFlowList = new ArrayList<>();
    //电话号码item列表
    private List<PhoneNumberItemModel> mPhoneNumberItemList = new ArrayList<>();

    //该页面要展示的联系人的姓名
    private String mContactsName;
    //该页面对应的最近一次通话的电话号码
    private String mPhoneNumber;
    //该页面从ViewPager哪一页触发启动
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calllog_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Intent intent = getIntent();
        mContactsName = intent.getStringExtra(CONTACTS_NAME);
        mPhoneNumber = intent.getStringExtra(PHONE_NUMBER);
        mPosition = intent.getIntExtra(ViewPagerPosition.POSITION, ViewPagerPosition.POSITION_ALL_TYPE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
            findViewById(R.id.toolbar_layout);

        if (mContactsName.equals(mPhoneNumber)) {
            collapsingToolbar.setTitle(PhoneNumberFormatter.phoneNumberFormat(mPhoneNumber));
        } else {
            collapsingToolbar.setTitle(mContactsName);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CallLogDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new CallLogDetailRecyclerViewAdapter(CallLogDetailActivity.this,
            mPhoneNumberItemList, mCallLogModelDBFlowList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //初始化数据
        initCallLogs();
    }

    /**
     * 初始化构造View需要的数据
     */
    private void initCallLogs() {

        final ProgressDialog pgDialog = createProgressDialog(null,
            MyApplication.getContext().getString(R.string.readDatabaseIng));
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getCallModelList(mContactsName, mPosition);

                //更新UI页面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setPhoneNumberList(mPhoneNumberItemList);
                        mRecyclerViewAdapter.setCallLogList(mCallLogModelDBFlowList);
                        mRecyclerViewAdapter.notifyDataSetChanged();

                        pgDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private void getCallModelList(String name, int type) {
        if (type == ViewPagerPosition.POSITION_ALL_TYPE) {
            mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                .queryList();
        } else {
            mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                .and(CallLogModelDBFlow_Table.callType.eq(type))
                .queryList();
        }

        for (CallLogModelDBFlow model : mCallLogModelDBFlowList) {
            LogUtil.d(TAG, model.toString());
        }
    }

    /**
     * 一个圆圈的进度对话框
     * @param title
     * @param msg
     * @return
     */
    private ProgressDialog createProgressDialog(String title, String msg) {

        ProgressDialog pgDialog = new ProgressDialog(CallLogDetailActivity.this);

        pgDialog.setTitle(title);
        pgDialog.setMessage(msg);
        pgDialog.setCancelable(false);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(false);

        return pgDialog;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //使用PermissionUtils处理动态权限申请
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
