package com.navigation_ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.navigation_ui.tools.LogUtil;

import java.util.List;

/**
 * Created by Yong on 2017/2/11.
 */

/**
 * 主界面内容部分ViewPager的适配器
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

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

        Fragment fragment = (Fragment)super.instantiateItem(container, position);

        String fragmentTag = fragment.getTag();

        if (mFragmentsUpdateFlag[position]) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            transaction.remove(fragment);
            fragment = mFragments.get(position);
            transaction.add(container.getId(), fragment, fragmentTag);
            transaction.attach(fragment);

            /**
             * 不同于FragmentStatePagerAdapter，这里似乎不能用replace函数。
             */
//            Fragment fragment = mFragments.get(position);
//            ft.replace(container.getId(), fragment);

            transaction.commit();

            mFragmentsUpdateFlag[position] = false; //标记回位
        }

        LogUtil.d("PagerAdapter", "位置：" + position);

        return fragment;
    }

    //重置所有的Fragment
    public void setFragmentList(List<Fragment> fgList) {

        if (fgList != null) {
            mFragments = fgList;

            for (int i = 0; i < mFragments.size(); i++) {
                mFragmentsUpdateFlag[i] = true;
            }

            notifyDataSetChanged();
        }
        else {
            //undo:错误
        }
    }

    //替换指定位置的Fragment
    public void replaceFragment(Fragment fragment, int position) {

        if (fragment != null && position >=0 && position < mFragments.size()) {
            mFragments.set(position, fragment);
            mFragmentsUpdateFlag[position] = true;
            notifyDataSetChanged();
        }
        else {
            //undo:错误
        }
    }
}
