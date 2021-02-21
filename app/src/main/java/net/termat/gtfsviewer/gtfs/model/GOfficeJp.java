package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GOfficeJp {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String office_id;
	public String office_name;
	public String office_url;
	public String office_phone;
	public String data_id;
}
