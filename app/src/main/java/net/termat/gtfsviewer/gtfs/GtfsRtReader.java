package net.termat.gtfsviewer.gtfs;

import android.util.Log;

import com.google.gson.Gson;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.Position;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import net.termat.gtfsviewer.gtfs.data.GtfsView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GtfsRtReader {
    private String name;
    private URL tripUrl;
    private URL vehicleUrl;
    private Gson gson;

    public GtfsRtReader(String name, URL trip, URL vehicle){
        this.name=name;
        tripUrl =trip;
        vehicleUrl =vehicle;
        gson=new Gson();
    }

    public GtfsRtReader(GtfsView gr) throws MalformedURLException {
        this(gr.name,new URL(gr.trip_url),new URL(gr.vehicle_url));
    }

    public Map<String, Object> getRtMap() throws IOException {
        Map<String, Object> ret=new HashMap<>();
        ret.put("trip", getTirpUpdate(tripUrl));
        ret.put("vehicle", getVehiclePosition(vehicleUrl));
        return ret;
    }

    public static Map<String, Object> getTripStopData(List<Map<String, Object>> list,String trip_id,String stop_id){
        Map<String, Object> o=getTripData(list,trip_id);
        if(o==null)return null;
        List<Map<String, Object>> stops=(List<Map<String, Object>>)o.get("stops");
        if(stop_id==null||stop_id.isEmpty()){
            if(stops.size()>0)return stops.get(0);
        }else{
            for(Map<String, Object> m : stops){
                if(m.get("stop_id").equals(stop_id))return m;
            }
        }
        return null;
    }

    public static Map<String, Object> getTripData(List<Map<String, Object>> list,String trip_id){
        for(Map<String, Object> m : list){
             if(m.get("trip_id").equals(trip_id))return m;
        }
        return null;
    }

    private static Map<String, Object> getTirpUpdate(URL url)throws IOException {
        FeedMessage feed = FeedMessage.parseFrom(url.openStream());
        Map<String, Object> root=new HashMap<>();
        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasTripUpdate()) {
                TripUpdate update=entity.getTripUpdate();
                Map<String, Object> map=new HashMap<String, Object>();
                map.put("trip_id", update.getTrip().getTripId());
                map.put("route_id", update.getTrip().getRouteId());
                map.put("time", update.getTimestamp());
                List<Map<String, Object>> list=new ArrayList<>();
                map.put("stops", list);
                for(int i=0;i<update.getStopTimeUpdateCount();i++){
                    StopTimeUpdate stu=update.getStopTimeUpdate(i);
                    Map<String, Object> sm=new HashMap<String, Object>();
                    sm.put("stop_id", stu.getStopId());
                    sm.put("stop_sequence", stu.getStopSequence());
                    sm.put("arruval_delay", stu.getArrival().getDelay());
                    sm.put("depature_delay",stu.getDeparture().getDelay());
                    list.add(sm);
                }
                root.put(update.getTrip().getTripId(),map);
            }
        }
        return root;
    }

    private static Map<String, Object> getVehiclePosition(URL url)throws IOException {
        FeedMessage feed = FeedMessage.parseFrom(url.openStream());
        Map<String, Object> ret=new HashMap<>();
        for (FeedEntity entity : feed.getEntityList()) {
            if (entity.hasVehicle()) {
                VehiclePosition vp=entity.getVehicle();
                Map<String, Object> veh=new HashMap<>();
                veh.put("id", vp.getVehicle().getId());
                veh.put("route_id",vp.getTrip().getRouteId());
                veh.put("trip_id", vp.getTrip().getTripId());
                veh.put("stop_id", vp.getStopId());
                Position p=vp.getPosition();
                veh.put("lat", p.getLatitude());
                veh.put("lon", p.getLongitude());
                veh.put("speed", p.getSpeed());
                veh.put("status",vp.getCurrentStatus().getNumber());
                ret.put(vp.getTrip().getTripId(), veh);
            }
        }
        return ret;
    }

}
