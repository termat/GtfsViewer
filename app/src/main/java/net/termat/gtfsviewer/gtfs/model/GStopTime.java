package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GStopTime{

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String trip_id;
	public String drop_off_type;
	public String arrival_time;
	public Integer stop_sequence;
	public String shape_dist_traveled;
	public String pickup_type;
	public String stop_id;
	public String departure_time;
	public String stop_headsign;
	public String timepoint;
	public String data_id;

	public static Long getTimeVal(String time){
		String[] s=time.split(":");
		if(s[0].startsWith("0"))s[0]=s[0].substring(1);
		if(s[1].startsWith("0"))s[1]=s[1].substring(1);
		if(s[2].startsWith("0"))s[2]=s[2].substring(1);
		long hh=Long.parseLong(s[0]);
		long mm=Long.parseLong(s[1]);
		long ss=Long.parseLong(s[2]);
		return hh*3600000+mm*60*1000+ss*1000;
	}
}
