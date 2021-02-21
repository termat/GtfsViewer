package net.termat.gtfsviewer.gtfs;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.gson.Gson;

import net.termat.gtfsviewer.gtfs.model.GAgency;
import net.termat.gtfsviewer.gtfs.model.GAgencyDB;
import net.termat.gtfsviewer.gtfs.model.GAgencyJp;
import net.termat.gtfsviewer.gtfs.model.GAgencyJpDB;
import net.termat.gtfsviewer.gtfs.model.GCalendar;
import net.termat.gtfsviewer.gtfs.model.GCalendarDB;
import net.termat.gtfsviewer.gtfs.model.GCalendarDate;
import net.termat.gtfsviewer.gtfs.model.GCalendarDateDB;
import net.termat.gtfsviewer.gtfs.model.GFareAttribute;
import net.termat.gtfsviewer.gtfs.model.GFareAttributeDB;
import net.termat.gtfsviewer.gtfs.model.GFareRule;
import net.termat.gtfsviewer.gtfs.model.GFareRuleDB;
import net.termat.gtfsviewer.gtfs.model.GFeedInfo;
import net.termat.gtfsviewer.gtfs.model.GFeedInfoDB;
import net.termat.gtfsviewer.gtfs.model.GFrequency;
import net.termat.gtfsviewer.gtfs.model.GFrequencyDB;
import net.termat.gtfsviewer.gtfs.model.GOfficeJp;
import net.termat.gtfsviewer.gtfs.model.GOfficeJpDB;
import net.termat.gtfsviewer.gtfs.model.GRoute;
import net.termat.gtfsviewer.gtfs.model.GRouteDB;
import net.termat.gtfsviewer.gtfs.model.GRouteJp;
import net.termat.gtfsviewer.gtfs.model.GRouteJpDB;
import net.termat.gtfsviewer.gtfs.model.GShape;
import net.termat.gtfsviewer.gtfs.model.GShapeDB;
import net.termat.gtfsviewer.gtfs.model.GStop;
import net.termat.gtfsviewer.gtfs.model.GStopDB;
import net.termat.gtfsviewer.gtfs.model.GStopTime;
import net.termat.gtfsviewer.gtfs.model.GStopTimeDB;
import net.termat.gtfsviewer.gtfs.model.GTransfer;
import net.termat.gtfsviewer.gtfs.model.GTransferDB;
import net.termat.gtfsviewer.gtfs.model.GTranslation;
import net.termat.gtfsviewer.gtfs.model.GTranslationDB;
import net.termat.gtfsviewer.gtfs.model.GTrip;
import net.termat.gtfsviewer.gtfs.model.GTripDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Gtfs {
    private static Pattern csvDiv=Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|([^,]+)|,|");
    private static Gtfs gtfs;
    private static Calendar cal= Calendar.getInstance();
    private Gson gson=new Gson();

    private GAgencyDB agency;
    private GAgencyJpDB agencyJp;
    private GCalendarDB calendar;
    private GCalendarDateDB date;
    private GFareAttributeDB fare;
    private GFareRuleDB rule;
    private GFeedInfoDB feed;
    private GFrequencyDB freq;
    private GOfficeJpDB office;
    private GRouteDB route;
    private GRouteJpDB routeJp;
    private GShapeDB shape;
    private GStopDB stop;
    private GStopTimeDB time;
    private GTransferDB transf;
    private GTranslationDB transl;
    private GTripDB trip;

    public static Gtfs getInstance(Context context){
        if(gtfs==null)gtfs=new Gtfs(context);
        return gtfs;
     }

    private Gtfs(Context context){
        agency = Room.databaseBuilder(context,GAgencyDB.class, "agency")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        agencyJp = Room.databaseBuilder(context,GAgencyJpDB.class, "agancyjp")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        calendar = Room.databaseBuilder(context,GCalendarDB.class, "calendar")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        date = Room.databaseBuilder(context,GCalendarDateDB.class, "calendardate")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        fare = Room.databaseBuilder(context, GFareAttributeDB.class, "fareattribute")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        rule = Room.databaseBuilder(context, GFareRuleDB.class, "farerule")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        feed = Room.databaseBuilder(context, GFeedInfoDB.class, "feedinfo")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        freq = Room.databaseBuilder(context, GFrequencyDB.class, "frequency")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        office = Room.databaseBuilder(context, GOfficeJpDB.class, "office")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        route = Room.databaseBuilder(context, GRouteDB.class, "route")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        routeJp = Room.databaseBuilder(context, GRouteJpDB.class, "routejp")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        shape = Room.databaseBuilder(context, GShapeDB.class, "shape")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        stop = Room.databaseBuilder(context, GStopDB.class, "stop")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        time = Room.databaseBuilder(context, GStopTimeDB.class, "stoptime")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        transf = Room.databaseBuilder(context, GTransferDB.class, "transfer")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        transl = Room.databaseBuilder(context, GTranslationDB.class, "translation")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        trip = Room.databaseBuilder(context, GTripDB.class, "trip")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
    }

    public void insertData(String name,List<String> list,String data_id)throws IOException {
        if(name.equals("agency.txt")){
            agency.getDao().insertAll(create(list, GAgency.class));
        }else if(name.equals("agency_jp.txt")){
            List<GAgencyJp> ll=create(list, GAgencyJp.class);
            for(GAgencyJp a : ll)a.data_id=data_id;
            agencyJp.getDao().insertAll(ll);
        }else if(name.equals("calendar.txt")){
            List<GCalendar> ll=create(list, GCalendar.class);
            for(GCalendar a : ll)a.data_id=data_id;
            calendar.getDao().insertAll(ll);
        }else if(name.equals("calendar_dates.txt")){
            List<GCalendarDate> ll=create(list, GCalendarDate.class);
            for(GCalendarDate a : ll)a.data_id=data_id;
            date.getDao().insertAll(ll);
        }else if(name.equals("fare_attributes.txt")){
            List<GFareAttribute> ll=create(list, GFareAttribute.class);
            for(GFareAttribute a : ll)a.data_id=data_id;
            fare.getDao().insertAll(ll);
        }else if(name.equals("fare_rules.txt")){
            List<GFareRule> ll=create(list, GFareRule.class);
            for(GFareRule a : ll)a.data_id=data_id;
            rule.getDao().insertAll(ll);
        }else if(name.equals("feed_info.txt")){
            List<GFeedInfo> ll=create(list, GFeedInfo.class);
            for(GFeedInfo a : ll)a.data_id=data_id;
            feed.getDao().insertAll(ll);
        }else if(name.equals("frequencies.txt")){
            List<GFrequency> ll=create(list, GFrequency.class);
            for(GFrequency a : ll)a.data_id=data_id;
            freq.getDao().insertAll(ll);
        }else if(name.equals("office_jp.txt")){
            List<GOfficeJp> ll=create(list, GOfficeJp.class);
            for(GOfficeJp a : ll)a.data_id=data_id;
            office.getDao().insertAll(ll);
        }else if(name.equals("routes.txt")){
            List<GRoute> ll=create(list, GRoute.class);
            for(GRoute a : ll)a.data_id=data_id;
            route.getDao().insertAll(ll);
        }else if(name.equals("routjp.txt")){
            List<GRouteJp> ll=create(list, GRouteJp.class);
            for(GRouteJp a : ll)a.data_id=data_id;
            routeJp.getDao().insertAll(ll);
        }else if(name.equals("shapes.txt")){
            List<GShape> ll=create(list, GShape.class);
            for(GShape a : ll)a.data_id=data_id;
            shape.getDao().insertAll(ll);
        }else if(name.equals("stops.txt")){
            List<GStop> ll=create(list, GStop.class);
            for(GStop a : ll)a.data_id=data_id;
            stop.getDao().insertAll(ll);
        }else if(name.equals("transfers.txt")){
            List<GTransfer> ll=create(list, GTransfer.class);
            for(GTransfer a : ll)a.data_id=data_id;
            transf.getDao().insertAll(ll);
        }else if(name.equals("translations.txt")){
            List<GTranslation> ll=create(list, GTranslation.class);
            for(GTranslation a : ll)a.data_id=data_id;
            transl.getDao().insertAll(ll);
        }else if(name.equals("trips.txt")){
            List<GTrip> ll=create(list, GTrip.class);
            for(GTrip a : ll)a.data_id=data_id;
            trip.getDao().insertAll(ll);
        }else if(name.equals("stop_times.txt")){
            List<GStopTime> ll=create(list, GStopTime.class);
            for(GStopTime a : ll)a.data_id=data_id;
            time.getDao().insertAll(ll);
        }
    }

    private List create(List<String> obj, Class cls) {
        List list=new ArrayList();
        if(obj==null)return new ArrayList();
        for(String json : obj) {
            list.add(gson.fromJson(json, cls));
        }
        return list;
    }

    public void deleteAtDataId(String agency_id){
        agency.getDao().getAgency(agency_id);
        agencyJp.getDao().delete(agencyJp.getDao().getDataAtDataId(agency_id));
        calendar.getDao().delete(calendar.getDao().getDataAtDataId(agency_id));
        date.getDao().delete(date.getDao().getDataAtDataId(agency_id));
        fare.getDao().delete(fare.getDao().getDataAtDataId(agency_id));
        rule.getDao().delete(rule.getDao().getDataAtDataId(agency_id));
        feed.getDao().delete(feed.getDao().getDataAtDataId(agency_id));
        freq.getDao().delete(freq.getDao().getDataAtDataId(agency_id));
        office.getDao().delete(office.getDao().getDataAtDataId(agency_id));
        route.getDao().delete(route.getDao().getDataAtDataId(agency_id));
        routeJp.getDao().delete(routeJp.getDao().getDataAtDataId(agency_id));
        shape.getDao().delete(shape.getDao().getDataAtDataId(agency_id));
        stop.getDao().delete(stop.getDao().getDataAtDataId(agency_id));
        time.getDao().delete(time.getDao().getDataAtDataId(agency_id));
        transf.getDao().delete(transf.getDao().getDataAtDataId(agency_id));
        transl.getDao().delete(transl.getDao().getDataAtDataId(agency_id));
        trip.getDao().delete(trip.getDao().getDataAtDataId(agency_id));
    }

    public String getStopsGeojson(String data_id){
        Gson gson=new Gson();
        Map<String,Object> root=new HashMap<String,Object>();
        root.put("type", "FeatureCollection");
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        root.put("features", list);
        List<GStop> gs=stop.getDao().getDataAtDataId(data_id);
        for(GStop s: gs){
            Map<String,Object> item=new HashMap<String,Object>();
            item.put("type","Feature");
            Map<String,Object> geom=new HashMap<String,Object>();
            item.put("geometry", geom);
            geom.put("type", "Point");
            geom.put("coordinates",new double[]{s.stop_lon,s.stop_lat});
            Map<String,Object> prop=new HashMap<String,Object>();
            item.put("properties", prop);
            prop.put("stop_id", s.stop_id);
            prop.put("name", s.stop_name);
            list.add(item);
        }
        return gson.toJson(root);
    }

    public List<GStop> getStops(String data_id){
        return stop.getDao().getDataAtDataId(data_id);
    }

    public List<GStop> getStops(String data_id,String stop_id){
        return stop.getDao().getStop(data_id,stop_id);
    }

    public List<GStopTime> getStopTimeAtStop(String data_id, String stop_id){
        return time.getDao().getDataAtStop(data_id,stop_id);
    }

    public List<GTrip> getTrip(String data_id,String trip_id){
        return trip.getDao().getTrip(data_id,trip_id);
    }

    public List<GCalendar> getCalendar(String data_id,String service_id){
        return calendar.getDao().getData(data_id,service_id);
    }

    public List<GStop> getStopStartEndAtTrip(String data_id,String trip_id) {
        List<GStopTime> tmp =time.getDao().getDataAtTrip(data_id,trip_id);
        List<GStop> s1 = stop.getDao().getStop(data_id, tmp.get(0).stop_id);
        List<GStop> s2 = stop.getDao().getStop(data_id, tmp.get(tmp.size() - 1).stop_id);
        s1.addAll(s2);
        return s1;
    }

    public List<GStopTime> getStoptimeAtTrip(String data_id,String trip_id) {
        return time.getDao().getDataAtTrip(data_id,trip_id);
    }

    public List<GShape> getShape(String data_id,String shape_id){
        return shape.getDao().getShape(data_id,shape_id);
    }

    public void insertAllTrip(List<GTrip> list){
        trip.getDao().insertAll(list);
    }
}
