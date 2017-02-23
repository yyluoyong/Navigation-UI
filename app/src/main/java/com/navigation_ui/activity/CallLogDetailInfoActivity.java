package com.navigation_ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.navigation_ui.R;
import com.navigation_ui.adapter.DetailRecyclerViewAdapter;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

public class CallLogDetailInfoActivity extends AppCompatActivity {

    //联系人的姓名
    public static final String CONTACTS_NAME = "contacts_name";
    //去电
    public static final String CALL_MADE = "call_made";
    //来电
    public static final String CALL_RECEIVED = "call_received";
    //未接
    public static final String CALL_MISSED = "call_missed";
    //所有类型
    public static final String CALL_ALL = "call_all";
    //要展示的类型
    public static final String CALL_TYPE = CALL_ALL;

    private RecyclerView mRecyclerView;
    private DetailRecyclerViewAdapter mRecyclerViewAdapter;

    private List<CallLogItemModel> callLogItemModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call_log_detail_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Intent intent = getIntent();
        String contactsName = intent.getStringExtra(CONTACTS_NAME);
        String callType = intent.getStringExtra(CALL_TYPE);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
            findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(contactsName);
//        collapsingToolbar.setContentScrimColor(MaterialDesignColor.MDAmber);

        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CallLogDetailInfoActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new DetailRecyclerViewAdapter(callLogItemModelList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerView.addItemDecoration(new RecycleViewDivider(
            CallLogDetailInfoActivity.this, LinearLayoutManager.VERTICAL, R.drawable.divider_1px));

        mRecyclerView.addItemDecoration(new RecycleViewDivider(CallLogDetailInfoActivity.this, LinearLayoutManager.VERTICAL));

        //初始化数据
        initCallLogs();
    }

    /**
     * 初始化构造View需要的数据
     */
    private void initCallLogs() {

        final ProgressDialog pgDialog = createProgressDialog(null, "正在读取，请稍后...");
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //undo：展示读取耗时操作
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 20; i++) {
                    CallLogItemModel callLogItemModel = new CallLogItemModel();
                    callLogItemModel.setContactsName("张三");
                    callLogItemModel.setPhoneNumber("13012341234");
                    callLogItemModel.setDateInMilliseconds("1485602523885");
                    callLogItemModel.setCallCounts(5);
                    callLogItemModel.setDuration("12");
                    callLogItemModel.setCallType(1);
                    callLogItemModel.setCallerLoc("四川省绵阳市");

                    callLogItemModelList.add(callLogItemModel);
                }

                //更新UI页面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                        mRecyclerViewAdapter.notifyDataSetChanged();

                        pgDialog.dismiss();
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

        ProgressDialog pgDialog = new ProgressDialog(CallLogDetailInfoActivity.this);

        pgDialog.setTitle(title);
        pgDialog.setMessage(msg);
        pgDialog.setCancelable(false);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(false);

        return pgDialog;
    }
}
