package com.neo_lab.demotwilio.ui.video_calling_room;

import com.neo_lab.demotwilio.model.Token;
import com.neo_lab.demotwilio.ui.base.BasePresenter;
import com.neo_lab.demotwilio.ui.base.BaseView;
import com.neo_lab.demotwilio.ui.base_mvp.BaseMvpPresenter;
import com.neo_lab.demotwilio.ui.base_mvp.BaseMvpView;

/**
 * Created by sam_nguyen on 18/04/2017.
 */

public interface VideoCallingRoomContract {

    interface View extends BaseMvpView {

        void getLocalProperties();

        void showUI();

        boolean isPermissionsGranted();

        void requestPermissions();

        void initializeCallingVideoRoom();

        void onListenerRequestVideoToken(boolean status, String message, Token token);

        void updateStatusRequestVideoToken(boolean status, String message);

    }

    interface Presenter extends BaseMvpPresenter<View> {

        void requestTokenCallingVideo(String deviceId, String userName);

    }
}
