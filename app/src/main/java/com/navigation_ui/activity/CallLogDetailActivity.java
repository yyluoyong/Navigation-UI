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
import com.navigation_ui.R;
import com.navigation_ui.adapter.CallLogDetailRecyclerViewAdapter;
import com.navigation_ui.adapter.CallLogDetailRecyclerViewAdapter.PhoneNumberItemModel;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.tools.LogUtil;
import com.navigation_ui.tools.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class CallLogDetailActivity extends AppCompatActivity {

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
    private CallLogDetailRecyclerViewAdapter mRecyclerViewAdapter;

    //通话记录item列表
    private List<CallLogItemModel> mCallLogItemModelList = new ArrayList<>();
    //电话号码item列表
    private List<PhoneNumberItemModel> mPhoneNumberItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calllog_detail);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(CallLogDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new CallLogDetailRecyclerViewAdapter(CallLogDetailActivity.this,
            mPhoneNumberItemList, mCallLogItemModelList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

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

                for (int i = 0; i < 10; i++) {
                    CallLogItemModel callLogItemModel = new CallLogItemModel();
                    callLogItemModel.setContactsName("张三");
                    callLogItemModel.setPhoneNumber("13012341234");
                    callLogItemModel.setDateInMilliseconds("1485602523885");
                    callLogItemModel.setCallCounts(5);
                    callLogItemModel.setDuration("1230");
                    callLogItemModel.setCallType(1);
                    callLogItemModel.setCallerLoc("四川省绵阳市");

                    mCallLogItemModelList.add(callLogItemModel);
                }

                mPhoneNumberItemList.add(new PhoneNumberItemModel("15676399228", "广西桂林", "联通"));
                mPhoneNumberItemList.add(new PhoneNumberItemModel("18989275996", "四川绵阳", "电信"));
                mPhoneNumberItemList.add(new PhoneNumberItemModel("2345897", "未知归属地", "未知运营商"));
                mPhoneNumberItemList.add(new PhoneNumberItemModel("08162345897", "四川绵阳", "未知运营商"));

                //更新UI页面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setPhoneNumberList(mPhoneNumberItemList);
                        mRecyclerViewAdapter.setCallLogList(mCallLogItemModelList);
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
        PermissionUtils.onRequestPermissionsResult(CallLogDetailActivity.this,
            requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
