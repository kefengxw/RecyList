package com.RecyList.android.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.RecyList.android.app.HomeApplication;
import com.RecyList.android.model.data.AppExecutors;
import com.RecyList.android.model.local.LocalDataRepository;
import com.RecyList.android.model.remote.Resource;
import com.RecyList.android.model.repository.DataRepository;
import com.RecyList.android.model.repository.DisplayData;

import java.util.List;

import io.reactivex.Flowable;

public class RecyListDataViewModel extends BaseViewModel {

    private AppExecutors mEx = null;
    private LiveData<Resource<List<DisplayData>>> mAllData = null;//Transformations.map, if need
    private MutableLiveData<String> mFilter = new MutableLiveData<>();
    private LiveData<List<DisplayData>> mFilterData = null;
    private LocalDataRepository mLocalRepos = null;
    private DataRepository mRepos = null;
    private int mPrevLength = 0;

    public RecyListDataViewModel(@NonNull Application app) {//can be replace by ViewModel not android view model
        super(app);
        initViewModel(app);
    }

    private void initViewModel(Application app) {

        HomeApplication mInstanceApp = this.getApplication();

        mEx = mInstanceApp.getInstanceEx();
        mRepos = mInstanceApp.getInstanceRepos();
        mLocalRepos = mInstanceApp.getInstanceReposDb();

        initAllData();
        initFilterData();
    }

    private void initAllData() {

        Flowable<Resource<List<DisplayData>>> it = null;
        //LiveData<Resource<List<DisplayData>>>

        it = mRepos.getAllDisplayData();//To be open
        mAllData = LiveDataReactiveStreams.fromPublisher(it);
        //mAllData = mRepos.getAllDisplayData();
    }

    private void initFilterData() {
        mFilterData = Transformations.switchMap(mFilter, it -> {
            return getDataByName(it);
        });
    }

    public LiveData<Resource<List<DisplayData>>> getLiveDataAllDisplayData() {
        return mAllData;
    }

    public LiveData<List<DisplayData>> getDataByFilter() {
        return mFilterData;
    }

    public void setFilter(String input) {

        String it = input.trim();
        int tmp = it.trim().length();

        if ((tmp > 0) || (mPrevLength > 0)) {//only handle valid input, all logical is done by ViewModel
            mFilter.setValue(it);//Improvement 1: To avoid subscribe and unsubscribe each time
        }
        mPrevLength = tmp;
    }

    private LiveData<List<DisplayData>> getDataByName(String input) {
        //all the logical is done by ViewModel
        //return mLocalRepos.getDataByName(input.toLowerCase() + "%");
        Flowable<List<DisplayData>> it = null;

        it = mLocalRepos.getDataByName(input.toLowerCase() + "%");
        return LiveDataReactiveStreams.fromPublisher(it);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}