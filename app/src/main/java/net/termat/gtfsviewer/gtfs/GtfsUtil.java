package net.termat.gtfsviewer.gtfs;

import android.graphics.Color;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import net.termat.gtfsviewer.gtfs.model.GShape;
import net.termat.gtfsviewer.gtfs.model.GStop;
import net.termat.gtfsviewer.gtfs.model.GStopTime;
import net.termat.gtfsviewer.gtfs.model.GTrip;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GtfsUtil {

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

    public static String getTimeStr(Long val){
        long hh=(long)Math.floor(val/3600000);
        long mod=val-(hh*3600000);
        long mm=(long)Math.floor(mod/60000);
        long ss=(mod-(mm*60000))/1000;
        StringBuffer buf=new StringBuffer();
        buf.append(hh);
        if(mm<10){
            buf.append(":0");
            buf.append(mm);
        }else{
            buf.append(":"+mm);
        }
        return buf.toString();
    }

    public static List<GStopTime> checkStopTime(List<GStopTime> gt){
        List<GStopTime> ret=new ArrayList<>();
        Set<Long> check=new HashSet<>();
        for(GStopTime g : gt){
            if(check.contains(g.id))continue;
            ret.add(g);
            g.arrival_time=g.arrival_time.substring(0,5);
            check.add(g.id);
        }
        return ret;
    }

    public static List<GStopTime> setDelay(List<GStopTime> gt, List<Map<String,Object>> feed){
        NumberFormat f=NumberFormat.getInstance();
        f.setMaximumFractionDigits(0);
        List<GStopTime> ret=new ArrayList<>();
        Set<Long> check=new HashSet<>();
        Map<String,GStopTime> map=new HashMap<>();
        Map<Integer,GStopTime> map2=new HashMap<>();
        for(GStopTime g : gt){
            map.put(g.stop_id,g);
            map2.put(g.stop_sequence,g);
        }
        for(Map<String,Object> d : feed){
          GStopTime g=map.get((String)d.get("stop_id"));
          if(g==null){
              g=map2.get(((Number)d.get("stop_sequence")).intValue());
              if(g==null)continue;
          }
          if(check.contains(g.id))continue;
          check.add(g.id);
          long time=getTimeVal(g.arrival_time);
          g.departure_time=getTimeStr(time);
          time=time+((Number)d.get("arruval_delay")).longValue()*1000;
          g.arrival_time=getTimeStr(time);
          g.stop_headsign=f.format(Math.round(((Number)d.get("arruval_delay")).doubleValue()/60));
          ret.add(g);
        }
        return ret;
    }

    public static List<GStopTime> setDelay2(List<GStopTime> gt, int stop,int delay){
        NumberFormat f=NumberFormat.getInstance();
        f.setMaximumFractionDigits(0);
        for(GStopTime g : gt){
            if(g.stop_sequence>=stop){
                long time=getTimeVal(g.arrival_time);
                g.departure_time=getTimeStr(time);
                time=time+delay*1000;
                g.arrival_time=getTimeStr(time);
                g.stop_headsign=f.format(delay/60);
            }
        }
        return gt;
    }

    public static List<LineOptions> createLineOption(Gtfs gtfs,String agency_id,String trip_id,List<GStopTime> st,Map<String,GStop> map,int color,float width){
        try{
            List<LineOptions> ret=new ArrayList<>();
            List<GTrip> t=gtfs.getTrip(agency_id,trip_id);
            List<GShape> sp=gtfs.getShape(agency_id,t.get(0).shape_id);
            if(sp.size()==0)return createLineOption(st,map,color,width);
            List<LatLng> latLngs = new ArrayList<>();
            for(GShape g : sp){
                latLngs.add(new LatLng(g.shape_pt_lat,g.shape_pt_lon));
            }
              LineOptions lineOptions = new LineOptions()
                    .withLatLngs(latLngs)
                    .withLineColor(ColorUtils.colorToRgbaString(color))
                    .withLineWidth(width);
            ret.add(lineOptions);
            return ret;
        }catch(Exception e){
            e.printStackTrace();
            return createLineOption(st,map,color,width);
        }
    }

    public static List<LineOptions> createLineOption(List<GStopTime> st,Map<String,GStop> map,int color,float width){
        List<LineOptions> ret=new ArrayList<>();
        List<LatLng> latLngs = new ArrayList<>();
        for(GStopTime g : st){
            GStop gs=map.get(g.stop_id);
            latLngs.add(new LatLng(gs.stop_lat,gs.stop_lon));
        }
        LineOptions lineOptions = new LineOptions()
                .withLatLngs(latLngs)
                .withLineColor(ColorUtils.colorToRgbaString(color))
                .withLineWidth(width);
        ret.add(lineOptions);
        return ret;
    }

    public static GTrip create(String[] title,String[] line){
        GTrip ret=new GTrip();
        for(int i=0;i<title.length;i++){
            if(title.equals("trip_short_name")){
                ret.trip_short_name=line[i];
            }else if(title.equals("trip_id")){
                ret.trip_id=line[i];
            }else if(title.equals("direction_id")){
                ret.direction_id=line[i];
            }else if(title.equals("route_id")){
                ret.route_id=line[i];
            }else if(title.equals("service_id")){
                ret.service_id=line[i];
            }else if(title.equals("shape_id")){
                ret.shape_id=line[i];
            }else if(title.equals("trip_headsign")){
                ret.trip_headsign=line[i];
            }else if(title.equals("block_id")){
                ret.block_id=line[i];
            }else if(title.equals("jp_trip_desc")){
                ret.jp_trip_desc=line[i];;
            }else if(title.equals("wheelchair_accessible")){
                ret.wheelchair_accessible=line[i];
            }else if(title.equals("bikes_allowed")){
                ret.bikes_allowed=line[i];
            }else if(title.equals("jp_trip_desc_symbol")){
                ret.jp_trip_desc_symbol=line[i];
            }else if(title.equals("jp_office_id")){
                ret.jp_office_id=line[i];
            }
        }
        return ret;
    }
}
