package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GFeedInfoDao {

    @Query("select * from gfeedinfo")
    List<GFeedInfo> findAll();

    @Insert
    void insert(GFeedInfo a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GFeedInfo> a);

    @Query("DELETE FROM gfeedinfo")
    public void deleteAll();

    @Delete
    void delete(List<GFeedInfo> list);

    @Query("select * from gfeedinfo where data_id=:data_id")
    List<GFeedInfo> getDataAtDataId(String data_id);
}
