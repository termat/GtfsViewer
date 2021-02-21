package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GCalendar {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String start_date;
	public String end_date;
	public String sunday;
	public String monday;
	public String tuesday;
	public String wednesday;
	public String thursday;
	public String friday;
	public String saturday;
	public String service_id;
	public String data_id;
}
