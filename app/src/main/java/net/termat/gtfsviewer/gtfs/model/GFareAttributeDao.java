package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GFareAttributeDao {

    @Query("select * from gfareattribute")
    List<GFareAttribute> findAll();

    @Insert
    void insert(GFareAttribute a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GFareAttribute> a);

    @Query("DELETE FROM gfareattribute")
    public void deleteAll();

    @Delete
    void delete(List<GFareAttribute> list);

    @Query("select * from gfareattribute where data_id=:data_id")
    List<GFareAttribute> getDataAtDataId(String data_id);
}
