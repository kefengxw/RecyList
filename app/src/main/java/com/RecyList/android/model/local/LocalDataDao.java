package com.RecyList.android.model.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.RecyList.android.model.repository.DisplayData;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface LocalDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocalBean it);

    @Query("SELECT * FROM local_table ORDER BY name ASC")
    //1.LiveData<List<DisplayData>> getAllDataFromDb();
    Single<List<DisplayData>> getAllDataFromDb();

    @Query("SELECT * FROM local_table WHERE lower(name) LIKE :input ORDER BY name ASC")
    Flowable<List<DisplayData>> getDataFromDbByName(String input);
}
