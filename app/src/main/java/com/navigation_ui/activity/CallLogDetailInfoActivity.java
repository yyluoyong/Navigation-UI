package com.navigation_ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.navigation_ui.R;
import com.navigation_ui.tools.MaterialDesignColor;

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
    }
}
