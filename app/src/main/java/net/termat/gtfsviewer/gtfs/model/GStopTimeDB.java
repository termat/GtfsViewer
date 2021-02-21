package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GStopTime.class}, version = 2, exportSchema = false)
public abstract class GStopTimeDB extends RoomDatabase {
    public abstract GStopTimeDao getDao();
}
