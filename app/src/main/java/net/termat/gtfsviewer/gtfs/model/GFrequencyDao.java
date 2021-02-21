package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GFrequencyDao {

    @Query("select * from gfrequency")
    List<GFrequency> findAll();

    @Insert
    void insert(GFrequency a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GFrequency> a);

    @Query("DELETE FROM gfrequency")
    public void deleteAll();

    @Delete
    void delete(List<GFrequency> list);

    @Query("select * from gfrequency where data_id=:data_id")
    List<GFrequency> getDataAtDataId(String data_id);
}
