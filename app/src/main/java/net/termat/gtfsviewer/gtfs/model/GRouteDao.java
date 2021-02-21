package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GRouteDao {
    @Query("select * from groute")
    List<GRoute> findAll();

    @Insert
    void insert(GRoute a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GRoute> a);

    @Query("select * from groute where route_id=:route_id")
    List<GRoute> getRoute(String route_id);

    @Query("DELETE FROM groute")
    public void deleteAll();

    @Delete
    void delete(List<GRoute> list);

    @Query("select * from groute where data_id=:data_id")
    List<GRoute> getDataAtDataId(String data_id);
}
