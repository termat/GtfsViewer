package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GCalendarDateDao {

    @Query("select * from gcalendardate")
    List<GCalendarDate> findAll();

    @Insert
    void insert(GCalendarDate a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GCalendarDate> a);

    @Query("DELETE FROM gcalendardate")
    public void deleteAll();

    @Delete
    void delete(List<GCalendarDate> list);

    @Query("select * from gcalendardate where data_id=:data_id")
    List<GCalendarDate> getDataAtDataId(String data_id);

}
