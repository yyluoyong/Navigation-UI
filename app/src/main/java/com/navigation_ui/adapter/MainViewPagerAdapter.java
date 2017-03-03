package com.navigation_ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.navigation_ui.R;
import com.navigation_ui.database.CallLogDatabase;
import com.navigation_ui.fragment.view.pager.CallLogFragment;
import com.navigation_ui.utils.LogUtil;

/**
 * Created by Yong on 2017/2/11.
 */

/**
 * 主界面内容部分ViewPager的适配器
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "MainViewPagerAdapter";

    private FragmentManager mFragmentManager;

    private Context context;

    //页面数量
    private static int mPageCounts;
    //页面标题
    private String[] mPageTitles;

    public MainViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.mFragmentManager = fm;
        this.context = context;

        initData();
    }

    private void initData() {
        mPageCounts = context.getResources().getInteger(R.integer.MAIN_PAGE_COUNTS);
        mPageTitles = context.getResources().getStringArray(R.array.MAIN_PAGE_TITLES);
    }

    /**
     * 创建一个新的Fragment实例的时候调用
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(final int position) {
        CallLogFragment callLogFragment = new CallLogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(CallLogFragment.POSITION, position);
        callLogFragment.setArguments(bundle);

        return callLogFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }

    @Override
    public int getCount() {
        return mPageCounts;
    }

}
