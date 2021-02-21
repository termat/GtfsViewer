package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GShape.class}, version = 1, exportSchema = false)
public abstract class GShapeDB extends RoomDatabase {
    public abstract GShapeDao getDao();
}
