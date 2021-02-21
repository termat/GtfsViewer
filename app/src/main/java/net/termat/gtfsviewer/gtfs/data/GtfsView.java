package net.termat.gtfsviewer.gtfs.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GtfsView {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String data_id;
    public String gtfs_url;
    public String trip_url;
    public String vehicle_url;
}
