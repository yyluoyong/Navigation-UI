package com.navigation_ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Yong on 2017/2/11.
 */

/**
 * 主界面内容部分ViewPager的适配器
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;
    private List<Fragment> mFragments;

    public MainViewPagerAdapter(FragmentManager fm, String[] mTitles,
                                List<Fragment> mFragments) {
        super(fm);
        this.mTitles = mTitles;
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
