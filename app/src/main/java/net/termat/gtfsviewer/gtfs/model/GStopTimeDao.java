package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GStopTimeDao {
    @Query("select * from gstoptime")
    List<GStopTime> findAll();

    @Insert
    void insert(GStopTime a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GStopTime> a);

    @Query("DELETE FROM gstoptime")
    public void deleteAll();

    @Query("select * from gstoptime where trip_id=:trip_id")
    List<GStopTime> getTimesAtTrip(String trip_id);

    @Query("SELECT * from gstoptime WHERE stop_id IN (:stop_ids)")
    public List<GStopTime> getTimeAdStops(List<String> stop_ids);

    @Delete
    void delete(List<GStopTime> list);

    @Query("select * from gstoptime where data_id=:data_id")
    List<GStopTime> getDataAtDataId(String data_id);

    @Query("select * from gstoptime where data_id=:data_id and stop_id=:stop_id")
    List<GStopTime> getDataAtStop(String data_id,String stop_id);

    @Query("select * from gstoptime where (data_id=:data_id and trip_id=:trip_id) ORDER BY stop_sequence ASC")
    List<GStopTime> getDataAtTrip(String data_id,String trip_id);
}
