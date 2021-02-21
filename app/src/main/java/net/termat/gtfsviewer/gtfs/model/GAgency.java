package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GAgency {
	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String agency_timezone;
	public String agency_name;
	public String agency_url;
	public String agency_fare_url;
	public String agency_phone;
	public String agency_id;
	public String agency_lang;
	public String agency_email;
	public String data_id;
}
