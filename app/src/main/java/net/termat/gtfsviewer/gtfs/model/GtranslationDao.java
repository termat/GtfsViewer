package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GtranslationDao {
    @Query("select * from gtranslation")
    List<GTranslation> findAll();

    @Insert
    void insert(GTranslation a);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<GTranslation> a);

    @Query("DELETE FROM gtranslation")
    public void deleteAll();

    @Delete
    void delete(List<GTranslation> list);

    @Query("select * from gtranslation where data_id=:data_id")
    List<GTranslation> getDataAtDataId(String data_id);
}
