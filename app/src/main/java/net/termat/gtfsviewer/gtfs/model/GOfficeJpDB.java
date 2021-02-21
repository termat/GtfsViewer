package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GOfficeJp.class}, version = 1, exportSchema = false)
public abstract class GOfficeJpDB extends RoomDatabase {
    public abstract GOfficeJpDao getDao();
}
