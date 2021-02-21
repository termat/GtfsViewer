package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GTrip {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String trip_short_name;
	public String trip_id;
	public String direction_id;
	public String route_id;
	public String service_id;
	public String shape_id;
	public String trip_headsign;
	public String block_id;
	public String jp_trip_desc;
	public String wheelchair_accessible ;
	public String bikes_allowed;
	public String jp_trip_desc_symbol;
	public String jp_office_id;
	public String data_id;
}
