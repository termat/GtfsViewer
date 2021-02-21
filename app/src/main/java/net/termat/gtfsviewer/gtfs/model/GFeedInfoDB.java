package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GFeedInfo.class}, version = 1, exportSchema = false)
public abstract class GFeedInfoDB extends RoomDatabase {
    public abstract GFeedInfoDao getDao();
}
