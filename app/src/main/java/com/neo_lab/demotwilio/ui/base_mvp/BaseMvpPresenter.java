package com.neo_lab.demotwilio.ui.base_mvp;

/**
 * Created by sam_nguyen on 19/04/2017.
 */

public interface BaseMvpPresenter<V> {

    void attachView(V view);

    void detachView();

}
