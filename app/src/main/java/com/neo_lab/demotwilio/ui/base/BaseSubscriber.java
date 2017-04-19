package com.neo_lab.demotwilio.ui.base;

import android.util.Log;

import com.neo_lab.demotwilio.R;
import com.neo_lab.demotwilio.domain.error.APIError;
import com.neo_lab.demotwilio.domain.utils.DomainUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by sam_nguyen on 12/04/2017.
 */

public abstract class BaseSubscriber<T> extends Subscriber<Response<T>> {

    private String TAG = this.getClass().getName();

    private BaseActivity view;

    protected BaseSubscriber(BaseActivity view) {
        super();
        this.view = view;
    }

    @Override
    public void onCompleted() {

        Log.d(TAG, "Finished");

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        if (e instanceof HttpException) {

            ResponseBody responseBody = ((HttpException)e).response().errorBody();

            view.onUnknownError(getErrorMessage(responseBody));

        } else if (e instanceof SocketTimeoutException) {

            view.onTimeOut(R.string.error_connection_time_out);

        } else if (e instanceof IOException) {

            view.onNetworkError(R.string.error_internet_unavailable);

        } else {

            view.onUnknownError(e.getMessage());
        }

        handleViewOnConnectSeverError();
    }

    @Override
    public void onNext(Response<T> tResponse) {
        if (!tResponse.isSuccessful()) {
            APIError apiError = DomainUtils.parseError(tResponse);
            handleViewOnRequestError(apiError);
        } else {
            handleViewOnRequestSuccess(tResponse.body());
        }

    }

    public abstract void handleViewOnRequestSuccess(T data);

    public abstract void handleViewOnRequestError(APIError apiError);

    public abstract void handleViewOnConnectSeverError();

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }


}
