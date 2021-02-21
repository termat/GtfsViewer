package net.termat.gtfsviewer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Line;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;

import net.termat.gtfsviewer.components.Diagram;
import net.termat.gtfsviewer.components.DiagramUtil;
import net.termat.gtfsviewer.components.DialogUtil;
import net.termat.gtfsviewer.components.GlobalData;
import net.termat.gtfsviewer.components.SearchListAdapter;
import net.termat.gtfsviewer.components.Symbols;
import net.termat.gtfsviewer.gtfs.Gtfs;
import net.termat.gtfsviewer.gtfs.GtfsRtReader;
import net.termat.gtfsviewer.gtfs.GtfsUtil;
import net.termat.gtfsviewer.gtfs.data.GtfsView;
import net.termat.gtfsviewer.gtfs.data.GtfsViewData;
import net.termat.gtfsviewer.gtfs.model.GStop;
import net.termat.gtfsviewer.gtfs.model.GStopTime;
import net.termat.gtfsviewer.gtfs.model.GTrip;
import net.termat.gtfsviewer.task.GtfsRtTask;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;


/**
 MapActivity2：ルート地図用Activity
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {
    private static final int VOICE_REQUEST_CODE = 1000;
    private int speechType=0;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private GlobalData global=GlobalData.getInstance();
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;
    private SymbolManager symbols;
    private LineManager lines;
    private BuildingPlugin buildingPlugin;
    private Button gps;
    private Gtfs gtfs;
    private GtfsView gview;
    private LatLngBounds home;
    private DialogManager man;
    private List<Symbol> stops;
    private Map<String,GStop> stopMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Button bt=(Button)findViewById(R.id.button_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.setOpened(false);
                finish();
            }
        });
        Button search=(Button)findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogUtil.showSearchDialog(getThis(),getString(R.string.geocorder),home,stops,symbols,null);
            }
        });
        Button ht=(Button)findViewById(R.id.home_button);
        ht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(home!=null)mapPan(home);
            }
        });
        Button gps=(Button)findViewById(R.id.gps_button);
        gps.setOnClickListener(v -> {
            if(locationComponent==null)return;
            if (locationComponent.getLastKnownLocation()!= null){
                android.location.Location loc=locationComponent.getLastKnownLocation();
                CameraPosition cam = new CameraPosition.Builder()
                        .target(new LatLng(loc.getLatitude(),loc.getLongitude()))
                        .zoom(15)
                        .tilt(50)
                        .bearing(0)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam), 1000);
            }
        });
        Button rt=(Button)findViewById(R.id.rt_bus);
        rt.setOnClickListener(v -> {
            try {
                man.close();
                showRT();
            } catch (MalformedURLException e) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getThis());
                builder.setMessage(R.string.get_rt_fail);
                builder.create().show();
                e.printStackTrace();
            }
        });
        Button st=(Button)findViewById(R.id.bus_stop);
        st.setOnClickListener(v -> {
            man.showIconList("停留所一覧",false,stops);
            man.showIconList("停留所一覧",false,stops);
        });

        Intent i = getIntent();
        String data_id = i.getStringExtra("DATA_ID");
        gtfs=Gtfs.getInstance(this);
        gview=GtfsViewData.getInstance(this).getDao().getGtfsView(data_id).get(0);
        man=new DialogManager();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            man.h_rate=0.2f;
        }else{
            man.h_rate=0.25f;
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        AlertDialog.Builder b=new AlertDialog.Builder(getThis());
//        b.setMessage(getString(R.string.user_location_permission_explanation));
//        b.show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            AlertDialog.Builder b=new AlertDialog.Builder(getThis());
            b.setMessage(getString(R.string.user_location_permission_explanation));
            b.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    permissionsManager.requestLocationPermissions(getThis());
                }
            });
            b.show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {}

    @Override
    public void onCameraTrackingChanged(int currentMode) {}

    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.setCameraPosition(global.getCam());
                enableLocationComponent(style);
                buildingPlugin = new BuildingPlugin(mapView,mapboxMap,style);
                buildingPlugin.setMinZoomLevel(12);
                buildingPlugin.setOpacity(0.5f);
                buildingPlugin.setColor(Color.parseColor("#88cccccc"));
                buildingPlugin.setVisibility(true);
                lines=new LineManager(mapView, mapboxMap, style);
                symbols = new SymbolManager(mapView, mapboxMap, style);
                symbols.setIconAllowOverlap(true);
                symbols.setIconIgnorePlacement(false);
                symbols.addClickListener(new OnSymbolClickListener(){
                    @Override
                    public void onAnnotationClick(Symbol symbol) {
                        JsonObject obj=(JsonObject)symbol.getData();
                        String type=obj.get("type").getAsString();
                        if(type.equals("STOP")){
                            String stop_id=obj.get("stop_id").getAsString();
                            showDiagram(stop_id);
                        }else if(type.equals("BUS")){
                            Gson gson=new Gson();
                            String json=obj.get("data").getAsString();
                            Map<String,Object> data=(Map<String,Object>)gson.fromJson(json,Map.class);
                            if(data!=null){
                                List<Map<String,Object>> feed=(List<Map<String,Object>>)data.get("stops");
                                String trip_id=obj.get("trip_id").getAsString();
                                String stop_id=obj.get("stop_id").getAsString();
                                List<GStopTime> gt=gtfs.getStoptimeAtTrip(gview.data_id,trip_id);
                  Log.w("TEST","--------------->   "+feed.size());
                                if(feed.size()==1){
                                    int s1=((Number)feed.get(0).get("stop_sequence")).intValue();
                                    int delay=((Number)feed.get(0).get("depature_delay")).intValue();
                                    for(GStopTime t : gt){
                                        if(t.stop_sequence==s1){
                                            stop_id=t.stop_id;
                                            break;
                                        }
                                    }
                                    gt=GtfsUtil.setDelay2(gt,s1,delay);
                                }else{
                                    gt=GtfsUtil.setDelay(gt,feed);
                                }
                                showRoute(gt,trip_id,stop_id,symbol);
                            }
                        }
                    }
                });
                Symbols.initSymbols(getThis(),style);
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Log.d("Locale", exception.toString());
                }
                showStops();
                /*
                if(!global.isOpened()&&home!=null){
                    mapPan(home);
                    global.setOpened(true);
                }
                 */
            }
        });

        mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition c=mapboxMap.getCameraPosition();
                global.setCam(c);
            }
        });

        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener(){
            @Override
            public boolean onMapLongClick(@NonNull LatLng point) {

                return false;
            }
        });
    }

    private static String checkTime(String args){
        if(args.length()>5){
            return args.substring(0,5);
        }else{
            return args;
        }
    }

    public void showRoute(List<GStopTime> list,String trip_id,String stop_id,Symbol bus){
        man.close();
        List<Symbol> ret=new ArrayList<>();
        for(GStopTime t :list){
            GStop st=stopMap.get(t.stop_id);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "FLAG");
            String title=checkTime(t.arrival_time)+"  "+st.stop_name;
            try{
                if(Integer.parseInt(t.stop_headsign)>0){
                    title=title+" 【約"+ t.stop_headsign+"分の遅れ】";
                }
            }catch(NumberFormatException ne){}
            SymbolOptions symbolOptions = new SymbolOptions()
                    .withLatLng(new LatLng(st.stop_lat, st.stop_lon))
                    .withTextField(title)
                    .withData(jsonObject)
                    .withTextColor("#000000")
                    .withTextSize(0.0f)
                    .withTextOffset(new Float[]{0.0f, -1.2f})
                    .withTextJustify(TEXT_JUSTIFY_AUTO)
                    .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                    .withIconImage(Symbols.MARKER_FLAG)
                    .withIconSize(0.0f)
                    .withSymbolSortKey(10.0f)
                    .withDraggable(false);
            ret.add(symbols.create(symbolOptions));
            if(bus!=null&&st.stop_id.equals(stop_id)){
                SymbolOptions symbolOptions2 = new SymbolOptions()
                        .withLatLng(bus.getLatLng())
                        .withTextField(bus.getTextField())
                        .withData(bus.getData())
                        .withTextColor("#000000")
                        .withTextSize(14.0f)
                        .withTextOffset(new Float[]{0.0f, -1.2f})
                        .withTextJustify(TEXT_JUSTIFY_AUTO)
                        .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                        .withIconImage(Symbols.MARKER_BUS)
                        .withIconSize(0.9f)
                        .withSymbolSortKey(10.0f)
                        .withDraggable(false);
                ret.add(symbols.create(symbolOptions2));
//                ret.add(bus);
            }
        }
