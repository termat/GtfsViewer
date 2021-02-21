package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GRouteJp.class}, version = 1, exportSchema = false)
public abstract class GRouteJpDB extends RoomDatabase {
    public abstract GRouteJpDao getDao();
}
