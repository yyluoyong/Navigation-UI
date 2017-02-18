package com.navigation_ui.fragment;

/**
 * Created by Yong on 2017/2/15.
 */

/**
 * Fragment更新自身数据的接口
 */
public interface FragmentUpdatable {

    boolean isNeedUpdate();

    void updateData();
}
