package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GCalendarDao {

    @Query("select * from gcalendar")
    List<GCalendar> findAll();

    @Insert
    void insert(GCalendar a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GCalendar> a);

    @Query("DELETE FROM gcalendar")
    public void deleteAll();

    @Delete
    void delete(List<GCalendar> list);

    @Query("select * from gcalendar where data_id=:data_id")
    List<GCalendar> getDataAtDataId(String data_id);

    @Query("select * from gcalendar where data_id=:data_id and service_id=:servicey_id")
    List<GCalendar> getData(String data_id,String servicey_id);
}
