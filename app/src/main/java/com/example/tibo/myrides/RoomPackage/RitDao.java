package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.tibo.myrides.Entities.Rit;

import java.util.List;

@Dao
public interface RitDao {

    @Query("SELECT * FROM ritlocal")
    List<RitLocal> getAllUnCommittedRitten();

    @Insert
    long insertRit(RitLocal ritLocal);

}
