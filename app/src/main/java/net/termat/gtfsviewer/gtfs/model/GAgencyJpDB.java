package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GAgencyJp.class}, version = 1, exportSchema = false)
public abstract class GAgencyJpDB extends RoomDatabase {
    public abstract GAgencyJpDao getDao();
}