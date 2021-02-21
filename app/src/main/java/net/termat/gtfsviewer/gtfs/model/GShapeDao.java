package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GShapeDao {
    @Query("select * from gshape")
    List<GShape> findAll();

    @Insert
    void insert(GShape a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GShape> a);

    @Query("select * from gshape where shape_id=:shape_id and data_id=:data_id ORDER BY shape_pt_sequence ASC")
    List<GShape> getShape(String data_id,String shape_id);

    @Query("DELETE FROM gshape")
    public void deleteAll();

    @Delete
    void delete(List<GShape> list);

    @Query("select * from gshape where data_id=:data_id")
    List<GShape> getDataAtDataId(String data_id);
}
