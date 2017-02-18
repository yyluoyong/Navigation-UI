package com.navigation_ui.fragment;

import java.util.Observable;

/**
 * Created by Yong on 2017/2/17.
 */

public class UpdateDataObservable extends Observable {

    //单例
    private static UpdateDataObservable instance = null;

    public static UpdateDataObservable getInstance() {

        if (null == instance) {
            instance = new UpdateDataObservable();
        }

        return instance;
    }

    //通知观察者更新数据
    public void notifyUpdate(boolean updateFlag) {

        //关键方法，必须写，具体实现可以查看源码
        setChanged();//设置changeFlag
        notifyObservers(updateFlag);//通知观察者
    }
}
