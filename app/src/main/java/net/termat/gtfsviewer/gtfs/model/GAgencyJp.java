package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GAgencyJp {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String agency_official_name;
	public String agency_president_pos;
	public String agency_id;
	public String agency_zip_number;
	public String agency_address;
	public String agency_president_name;
	public String data_id;
}
