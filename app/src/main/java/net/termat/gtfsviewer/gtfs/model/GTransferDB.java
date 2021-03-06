package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GTransfer.class}, version = 1, exportSchema = false)
public abstract class GTransferDB extends RoomDatabase {
    public abstract GTransferDao getDao();
}
