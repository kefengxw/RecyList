package com.RecyList.android.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.RecyList.android.model.data.AppExecutors;
import com.RecyList.android.model.data.ExternalDataConfiguration;

import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static volatile Retrofit mInstanceRc = null;

    public static synchronized Retrofit getInstanceRc(AppExecutors appExecutors) {
        if (mInstanceRc == null) {
            mInstanceRc = buildRetrofit(appExecutors);
        }
        return mInstanceRc;
    }

    public static void destroyInstanceRc() {
        mInstanceRc = null;
    }

    //Might many remote service to Expansion, check it later
    public static <T> T createService(Class<T> service, AppExecutors appExecutors) {
        return getInstanceRc(appExecutors).create(service);
    }

    private static Retrofit buildRetrofit(AppExecutors appExecutors) {
        Retrofit.Builder rbuilder = new Retrofit.Builder();
        rbuilder.baseUrl(ExternalDataConfiguration.BASE_URL)
                .client(buildOkHttpClient())
                .callbackExecutor(buildRetrofitExecutor(appExecutors))
                .addCallAdapterFactory(buildCallAdapterFactoryRxJava())
                .addConverterFactory(buildConverterFactory());
        return rbuilder.build();
    }

    private static OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder obuilder = new OkHttpClient.Builder();
        return obuilder.build();
    }

    private static Converter.Factory buildConverterFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);
    }

    private static CallAdapter.Factory buildCallAdapterFactoryLiveData() {
        return new LiveDataCallAdapterFactory();
    }

    private static CallAdapter.Factory buildCallAdapterFactoryRxJava() {
        return RxJava2CallAdapterFactory.create();
    }

    private static Executor buildRetrofitExecutor(AppExecutors appExecutors) {
        return appExecutors.getNetworkIO();
    }
}