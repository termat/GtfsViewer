package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GFeedInfo {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String feed_publisher_url;
	public String feed_publisher_name;
	public String feed_start_date;
	public String feed_version;
	public String feed_lang;
	public String feed_end_date;
	public String data_id;
}
