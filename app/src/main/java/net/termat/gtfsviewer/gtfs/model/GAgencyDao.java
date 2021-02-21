package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GAgencyDao {

    @Query("select * from gagency")
    List<GAgency> findAll();

    @Query("select * from gagency where agency_id=:agency_id")
    List<GAgency> getAgency(String agency_id);

    @Insert
    void insert(GAgency a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GAgency> a);

    @Query("DELETE FROM gagency")
    public void deleteAll();

    @Delete
    public void delete(GAgency a);

    @Query("select * from gagency where data_id=:data_id")
    List<GAgency> getDataAtDataId(String data_id);
}
