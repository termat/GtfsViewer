package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GTransfer {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String from_stop_id;
	public String to_stop_id;
	public String transfer_type;
	public String min_transfer_time;
	public String data_id;
}
