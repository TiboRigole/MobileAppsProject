package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;


@Database(entities = {RitLocal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RitDao ritDao();
}
