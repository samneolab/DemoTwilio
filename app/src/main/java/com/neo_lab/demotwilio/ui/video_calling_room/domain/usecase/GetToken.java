package com.neo_lab.demotwilio.ui.video_calling_room.domain.usecase;

import com.neo_lab.demotwilio.domain.error.APIError;
import com.neo_lab.demotwilio.domain.generator.ServiceGenerator;
import com.neo_lab.demotwilio.domain.response.TokenServer;
import com.neo_lab.demotwilio.domain.services.TokenService;
import com.neo_lab.demotwilio.ui.base.BaseActivity;
import com.neo_lab.demotwilio.ui.base.BaseSubscriber;
import com.neo_lab.demotwilio.ui.video_calling_room.domain.model.Token;
import com.neo_lab.demotwilio.use_case.UseCase;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sam_nguyen on 24/04/2017.
 */

public class GetToken extends UseCase<GetToken.RequestValues, GetToken.ResponseValue>{

    private TokenService service;

    private CompositeSubscription subscriptions;

    private final BaseActivity view;

    public GetToken(BaseActivity view) {
        this.service = ServiceGenerator.createService(TokenService.class);
        this.subscriptions = new CompositeSubscription();
        this.view = view;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {



        Observable<Response<TokenServer>> observable =
                service.getTokenVideo(requestValues.deviceId, requestValues.userName);

        subscriptions.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TokenServer>(view) {
                    @Override
                    public void handleViewOnRequestSuccess(TokenServer data) {

                        TokenServer tokenServer = data;

                        Token token = new Token(tokenServer.getIdentity(), tokenServer.getToken());

                        getUseCaseCallback().onSuccess(new ResponseValue(token));

                        subscriptions.clear();

                    }

                    @Override
                    public void handleViewOnRequestError(APIError apiError) {
                        getUseCaseCallback().onError();
                    }

                    @Override
                    public void handleViewOnConnectSeverError() {

                    }
                })
        );

    }



    public static final class RequestValues implements UseCase.RequestValues {

        private String deviceId;
        private String userName;

        public RequestValues(String deviceId, String userName) {
            this.deviceId = deviceId;
            this.userName = userName;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }


    public static final class ResponseValue implements UseCase.ResponseValue {
        private Token token;

        public ResponseValue(Token token) {
            this.token = token;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }
    }

}
