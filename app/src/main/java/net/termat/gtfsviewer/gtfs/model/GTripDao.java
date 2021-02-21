package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GTripDao {
    @Query("select * from gtrip")
    List<GTrip> findAll();

    @Insert
    void insert(GTrip a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GTrip> a);

    @Query("select * from gtrip where trip_id=:trip_id and data_id=:data_id")
    List<GTrip> getTrip(String data_id,String trip_id);

    @Query("DELETE FROM gtrip")
    public void deleteAll();

    @Delete
    void delete(List<GTrip> list);

    @Query("select * from gtrip where data_id=:data_id")
    List<GTrip> getDataAtDataId(String data_id);
}
