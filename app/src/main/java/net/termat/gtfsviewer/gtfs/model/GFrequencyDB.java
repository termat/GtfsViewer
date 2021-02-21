package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GFrequency.class}, version = 1, exportSchema = false)
public abstract class GFrequencyDB extends RoomDatabase {
    public abstract GFrequencyDao getDao();
}