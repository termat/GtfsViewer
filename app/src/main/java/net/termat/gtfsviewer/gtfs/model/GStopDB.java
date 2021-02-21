package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GStop.class}, version = 1, exportSchema = false)
public abstract class GStopDB extends RoomDatabase {
    public abstract GStopDao getDao();
}
