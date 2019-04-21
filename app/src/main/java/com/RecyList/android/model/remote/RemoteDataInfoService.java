package com.RecyList.android.model.remote;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;

public interface RemoteDataInfoService {

    //@GET("{xxx}")
    //LiveData<ApiResponse<RemoteBean>> getRemoteInfo(@Path("xxx") double xxx);

    @GET("all?fields=name;callingCodes;nativeName;alpha2Code")
    Single<Response<List<RemoteBean>>> getRemoteInfoAll();
}