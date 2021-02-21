package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GCalendarDate {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String service_id;
	public String date;
	public String exception_type;
	public String data_id;
}
