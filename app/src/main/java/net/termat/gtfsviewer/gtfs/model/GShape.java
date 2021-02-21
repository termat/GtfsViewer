package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GShape {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public double shape_pt_lon;
	public double shape_pt_lat;
	public String shape_dist_traveled;
	public String shape_id;
	public Integer shape_pt_sequence;
	public String data_id;
}
