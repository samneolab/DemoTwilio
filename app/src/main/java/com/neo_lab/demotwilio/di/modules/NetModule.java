package com.neo_lab.demotwilio.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sam_nguyen on 21/04/2017.
 */
@Module
public class NetModule<S> {

    private static final String API_BASE_URL = "https://calling-dev.appspot.com";

    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";

    private Class<S> serviceClass;

    public NetModule(Class<S> serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    @Provides
    @Singleton
    OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient.Builder httpClient, Retrofit.Builder builder) {
        OkHttpClient client =  httpClient.build();;
        return builder.client(client).build();
    }

    @Provides
    @Singleton
    S provideService(OkHttpClient.Builder httpClient, HttpLoggingInterceptor logging,
                     Retrofit.Builder builder, Retrofit retrofit) {

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        retrofit = builder.client(httpClient.build()).build();

        return retrofit.create(serviceClass);

    }







}
