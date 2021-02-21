package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GRouteJpDao {
    @Query("select * from groutejp")
    List<GRouteJp> findAll();

    @Insert
    void insert(GRouteJp a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GRouteJp> a);

    @Query("DELETE FROM groutejp")
    public void deleteAll();

    @Delete
    void delete(List<GRouteJp> list);

    @Query("select * from groutejp where data_id=:data_id")
    List<GRouteJp> getDataAtDataId(String data_id);
}
