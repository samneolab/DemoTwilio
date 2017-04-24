package com.neo_lab.demotwilio.ui.video_calling_room;

import android.util.Log;

import com.neo_lab.demotwilio.domain.error.APIError;
import com.neo_lab.demotwilio.domain.generator.ServiceGenerator;
import com.neo_lab.demotwilio.domain.response.TokenServer;
import com.neo_lab.demotwilio.domain.services.TokenService;
import com.neo_lab.demotwilio.ui.base.BaseSubscriber;
import com.neo_lab.demotwilio.ui.video_calling_room.domain.usecase.GetToken;
import com.neo_lab.demotwilio.use_case.UseCase;
import com.neo_lab.demotwilio.use_case.UseCaseHandler;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sam_nguyen on 18/04/2017.
 */

public class VideoCallingRoomPresenter implements VideoCallingRoomContract.Presenter {

    private static final String TAG = VideoCallingRoomPresenter.class.getName();

    private VideoCallingRoomContract.View view;

    private final UseCaseHandler useCaseHandler;

    private final GetToken getToken;

    public VideoCallingRoomPresenter(UseCaseHandler useCaseHandler, GetToken getToken) {
        this.useCaseHandler = useCaseHandler;
        this.getToken = getToken;
    }


    @Override
    public void requestTokenCallingVideo(String deviceId, String userName) {

        GetToken.RequestValues requestValues = new GetToken.RequestValues(deviceId, userName);

        useCaseHandler.execute(getToken, requestValues, new UseCase.UseCaseCallback<GetToken.ResponseValue>() {
            @Override
            public void onSuccess(GetToken.ResponseValue response) {
                view.onListenerRequestVideoToken(true, "Connected Successfully", response.getToken());
            }

            @Override
            public void onError() {
                Log.e(TAG, "requestTokenCallingVideo");
            }
        });
    }

    @Override
    public void attachView(VideoCallingRoomContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {

    }
}
