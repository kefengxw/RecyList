package com.RecyList.android.model.remote;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;

public class RemoteDataRepository {

    private RemoteDataInfoService mDataInfoService = null;

    public RemoteDataRepository(RemoteDataInfoService remoteService) {
        mDataInfoService = remoteService;
    }

    public Single<Response<List<RemoteBean>>> getRemoteInfoAll() {
        return mDataInfoService.getRemoteInfoAll();
    }
}
