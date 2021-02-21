package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GTranslation.class}, version = 1, exportSchema = false)
public abstract class GTranslationDB extends RoomDatabase {
    public abstract GtranslationDao getDao();
}