//        List<LineOptions> ll=GtfsUtil.createLineOption(list,stopMap,Color.RED,7.0f);
        List<LineOptions> ll=GtfsUtil.createLineOption(gtfs,gview.data_id,trip_id,list,stopMap,Color.RED,7.0f);
        man.addLine(ll);
        man.showIconList("運行ルート："+trip_id,true,ret);
        man.showIconList("運行ルート："+trip_id,true,ret);
    }

    private void showDiagram(String stop_id){
        List<GStop> st=gtfs.getStops(gview.data_id,stop_id);
        List<GStopTime> gs=gtfs.getStopTimeAtStop(gview.data_id,stop_id);
        Map<String,List<Diagram>> map= DiagramUtil.getDiagram(gs,st.get(0),gtfs);
        AlertDialog alertDialog=DiagramUtil.showDiagramDialog(this,map,st.get(0));
    }

    private void showStops(){
        stops=new ArrayList<Symbol>();
        stopMap=new HashMap<>();
        List<GStop> gs=gtfs.getStops(gview.data_id);
        int mm=gs.size();
        home=getLatLngBounds(gs);
        mapPan(home);
        List<SymbolOptions> op=new ArrayList<>();
        for(GStop s:gs){
            if(!s.location_type.equals("0"))continue;
             stopMap.put(s.stop_id,s);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stop_id", s.stop_id);
            jsonObject.addProperty("type", "STOP");
            SymbolOptions symbolOptions = new SymbolOptions()
                     .withLatLng(new LatLng(s.stop_lat, s.stop_lon))
                    .withTextField(s.stop_name)
                    .withData(jsonObject)
                    .withTextColor("#000000")
                    .withTextSize(14.0f)
                    .withTextOffset(new Float[]{0.0f, -1.2f})
                    .withTextJustify(TEXT_JUSTIFY_AUTO)
                    .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                    .withIconImage(Symbols.MARKER_POINT)
                    .withIconSize(0.9f)
                    .withSymbolSortKey(10.0f)
                    .withDraggable(false);
            op.add(symbolOptions);
        }
        stops.addAll(symbols.create(op));
    }

    private void showRT() throws MalformedURLException {
        GtfsRtReader r=new GtfsRtReader(gview);
        GtfsRtTask task=new GtfsRtTask(getThis(),getSupportFragmentManager(),r);
        GtfsRtTask.Listener lt=new GtfsRtTask.Listener() {
            @Override
            public void onSuccess(Map<String,Object> json) {
                Gson gson=new Gson();
                Map<String,Object> list=(Map<String,Object>)json.get("trip");
                Map<String,Object> veh=(Map<String,Object>)json.get("vehicle");
                List<Symbol> ret=new ArrayList<>();
                for(Object v : veh.values()){
                    Map<String,Object> m=(Map<String,Object>)v;
                    JsonObject jsonObject = new JsonObject();
                    String trip_id=(String)m.get("trip_id");
                    String stop_id=(String)m.get("stop_id");
                    Map<String,Object> p=(Map<String,Object>)list.get(trip_id);
                    jsonObject.addProperty("stop_id", stop_id);
                    jsonObject.addProperty("trip_id", trip_id);
                    jsonObject.addProperty("type", "BUS");
                    jsonObject.addProperty("data", gson.toJson(p));
                    List<GTrip> tl=gtfs.getTrip(gview.data_id,trip_id);
                    String title=tl.get(0).trip_headsign;
                    /*
                    if(p!=null){
                        List<Map<String,Object>> o=(List<Map<String,Object>>)p.get("stops");
                        if(o!=null&&o.size()>=2){
                            GStop s1=stopMap.get(o.get(0).get("stop_id"));
                            GStop s2=stopMap.get(o.get(o.size()-1).get("stop_id"));
                            if(s1!=null&&s2!=null)title=s1.stop_name+"->"+s2.stop_name;
                        }
                    }
                    */
                    SymbolOptions symbolOptions = new SymbolOptions()
                            .withLatLng(new LatLng(((Number)m.get("lat")).doubleValue(), ((Number)m.get("lon")).doubleValue()))
                            .withTextField(title)
                            .withData(jsonObject)
                            .withTextColor("#000000")
                            .withTextSize(14.0f)
                            .withTextOffset(new Float[]{0.0f, -1.2f})
                            .withTextJustify(TEXT_JUSTIFY_AUTO)
                            .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                            .withIconImage(Symbols.MARKER_BUS)
                            .withIconSize(0.9f)
                            .withSymbolSortKey(10.0f)
                            .withDraggable(false);
                    ret.add(symbols.create(symbolOptions));
                }
                man.showIconList("バス運行状況",true,ret);
                man.showIconList("バス運行状況",true,ret);
            }
        };
        task.setListener(lt);
        task.execute("");
    }

    private MapActivity getThis(){
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        man.spotdialog.dismiss();
        mapView.onDestroy();
    }

    public void mapPan(LatLngBounds bounds){
        CameraPosition cam0=mapboxMap.getCameraPosition();
        CameraPosition cam1 = new CameraPosition.Builder()
                .target(cam0.target)
                .zoom(cam0.zoom)
                .tilt(0)
                .bearing(0)
                .build();
        mapboxMap.setCameraPosition(cam1);
        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 1000);
    }

    public void mapPan(LatLng p,int zoom,double tilt){
        CameraPosition c=mapboxMap.getCameraPosition();
        double bearing=c.bearing;
        if(Math.random()<0.5){
            bearing +=45.0;
        }else{
            bearing -=45.0;
        }
        CameraPosition cam = new CameraPosition.Builder()
                .target(p)
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam), 1000);
    }

    public static LatLngBounds getLatLngBounds(List<GStop> stops){
        List<LatLng> tmp=new ArrayList<LatLng>();
        for(GStop m : stops){
            tmp.add(new LatLng(m.stop_lat,m.stop_lon));
        }
        if(tmp.size()==0)return null;
        LatLngBounds.Builder builder=new LatLngBounds.Builder();
        builder.includes(tmp);
        return builder.build();
    }

    public static LatLngBounds getLatLngBounds(String geojson){
        Map<String,Object> root=new Gson().fromJson(geojson,Map.class);
        List<Map<String,Object>> list=(List<Map<String,Object>>)root.get("features");
        List<LatLng> tmp=new ArrayList<LatLng>();
        for(Map<String,Object> m : list){
            Map<String,Object> geom=(Map<String,Object>)m.get("geometry");
            List<Double> cood=(List<Double>)geom.get("coordinates");
            tmp.add(new LatLng(cood.get(1),cood.get(0)));
        }
        if(tmp.size()==0)return null;
        LatLngBounds.Builder builder=new LatLngBounds.Builder();
        builder.includes(tmp);
        return builder.build();
    }

    public void removeSymbols(List<Symbol> list){
        for(Symbol s :list){
            symbols.delete(s);
        }
    }

    class DialogManager{
        TextView title;
        AlertDialog spotdialog;
        ListView listView;
        SearchListAdapter adapter;
        List<Symbol> item=new ArrayList<>();
        float h_rate;
        boolean isRemove=false;
        List<Line> lineList=new ArrayList<Line>();

        DialogManager(){
            LinearLayout linearLayout = new LinearLayout(getThis());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            title=new TextView(getThis());
            title.setTextColor(Color.BLACK);
            title.setBackgroundColor(Color.parseColor("#eeeeee"));
            title.setTextSize(18.0f);
            linearLayout.addView(title);
            listView = new ListView(getThis());
            linearLayout.addView(listView);
            listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
            adapter=new SearchListAdapter(getThis(),R.layout.search_item,item);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LatLng p=item.get(position).getLatLng();
                    if(p==null)return;
                    mapPan(p,16,50.0);
                }
            });
            AlertDialog.Builder builder=new AlertDialog.Builder(getThis());
            builder.setView(linearLayout);
            DialogInterface.OnClickListener yes= (dialog, which) -> {
                close();
            };
            builder.setPositiveButton(getString(R.string.close), yes);
            builder.setNeutralButton(getString(R.string.bounds), null);
            builder.setCancelable(false);
            spotdialog=builder.create();
            spotdialog.setCancelable(false);
            spotdialog.getWindow().setFlags( 0 , WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            spotdialog.getWindow().setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL , WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        }

        void close(){
            spotdialog.hide();
            if(isRemove)removeSymbols(item);
            this.item.clear();
            deleteLine();
        }

        void addLine(List<LineOptions> lineOptions){
            lineList.clear();
            for(LineOptions op:lineOptions){
                lineList.add(lines.create(op));
            }
        }

        void deleteLine(){
            for(Line s :lineList){
                lines.delete(s);
            }
            lineList.clear();
        }

        void showIconList(String mes, boolean b, List<Symbol> sym){
            this.item.clear();
            isRemove=b;
            this.item.addAll(sym);
            title.setText(mes);
            listView.setAdapter(adapter);
            WindowManager.LayoutParams wmlp = spotdialog.getWindow().getAttributes();
            wmlp.gravity= Gravity.BOTTOM;
            spotdialog.show();
            Display display = getWindow().getWindowManager().getDefaultDisplay();
            Point point = new Point(0, 0);
            display.getRealSize(point);
            ViewGroup.LayoutParams lpt=listView.getLayoutParams();
            lpt.height = (int)(point.y*0.24);
            listView.setLayoutParams(lpt);
            ViewGroup.LayoutParams lp = title.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
            mlp.setMargins(5,10,10,0);
            title.setLayoutParams(mlp);
            spotdialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<LatLng> tmp=new ArrayList<LatLng>();
                    for(Symbol m : item){
                        tmp.add(m.getLatLng());
                    }
                    if(tmp.size()==0)return;
                    LatLngBounds.Builder lb=new LatLngBounds.Builder();
                    lb.includes(tmp);
                    mapPan(lb.build());
                }
            });
        }
    }

    public void showSymbolList(String title,List<Symbol> sym){
        man.showIconList(title, true,  sym);
    }

    public void speech(int lang,int sppechType){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if(lang == 0){
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString() );
        }else if(lang == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString());
        }else{
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_input));
        try {
            this.speechType=sppechType;
            startActivityForResult(intent, VOICE_REQUEST_CODE);
        }catch (ActivityNotFoundException e) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)return;
        switch(requestCode){
            case VOICE_REQUEST_CODE:
                ArrayList<String> candidates =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(candidates.size() > 0) {
                    switch (speechType){
                        case 0:
                            DialogUtil.showSearchDialog(getThis(),getString(R.string.geocorder),home,stops,symbols,candidates.get(0));
                            return;
                    }
                }
                break;
        }
    }
}
