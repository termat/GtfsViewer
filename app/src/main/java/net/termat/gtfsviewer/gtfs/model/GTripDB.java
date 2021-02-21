package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GTrip.class}, version = 1, exportSchema = false)
public abstract class GTripDB extends RoomDatabase {
    public abstract GTripDao getDao();
}