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

    //用于动态更新Fragment列表的时候，决定是否更新对应位置的Fragment
    private boolean[] mFragmentsUpdateFlag;

    public MainViewPagerAdapter(FragmentManager fm, String[] mTitles,
                                List<Fragment> mFragments) {
        super(fm);
        mFragmentManager = fm;
        this.mTitles = mTitles;
        this.mFragments = mFragments;

        //默认每个位置都不更新
        mFragmentsUpdateFlag = new boolean[this.mFragments.size()];
        for (int i = 0; i < this.mFragments.size(); i++) {
            mFragmentsUpdateFlag[i] = false;
        }
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

    /**
     * 实现动态更新Fragment
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragmentsUpdateFlag[i]) {
                fragment = mFragments.get(i);
                transaction.replace(container.getId(), fragment);

                mFragmentsUpdateFlag[i] = false;
            }
        }
        transaction.commit();

        /**
         * 由于预加载的缘故，如果写成如下的形式，不能实现预想的动态更新。例如：
         * ViewPager包含4个页面，即FragmentList长度为4，若想更新0位置的Fragment，如果当前
         * position=0，则会如期更新；若position /= 0，即视图在比如第3个Fragment上，则该函
         * 数被调用的时候，更新的是第3个Fragment，当视图回到第1个视图的时候，由于这个时候不会
         * 调用notifyDataSetChanged()方法，因此instantiateItem方法不再被调用，因而第一个
         * 视图不会被更新。
         */
//        fragment = mFragments.get(position);
//        transaction.replace(container.getId(), fragment);
//        transaction.commit();

        return mFragments.get(position);
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
