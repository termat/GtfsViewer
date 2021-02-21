package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GFareRule {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String route_id;
	public String destination_id;
	public String origin_id;
	public String fare_id;
	public String contains_id;
	public String data_id;
}
