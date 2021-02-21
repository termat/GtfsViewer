package net.termat.gtfsviewer.gtfs.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GtfsView.class}, version = 5, exportSchema = false)
public abstract class GtfsViewDB extends RoomDatabase{
    public abstract GtfsViewDao getDao();
}
