package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GFrequency {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String trip_id;
	public String start_time;
	public String end_time;
	public String headway_secs;
	public String exact_times;
	public String data_id;
}
