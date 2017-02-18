package com.navigation_ui.fragment;

/**
 * Created by Yong on 2017/2/17.
 */

/**
 * Fragment使用该接口和Activity实时通信，已确定自身是否需要更新数据。
 */
public interface FragmentUpdateListener {

    boolean isNeedUpdate();

}
