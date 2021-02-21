package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GRouteJp {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String route_id;
	public String origin_stop;
	public String destination_stop;
	public String via_stop;
	public String route_update_date;
	public String data_id;
}
