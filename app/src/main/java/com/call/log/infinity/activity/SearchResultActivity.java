package com.call.log.infinity.activity;

import android.os.Build;
import android.provider.CallLog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.adapter.SearchResultRecyclerViewAdapter;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.database.CallLogModelDBFlow_Table;
import com.call.log.infinity.model.SearchModel;
import com.call.log.infinity.utils.CallerLocationAndOperatorQueryUtil;
import com.call.log.infinity.utils.LogUtil;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends BaseActivity {
    static final String TAG = "SearchResultActivity";

    private RecyclerView mRecyclerView;
    private SearchResultRecyclerViewAdapter mRecyclerViewAdapter;

    private List<CallLogModelDBFlow> callLogModelList = new ArrayList<>();

    private SearchModel mSearchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchResultActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new SearchResultRecyclerViewAdapter(SearchResultActivity.this, callLogModelList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mSearchModel = (SearchModel) getIntent().getParcelableExtra("SearchModel");

        //初始化主题颜色
        setThemeAtStart();

        initCallLogList();
    }

    private void initCallLogList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCallLogModelList();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setCallLogList(callLogModelList);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 获得搜索结果列表。
     */
    private void getCallLogModelList() {
        callLogModelList = SQLite.select().from(CallLogModelDBFlow.class)
            .where(getDBFlowConditionGroup())
            .orderBy(CallLogModelDBFlow_Table.dateInMilliseconds, false)
            .queryList();

        for (CallLogModelDBFlow model : callLogModelList) {
            String[] areaAndOperator = CallerLocationAndOperatorQueryUtil
                .callerLocationAndOperatorQuery(model.getPhoneNumber());
            String callerLoc = areaAndOperator[0];
            String operator = areaAndOperator[1];

            if (TextUtils.isEmpty(callerLoc)) {
                callerLoc = CallerLocationAndOperatorQueryUtil.UNKOWN_AREA;
                operator = CallerLocationAndOperatorQueryUtil.UNKOWN_OPERATOR;
            }

            model.setCallerLoc(callerLoc);
            model.setOperator(operator);

            LogUtil.d(TAG, model.toString());
        }
    }

    /**
     * 用搜索模型构造搜索条件。
     */
    private ConditionGroup getDBFlowConditionGroup() {
        ConditionGroup sqlCondition = ConditionGroup.clause();

        if (!TextUtils.isEmpty(mSearchModel.getContactsName())) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.contactsName.eq(mSearchModel.getContactsName()));
        }

        if (!TextUtils.isEmpty(mSearchModel.getPhoneNumber())) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.phoneNumber.eq(mSearchModel.getPhoneNumber()));
        }

        if (mSearchModel.getDateStart() > 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.dateInMilliseconds
                .greaterThanOrEq(mSearchModel.getDateStart()));
        }

        if (mSearchModel.getDateEnd() > 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.dateInMilliseconds
                .lessThanOrEq(mSearchModel.getDateEnd()));
        }

        if (mSearchModel.getDurationMin() >= 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.duration
                .greaterThanOrEq(mSearchModel.getDurationMin()));
        }

        if (mSearchModel.getDurationMax() >= 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.duration
                .lessThanOrEq(mSearchModel.getDurationMax()));
        }

        if (mSearchModel.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.OUTGOING_TYPE));
        } else if (mSearchModel.getCallType() == CallLog.Calls.INCOMING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE)));
        } else if (mSearchModel.getCallType() == CallLog.Calls.MISSED_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                    .and(CallLogModelDBFlow_Table.duration.eq(0)));
        }

        return sqlCondition;
    }

    /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }
}
