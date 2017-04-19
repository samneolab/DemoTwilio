package com.neo_lab.demotwilio.ui.create_new_room;

import android.util.Log;

import com.neo_lab.demotwilio.utils.generate.GenerateUtils;

/**
 * Created by sam_nguyen on 10/04/2017.
 */

public class CreateNewRoomPresenter implements CreateNewRoomContract.Presenter {

    private static int SIZE_OF_GENERATE_ROOM_NUMBER = 4;

    private static final String TAG = CreateNewRoomPresenter.class.getName();

    private CreateNewRoomContract.View view;

    public CreateNewRoomPresenter() {

    }

    @Override
    public String generateRoomNumber() {
        return GenerateUtils.getRandomString(SIZE_OF_GENERATE_ROOM_NUMBER);
    }

    @Override
    public void attachView(CreateNewRoomContract.View view) {

        this.view = view;

    }

    @Override
    public void detachView() {

    }
}
