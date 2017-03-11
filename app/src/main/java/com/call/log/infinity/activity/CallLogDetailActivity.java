package com.call.log.infinity.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.adapter.CallLogDetailRecyclerViewAdapter;
import com.call.log.infinity.adapter.CallLogDetailRecyclerViewAdapter.PhoneNumberItemModel;
import com.call.log.infinity.constant.ViewPagerPosition;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.database.CallLogModelDBFlow_Table;
import com.call.log.infinity.utils.QueryContactsUtil;
import com.call.log.infinity.utils.CallerLocationAndOperatorQueryUtil;
import com.call.log.infinity.utils.LogUtil;
import com.call.log.infinity.utils.PhoneNumberFormatter;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

public class CallLogDetailActivity extends BaseActivity {
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
    //ViewPager触发页面对应的通话类型
    private int mCallType;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calllog_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        mContactsName = intent.getStringExtra(CONTACTS_NAME);
        mPhoneNumber = intent.getStringExtra(PHONE_NUMBER);
        mPosition = intent.getIntExtra(ViewPagerPosition.POSITION, ViewPagerPosition.POSITION_ALL_TYPE);
        mCallType = mPosition;

        //设置联系人姓名
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        if (mContactsName.equals(mPhoneNumber)) {
            mCollapsingToolbar.setTitle(PhoneNumberFormatter.phoneNumberFormat(mPhoneNumber));
        } else {
            mCollapsingToolbar.setTitle(mContactsName);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CallLogDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new CallLogDetailRecyclerViewAdapter(CallLogDetailActivity.this,
            mPhoneNumberItemList, mCallLogModelDBFlowList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //初始化主题颜色
        setThemeAtStart();

        //初始化数据
        initCallLogs();
    }

    /**
     * 初始化构造View需要的数据
     */
    private void initCallLogs() {
        //初始化电话列表
        initPhoneNumberItemList(mContactsName, mPhoneNumber);

        //初始化通话记录列表
        initCallLogModelDBFlowList(mContactsName, mCallType);
    }

    private void getCallModelList(String name, int type) {
        if (type == ViewPagerPosition.POSITION_ALL_TYPE) {
            mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                .queryList();
        } else {
            if (type == ViewPagerPosition.POSITION_INCOMING_TYPE) {
                mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                    .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                    .and(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                        .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE))))
                    .queryList();
            } else if (type == ViewPagerPosition.POSITION_MISSED_TYPE){
                mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                    .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                    .and(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE))
                        .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                            .and(CallLogModelDBFlow_Table.duration.eq("0"))))
                    .queryList();
            } else {
                mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
                    .where(CallLogModelDBFlow_Table.contactsName.eq(name))
                    .and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.OUTGOING_TYPE))
                    .queryList();
            }
        }

        for (CallLogModelDBFlow model : mCallLogModelDBFlowList) {
            LogUtil.d(TAG, model.toString());
        }
    }

    /**
     * 初始化当前联系人的电话模型列表。
     * @param contactsName：条目上联系人的姓名
     * @param phoneNumber：条目上的电话号码
     */
    private void initPhoneNumberItemList(String contactsName, String phoneNumber) {
        if (contactsName.equals(phoneNumber)) {
            String[] areaAndOperator = CallerLocationAndOperatorQueryUtil.callerLocationAndOperatorQuery(phoneNumber);
            String callerLoc = areaAndOperator[0];
            String operator = areaAndOperator[1];

            PhoneNumberItemModel model = new PhoneNumberItemModel(phoneNumber, callerLoc, operator);
            mPhoneNumberItemList.add(model);

            mRecyclerViewAdapter.setPhoneNumberList(mPhoneNumberItemList);
            mRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            QueryContactsUtil.queryContactsPhoneNumber(CallLogDetailActivity.this, contactsName,
                new QueryContactsUtil.OnQueryContactsPhoneNumberListener() {
                    @Override
                    public void onQuerySuccess(ArrayList<String> phoneNumberList) {
                        for (int i = 0; i < phoneNumberList.size(); i++) {
                            String[] areaAndOperator = CallerLocationAndOperatorQueryUtil.callerLocationAndOperatorQuery(phoneNumberList.get(i));
                            String callerLoc = areaAndOperator[0];
                            String operator = areaAndOperator[1];

                            if (TextUtils.isEmpty(callerLoc)) {
                                callerLoc = CallerLocationAndOperatorQueryUtil.UNKOWN_AREA;
                                operator = CallerLocationAndOperatorQueryUtil.UNKOWN_OPERATOR;
                            }

                            PhoneNumberItemModel model = new PhoneNumberItemModel(phoneNumberList.get(i), callerLoc, operator);
                            mPhoneNumberItemList.add(model);
                        }

                        //更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerViewAdapter.setPhoneNumberList(mPhoneNumberItemList);
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onQueryFailed() {
                        Toast.makeText(CallLogDetailActivity.this, getString(R.string.refusePermissionMessage),
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    /**
     * 初始化通话记录模型列表。
     * @param contactsName：条目上联系人的姓名
     * @param type：通话类型
     */
    private void initCallLogModelDBFlowList(final String contactsName, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCallModelList(contactsName, type);

                //更新UI页面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setCallLogList(mCallLogModelDBFlowList);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        ((ImageView) findViewById(R.id.imageView)).setColorFilter(MyApplication.getThemeColorPrimary());
        mToolbar.setBackgroundColor(MyApplication.getThemeColorPrimary());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }
}
