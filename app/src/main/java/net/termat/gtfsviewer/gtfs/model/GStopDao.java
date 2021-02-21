package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GStopDao {
    @Query("select * from gstop")
    List<GStop> findAll();

    @Query("select * from gstop where stop_lat <=:latN and stop_lat >= :latS and stop_lon <= :lonE and stop_lon >= :lonW")
    List<GStop> getStopByLonlat(double latN, double latS, double lonE, double lonW);

    @Query("select * from gstop where stop_id=:stop_id")
    List<GStop> getStop(String stop_id);

    @Query("select * from gstop where stop_lon=:lon and stop_lat=:lat")
    List<GStop> getStop(double lon, double lat);

    @Query("select * from gstop where stop_name like :stop_name")
    List<GStop> getStopByName(String stop_name);

    @Insert
    void insert(GStop a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GStop> a);

    @Query("DELETE FROM gstop")
    public void deleteAll();

    @Delete
    void delete(List<GStop> list);

    @Query("select * from gstop where data_id=:data_id")
    List<GStop> getDataAtDataId(String data_id);

    @Query("select * from gstop where data_id=:data_id and stop_id=:stop_id")
    List<GStop> getStop(String data_id,String stop_id);
}
