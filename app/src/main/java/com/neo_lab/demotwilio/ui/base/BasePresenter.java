package com.neo_lab.demotwilio.ui.base;

/**
 * Created by sam_nguyen on 19/04/2017.
 */

public interface BasePresenter<V> {

    void attachView(V view);

    void detachView();

}
