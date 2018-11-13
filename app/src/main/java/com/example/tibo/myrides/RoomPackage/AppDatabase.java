package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Database;


@Database(entities = {CarLocal.class, RitLocal.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CarDao carDao();
    public abstract RitDao ritDao();
}
