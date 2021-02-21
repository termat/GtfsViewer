package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GAgency.class}, version = 2, exportSchema = false)
public abstract class GAgencyDB extends RoomDatabase {
    public abstract GAgencyDao getDao();
}
