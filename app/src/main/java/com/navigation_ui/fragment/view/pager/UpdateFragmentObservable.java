package com.navigation_ui.fragment.view.pager;

import java.util.Observable;

/**
 * Created by Yong on 2017/2/17.
 */

/**
 *用于通知观察者，采用单例模式。
 */
public class UpdateFragmentObservable extends Observable {

    private static UpdateFragmentObservable instance = null;

    public static UpdateFragmentObservable getInstance() {

        if (null == instance) {
            instance = new UpdateFragmentObservable();
        }

        return instance;
    }

    public void notifyFragmentUpdate() {
        setChanged();
        notifyObservers();
    }
}
