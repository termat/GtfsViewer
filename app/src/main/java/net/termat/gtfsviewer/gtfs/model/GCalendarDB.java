package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GCalendar.class}, version = 1, exportSchema = false)
public abstract class GCalendarDB extends RoomDatabase {
    public abstract GCalendarDao getDao();
}
