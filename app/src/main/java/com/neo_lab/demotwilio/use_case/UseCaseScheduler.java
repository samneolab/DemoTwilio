package com.neo_lab.demotwilio.use_case;

/**
 * Created by sam_nguyen on 24/04/2017.
 */

public interface UseCaseScheduler {

    void execute(Runnable runnable);

    <V extends UseCase.ResponseValue> void notifyResponse(final V response, final UseCase.UseCaseCallback<V> useCaseCallback);

    <V extends UseCase.ResponseValue> void onError(final UseCase.UseCaseCallback<V> useCaseCallback);

}
