package net.termat.gtfsviewer.gtfs.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import net.termat.gtfsviewer.gtfs.model.GRoute;

import java.util.List;

@Dao
public interface GtfsViewDao {

    @Query("select * from GtfsView")
    List<GtfsView> findAll();

    @Insert
    void insert(GtfsView a);

    @Delete
    void delete(GtfsView a);

    @Update
    void update(GtfsView a);

    @Query("select * from GtfsView where data_id=:data_id")
    List<GtfsView> getGtfsView(String data_id);
}
