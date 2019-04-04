package com.RecyList.android.model.repository;

import android.arch.persistence.room.ColumnInfo;

public class DisplayData {
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "alpha2Code")
    public String alpha2Code;
    @ColumnInfo(name = "callCode")
    public String callCode;
}
