package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GFareAttribute.class}, version = 1, exportSchema = false)
public abstract class GFareAttributeDB extends RoomDatabase {
    public abstract GFareAttributeDao getDao();
}