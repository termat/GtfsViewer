package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GOfficeJpDao {

    @Query("select * from gofficejp")
    List<GOfficeJp> findAll();

    @Insert
    void insert(GOfficeJp a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GOfficeJp> a);

    @Query("DELETE FROM gofficejp")
    public void deleteAll();

    @Delete
    void delete(List<GOfficeJp> list);

    @Query("select * from gofficejp where data_id=:data_id")
    List<GOfficeJp> getDataAtDataId(String data_id);
}
