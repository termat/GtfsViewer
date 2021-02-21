package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GRoute {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String route_id;
	public String route_long_name;
	public String route_text_color;
	public String route_desc;
	public String route_type;
	public String agency_id;
	public String route_short_name;
	public String route_url;
	public String route_color;
	public String p_parent_route_id;
	public String data_id;
}
