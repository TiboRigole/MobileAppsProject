package com.example.tibo.myrides.RoomPackage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.tibo.myrides.Entities.Rit;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RitDao {

    @Query("SELECT * FROM ritlocal")
    List<RitLocal> getAllRitten();

    /**
     * vraagt ritten op die andere mensen met eigenaar z'n auto gereden hebben
     * @param eigenaarAuto
     * @return
     */
    @Query("SELECT * FROM ritlocal WHERE eigenaarAuto== :eigenaarAuto AND uitvoerder != :eigenaarAuto")
    List<RitLocal> getOtherRittenFromExclCarOwner(String eigenaarAuto);

    @Query("SELECT * FROM ritlocal WHERE uitvoerder == :uitvoerder")
    List<RitLocal> getRittenFromUser(String uitvoerder);

    @Insert
    long[] insertRitten(List<RitLocal> ritten);

    @Query("DELETE FROM ritlocal")
    int deleteAllRitten();


}
