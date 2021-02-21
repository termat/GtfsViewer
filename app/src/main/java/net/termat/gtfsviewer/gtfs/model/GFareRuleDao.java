package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GFareRuleDao {
    @Query("select * from gfarerule")
    List<GFareRule> findAll();

    @Insert
    void insert(GFareRule a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GFareRule> a);

    @Query("DELETE FROM gfarerule")
    public void deleteAll();

    @Delete
    void delete(List<GFareRule> list);

    @Query("select * from gfarerule where data_id=:data_id")
    List<GFareRule> getDataAtDataId(String data_id);
}
