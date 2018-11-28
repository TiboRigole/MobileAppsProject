package com.example.tibo.myrides.RoomPackage;

import android.app.Application;
import android.arch.persistence.room.Room;

public class AnApplication extends Application {

    private AppDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase= Room.databaseBuilder(this, AppDatabase.class, "ritten.db").build();
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }


}
