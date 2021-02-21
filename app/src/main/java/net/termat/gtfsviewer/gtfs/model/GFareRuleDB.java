package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GFareRule.class}, version = 1, exportSchema = false)
public abstract class GFareRuleDB extends RoomDatabase {
    public abstract GFareRuleDao getDao();
}
