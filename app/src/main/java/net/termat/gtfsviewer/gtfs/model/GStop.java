package net.termat.gtfsviewer.gtfs.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class GStop {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String zone_id;
	public String stop_id;
	public double stop_lon;
	public double stop_lat;
	public String stop_code;
	public String stop_url;
	public String stop_desc;
	public String stop_timezone;
	public String stop_name;
	public String location_type;
	public String parent_station;
	public String wheelchair_boarding;
	public String platform_code;
	public String data_id;

	public static GStop create(Map<String,Object> map){
		GStop ret=new GStop();
		ret.zone_id=(String)map.get("zone_id");
		ret.stop_id=(String)map.get("stop_id");
		ret.stop_lon=(Double)map.get("stop_lon");
		ret.stop_lat=(Double)map.get("stop_lat");
		ret.stop_code=(String)map.get("stop_code");
		ret.stop_url=(String)map.get("stop_url");
		ret.stop_desc=(String)map.get("stop_desc");
		ret.stop_timezone=(String)map.get("stop_timezone");
		ret.stop_name=(String)map.get("stop_name");
		ret.location_type=(String)map.get("location_type");
		ret.parent_station=(String)map.get("parent_station");
		ret.wheelchair_boarding=(String)map.get("wheelchair_boarding");
		ret.platform_code=(String)map.get("platform_code");
		return ret;
	}
}
