package com.navigation_ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Yong on 2017/2/11.
 */

/**
 * 主界面内容部分ViewPager的适配器
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager mFragmentManager;
    private String[] mTitles;
    private List<Fragment> mFragments;

    public MainViewPagerAdapter(FragmentManager fm, String[] mTitles,
                                List<Fragment> mFragments) {
        super(fm);
        mFragmentManager = fm;
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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    //重置所有的Fragment
    public void setFragmentList(List<Fragment> fgList) {

        if (fgList != null) {
            this.mFragments = fgList;
            notifyDataSetChanged();
        }
    }

    //替换指定位置的Fragment
    public void replaceFragment(Fragment fragment, int position) {

        if (fragment != null && position >=0 && position < mFragments.size()) {
            mFragments.set(position, fragment);

            notifyDataSetChanged();
        }
        else {
            //错误
        }
    }
}
