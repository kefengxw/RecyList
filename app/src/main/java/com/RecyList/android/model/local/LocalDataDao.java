package com.RecyList.android.model.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.RecyList.android.model.repository.DisplayData;

import java.util.List;

@Dao
public interface LocalDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocalBean it);

    @Query("SELECT * FROM local_table ORDER BY name ASC")
    LiveData<List<DisplayData>> getAllDataFromDb();

    @Query("SELECT * FROM local_table WHERE lower(name) LIKE :input ORDER BY name ASC")
    LiveData<List<DisplayData>> getDataFromDbByName(String input);
}
