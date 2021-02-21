package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GFareAttribute {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String transfer_duration;
	public String currency_type;
	public double price;
	public String transfers;
	public String fare_id;
	public String payment_method;
	public String data_id;
}
