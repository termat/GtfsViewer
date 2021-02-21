package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GTransferDao {
    @Query("select * from gtransfer")
    List<GTransfer> findAll();

    @Insert
    void insert(GTransfer a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GTransfer> a);

    @Query("DELETE FROM gtransfer")
    public void deleteAll();

    @Delete
    void delete(List<GTransfer> list);

    @Query("select * from gtransfer where data_id=:data_id")
    List<GTransfer> getDataAtDataId(String data_id);
}
