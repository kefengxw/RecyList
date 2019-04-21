package com.RecyList.android.model.local;

import android.os.AsyncTask;

import com.RecyList.android.model.data.AppExecutors;
import com.RecyList.android.model.repository.DisplayData;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalDataRepository {
    //this is just for expansion from architecture aspect
    private LocalDataDao mLocalDataDao = null;
    private AppExecutors mEx = null;

    public LocalDataRepository(LocalDataDao localDao, AppExecutors appExecutors) {
        this.mLocalDataDao = localDao;
        this.mEx = appExecutors;
    }

    public void insert(LocalBean it) {
        if (it != null) {
            //LocalBean fLo = it; //new LocalBean(it);
            //new InsertLocationAsyncTask(mLocalDataDao).execute(fLo);
            mEx.runOnDiskIO(() -> mLocalDataDao.insert(it));
        }
    }

    public void insert(List<LocalBean> list) {
        if (list != null && list.size() != 0) {
            mEx.runOnDiskIO(() -> {
                for (LocalBean it : list) {
                    mLocalDataDao.insert(it);
                }
            });
        }
    }

    public Flowable<List<DisplayData>> getDataByName(String input) {
        return mLocalDataDao.getDataFromDbByName(input);
    }

    //2.public LiveData<List<DisplayData>> getAllDataFromDb() {
    public Single<List<DisplayData>> getAllDataFromDb() {
        return mLocalDataDao.getAllDataFromDb();
    }

    private static class InsertLocationAsyncTask extends AsyncTask<LocalBean, Void, Void> {

        private final LocalDataDao mDao;

        public InsertLocationAsyncTask(LocalDataDao it) {
            this.mDao = it;
        }

        @Override
        protected Void doInBackground(LocalBean... fLos) {
            mDao.insert(fLos[0]);
            return null;
        }
    }
}
