package net.termat.gtfsviewer.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.termat.gtfsviewer.MapActivity;
import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.gtfs.Gtfs;
import net.termat.gtfsviewer.gtfs.GtfsUtil;
import net.termat.gtfsviewer.gtfs.model.GCalendar;
import net.termat.gtfsviewer.gtfs.model.GStop;
import net.termat.gtfsviewer.gtfs.model.GStopTime;
import net.termat.gtfsviewer.gtfs.model.GTrip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiagramUtil {
    private static String[] date=new String[]{"平日","土曜","休日"};

    private static String getDateType(Gtfs gtfs,String agency_id,String service_id){
        List<GCalendar> gl=gtfs.getCalendar(agency_id,service_id);
        if(gl.size()==0)return date[0];
        GCalendar cal=gl.get(0);
        if(Integer.parseInt(cal.sunday)==1){
            return date[2];
        }else if(Integer.parseInt(cal.saturday)==1&&Integer.parseInt(cal.monday)==0){
            return date[1];
        }else{
            return date[0];
        }
    }

    private static int dayOfWeek(){
        Calendar cal=Calendar.getInstance();
        int week=cal.get(Calendar.DAY_OF_WEEK);
        if(week==Calendar.SUNDAY){
            return 2;
        }else if(week==Calendar.SATURDAY){
            return 1;
        }else{
            return 0;
        }
    }

    public static Map<String,List<Diagram>> getDiagram(List<GStopTime> data, GStop st, Gtfs gtfs){
        Map<String,String> trans=new HashMap<>();
        Comparator<GStopTime> comp= (gStopTime, t1) -> {
            int v1=GStopTime.getTimeVal(gStopTime.arrival_time).intValue();
            int v2=GStopTime.getTimeVal(t1.arrival_time).intValue();
            return v1-v2;
        };
        data.sort(comp);
        Map<String,List<Diagram>> map=new HashMap<>();
        Set<String> checker=new HashSet<>();
        for(GStopTime g : data){
            if(checker.contains(g.trip_id))continue;
            List<GTrip> gt=gtfs.getTrip(g.data_id,g.trip_id) ;
            if(gt.size()==0)continue;
            GTrip t=gt.get(0);
            if(!trans.containsKey(t.service_id)){
                String ss=getDateType(gtfs,st.data_id,t.service_id);
                trans.put(t.service_id,ss);
                List<Diagram> ll=new ArrayList<>();
                map.put(ss,ll);
            }
            Diagram di=new Diagram();
            di.name=st.stop_name;
            di.stop_id=st.stop_id;
            di.trip_id=g.trip_id;
            di.head=t.trip_headsign;
            di.time=g.departure_time;
            map.get(trans.get(t.service_id)).add(di);
            checker.add(g.trip_id);
        }
        return map;
    }


    public static AlertDialog showDiagramDialog(MapActivity context, Map<String,List<Diagram>> data, GStop stop){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_diagram, null );
        TextView title=(TextView)view.findViewById(R.id.dialog_stop_name);
        title.setText(stop.stop_name);
        Spinner dateType=(Spinner)view.findViewById(R.id.diagram_date);
        List<String> dd=new ArrayList<>();
        Set<String> se=data.keySet();
        if(se.contains("平日"))dd.add("平日");
        if(se.contains("土曜"))dd.add("土曜");
        if(se.contains("休日"))dd.add("休日");
        StringAdapter sp_adapter=new StringAdapter(context,dd.toArray(new String[dd.size()]));
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateType.setAdapter(sp_adapter);
        int ck=Math.min(dayOfWeek(),dd.size()-1);
        dateType.setSelection(ck);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        DialogInterface.OnClickListener yes= (dialog, which) ->{
            dialog.dismiss();
        };
        builder.setPositiveButton(context.getString(R.string.close), yes);
        AlertDialog alertDialog = builder.create();
        RecyclerView list=(RecyclerView)view.findViewById(R.id.diagram_list);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                new LinearLayoutManager(context).getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        RecycleDiagramListAdapter adapter=new RecycleDiagramListAdapter(context,R.layout.item_diagram,data.get((String)dateType.getSelectedItem()));
        list.setAdapter(adapter);
        RecycleDiagramListAdapter.OnClickListsner onClickListsner=new RecycleDiagramListAdapter.OnClickListsner(){
            @Override
            public void onClick(int index) {
                alertDialog.dismiss();
                List<Diagram> di=data.get((String)dateType.getSelectedItem());
                Diagram dd=di.get(index);
                List<GStopTime> list =Gtfs.getInstance(context).getStoptimeAtTrip(stop.data_id,dd.trip_id);
                list= GtfsUtil.checkStopTime(list);
                context.showRoute(list,dd.trip_id,stop.stop_id,null);
            }
        };
        adapter.setOnItemClickListener(onClickListsner);
        dateType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                RecycleDiagramListAdapter adapter=new RecycleDiagramListAdapter(context,R.layout.item_diagram,data.get(dateType.getSelectedItem().toString()));
                list.setAdapter(adapter);
                adapter.setOnItemClickListener(onClickListsner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity= Gravity.TOP;
        alertDialog.show();
        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        Point point = new Point(0, 0);
        display.getRealSize(point);
        ViewGroup.LayoutParams lp=view.getLayoutParams();
        lp.height = (int)(point.y*0.6);
        view.setLayoutParams(lp);
        return alertDialog;
    }

    static class StringAdapter extends ArrayAdapter<String> {

        public StringAdapter(Context context, String[] list) {
            super(context, android.R.layout.simple_spinner_item, list);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView)super.getView(position, convertView, parent);
            textView.setText(getItem(position));
            return textView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView)super.getDropDownView(position, convertView, parent);
            textView.setText(getItem(position));
            return textView;
        }
    }
}
