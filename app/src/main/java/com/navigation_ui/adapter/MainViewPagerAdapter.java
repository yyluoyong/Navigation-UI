package com.navigation_ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.navigation_ui.R;
import com.navigation_ui.fragment.CallLogFragment;
import com.navigation_ui.fragment.FragmentUpdatable;
import com.navigation_ui.tools.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yong on 2017/2/11.
 */

/**
 * 主界面内容部分ViewPager的适配器
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "PagerAdapter";

    private FragmentManager mFragmentManager;

    //用于保存每个Fragment对应的Tag值
    private Map<Integer, String> mFragmentTags;
    private Context context;

    //页面数量
    private static int mPageCounts;
    private String[] mPageTitles;

    //页面是否需要更新，默认不需要更新
    private boolean[] isPagesNeedUpdate;

    public MainViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.mFragmentManager = fm;
        this.context = context;

        init();
    }

    private void init() {
        mFragmentTags = new HashMap<Integer, String>();

        mPageCounts = context.getResources().getInteger(R.integer.MAIN_PAGE_COUNTS);
        mPageTitles = context.getResources().getStringArray(R.array.MAIN_PAGE_TITLES);

        isPagesNeedUpdate = new boolean[mPageCounts];
        for (int i = 0; i < mPageCounts; i++) {
            isPagesNeedUpdate[i] = false;
        }
    }

    /**
     * 创建一个新的Fragment实例的时候调用
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {

        return new CallLogFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }

    @Override
    public int getCount() {
        return mPageCounts;
    }

    /**
     * 实现更新该Fragment
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {

        LogUtil.d("Adapter", "getItemPosition");

        int position = super.getItemPosition(object);

        if (object != null) {
//            if (((CallLogFragment) object).isNeedUpdate()) {
//                ((CallLogFragment) object).update();
//                isPagesNeedUpdate[position] = false;
//            }
            ((CallLogFragment) object).update();
        }

        return position;
    }


    public void updateFragment(int position) {

        LogUtil.d("Adapter", "updateFragment - " + position);

        Fragment fragment = getFragment(position);

        if (fragment != null) {
            isPagesNeedUpdate[position] = true;

        } else {
            //该位置Fragment需要更新，由于Fragment尚未创建等原因，在这里并不能完成更新，因此先标记。
            //比如，当前位置为0时，位置3的Fragment并没有创建，若此时使用该函数更新位置3的Fragment，
            //则并不会生效。此处标记是为了配合instantiateItem函数使用。
            isPagesNeedUpdate[position] = true;
        }


        notifyDataSetChanged();
    }


    /**
     * 得到每个Fragment的Tag，将其存储到HashMap中，以便于根据Tag从FragmentManager中获得
     * 对应的Fragment。其余功能集成父类。
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);

        if (object != null) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);

            if (isPagesNeedUpdate[position]) {
                ((CallLogFragment) fragment).setNeedUpdate(true);
                isPagesNeedUpdate[position] = false;
            }
        }

        return object;
    }

    private Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);

        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }

        return fragment;
    }
}
