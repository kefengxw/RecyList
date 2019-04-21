package com.RecyList.android.model.repository;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.RecyList.android.model.data.AppExecutors;
import com.RecyList.android.model.remote.Resource;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors mEx;//database, network, UI, 3 threads
    private PublishSubject<Resource<ResultType>> mResult = PublishSubject.create();

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {

        this.mEx = appExecutors;
        init();
    }

    private void init() {
        //all logical control here, local first, remote first, or fetch every time
        mResult.onNext(Resource.loading(null));
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        loadFromDb()
                .subscribeOn(mEx.asRxSchedulerDiskIO())
                .observeOn(mEx.asRxSchedulerMainThread())
                .subscribe(new SingleObserver<ResultType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(ResultType it) {
                        if (shouldFetchRemote(it)) {
                            //mResult.onNext(Resource.loading(null));
                            fetchFromNetwork();
                        } else {
                            mResult.onNext(Resource.success(it));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResult.onNext(Resource.error(null, e.getMessage()));
                    }
                });
    }

    private void fetchFromNetwork() {
        //Single<ApiResponse<RequestType>> apiResponseSingle =
        createNetworkCall()
                .subscribeOn(mEx.asRxSchedulerNetwork())
                .observeOn(mEx.asRxSchedulerMainThread())
                .subscribe(new SingleObserver<Response<RequestType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Response<RequestType> response) {
                        if (response.isSuccessful()) {
                            saveCallResultToDb(processResponse(response));
                            loadDataFromDb();
                        } else {
                            mResult.onNext(Resource.error(null, response.message()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResult.onNext(Resource.error(null, e.getMessage()));
                        onFetchFailed();
                    }
                });
    }

    @WorkerThread
    private RequestType processResponse(Response<RequestType> response) {
        //return response.getBody();
        return response.body();
    }

    @NonNull
    @MainThread // Called to get the cached data from the database.
    protected abstract Single<ResultType> loadFromDb();

    @MainThread
    // Called with the data in the database to decide whether to fetch potentially updated data from the network.
    protected abstract boolean shouldFetchRemote(@Nullable ResultType data);

    @WorkerThread //db thread // Called to save the result of the API response into the database.
    protected abstract void saveCallResultToDb(@NonNull RequestType data);

    @NonNull
    @MainThread // Called to create the API call.
    protected abstract Single<Response<RequestType>> createNetworkCall();

    @MainThread
    // Called when the fetch fails. The child class may want to reset components like rate limiter.
    protected void onFetchFailed() {
        /*Log*/
    }

    // Returns a LiveData object that represents the resource that's implemented in the base class.
    public Flowable<Resource<ResultType>> getAsFlowable() {
        return mResult.toFlowable(BackpressureStrategy.BUFFER);
                //.startWith(Resource.loading(null));
    }
}