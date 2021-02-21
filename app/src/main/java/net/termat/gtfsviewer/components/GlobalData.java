package net.termat.gtfsviewer.components;

import android.graphics.Color;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * グローバルデータクラス
 */
public class GlobalData {
    private static GlobalData data=null;

    private double lon=135.757755;
    private double lat=34.985458;
    private double zoom=12.0;
    private double tilt=50.0;
    private double bearing=0.0;
    private boolean isOpened=false;

    public static GlobalData getInstance(){
        if(data==null)data=new GlobalData();
        return data;
    }

    public void setCam(CameraPosition cam){
        lat=cam.target.getLatitude();
        lon=cam.target.getLongitude();
        bearing=cam.bearing;
        tilt=cam.tilt;
        zoom=cam.zoom;
    }

    public CameraPosition getCam(){
         CameraPosition cam = new CameraPosition.Builder()
                .target(new LatLng(lat,lon))
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing)
                .build();
         return cam;
    }

    public boolean isOpened(){
        return isOpened;
    }

    public void setOpened(boolean b){
        isOpened=b;
    }


}
