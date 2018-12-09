package com.rebtel.android.model.local;

import android.os.AsyncTask;

public class LocalDataRepository {
    //this is just for expansion from architecture aspect
    private LocalDataDao mLocalDataDao = null;
    //private LiveData<LocalData> mFavorLocation = null;

    public LocalDataRepository(LocalDataDao local) {
        this.mLocalDataDao = local;
        //this.mFavorLocation = mLocalDataDao.getFavorLocationLd();
    }

    public void insert(LocalBean it) {
        if (it != null) {
            LocalBean fLo = it; //new LocalBean(it);
            new InsertLocationAsyncTask(mLocalDataDao).execute(fLo);
        }
    }

/*    public LiveData<LocalData> getFavorLocationLd() {
        return mFavorLocation;
    }*/

    private static class InsertLocationAsyncTask extends AsyncTask<LocalBean, Void, Void> {
        private LocalDataDao localDataDao;

        public InsertLocationAsyncTask(LocalDataDao it) {
            this.localDataDao = it;
        }

        @Override
        protected Void doInBackground(LocalBean... fLos) {
            localDataDao.insert(fLos[0]);
            return null;
        }
    }
}
