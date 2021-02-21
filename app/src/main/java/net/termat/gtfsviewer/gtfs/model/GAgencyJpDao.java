package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GAgencyJpDao {

    @Query("select * from gagencyjp")
    List<GAgencyJp> findAll();

    @Insert
    void insert(GAgencyJp a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GAgencyJp> a);

    @Query("DELETE FROM gagencyjp")
    void deleteAll();

    @Delete
    void delete(List<GAgencyJp> list);

    @Query("select * from gagencyjp where data_id=:data_id")
    List<GAgencyJp> getDataAtDataId(String data_id);
}
