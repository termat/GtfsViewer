package net.termat.gtfsviewer.components;

import android.app.Activity;

import com.mapbox.mapboxsdk.maps.Style;

import net.termat.gtfsviewer.R;

import java.util.HashMap;
import java.util.Map;

public class Symbols {
    public final static String MARKER_BUS="BUS";
    public final static String MARKER_POINT="POINT";
    public final static String MARKER_FLAG="FLAG";
    private static Map<String, Integer> drawable;

    public static void initSymbols(Activity activity, Style style){
        style.addImage(MARKER_BUS,activity.getDrawable(R.drawable.icon_bus));
        style.addImage(MARKER_POINT,activity.getDrawable(R.drawable.icon_point));
        style.addImage(MARKER_FLAG,activity.getDrawable(R.drawable.icon_flag));
        drawable=new HashMap<>();
        drawable.put(MARKER_BUS,R.drawable.icon_bus);
        drawable.put(MARKER_POINT,R.drawable.icon_point);
        drawable.put(MARKER_FLAG,R.drawable.icon_flag);
    }

    public static int getDrawableID(String str){
        return drawable.get(str);
    }
}
