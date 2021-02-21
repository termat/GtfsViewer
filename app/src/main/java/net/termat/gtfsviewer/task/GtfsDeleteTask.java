package net.termat.gtfsviewer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.termat.gtfsviewer.gtfs.Gtfs;
import net.termat.gtfsviewer.gtfs.data.GtfsView;

public class GtfsDeleteTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private GtfsView data;
    private Gtfs gtfs;

    public GtfsDeleteTask(Context context, GtfsView data){
        this.context=context;
        this.gtfs= Gtfs.getInstance(context);
        this.data=data;
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            gtfs.deleteAtDataId(data.data_id);
            return "OK";
        }catch(Exception e){
            return "NG";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("OK")){
            Toast.makeText(context, "Data deletion completed.", Toast.LENGTH_SHORT).show();
            Log.w("TAG","Data deletion completed.");
        }else{
            Toast.makeText(context, "Data deletion failed.", Toast.LENGTH_SHORT).show();
            Log.w("TAG","Data deletion failed.");
        }
    }
}
