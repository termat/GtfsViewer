package net.termat.gtfsviewer.task;

import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentManager;

import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.components.SpProgressDialog;
import net.termat.gtfsviewer.gtfs.GtfsRtReader;

import java.io.IOException;
import java.util.Map;

public class GtfsRtTask extends AsyncTask<String, Void, String> {
    private GtfsRtReader reader;
    private SpProgressDialog progress = null;
    private FragmentManager fragmentManager;
    private GtfsRtTask.Listener listener;
    private Map<String,Object> geojson;

    public GtfsRtTask(Context context, FragmentManager fragmentManager, GtfsRtReader reader){
        this.reader=reader;
        this.fragmentManager=fragmentManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = SpProgressDialog.newInstance(R.string.get_gtfsrt);
        progress.show(fragmentManager,"Progress");
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            geojson=reader.getRtMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "O.K.";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try{
            if (progress.getShowsDialog()) progress.dismiss();
            if (listener != null) {
                listener.onSuccess(geojson);
            }
        }catch(IllegalStateException ie){}
    }

    public void setListener(GtfsRtTask.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSuccess(Map<String,Object> geojson);
    }
}
