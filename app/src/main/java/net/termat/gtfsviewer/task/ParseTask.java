package net.termat.gtfsviewer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import net.termat.gtfsviewer.MainActivity;
import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.components.SpProgressDialog;
import net.termat.gtfsviewer.gtfs.Gtfs;
import net.termat.gtfsviewer.gtfs.data.GtfsView;
import net.termat.gtfsviewer.gtfs.data.GtfsViewData;

import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTask extends AsyncTask<String, Integer, String> {
    private static Pattern csvDiv=Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|([^,]+)|,|");
    private Gtfs gtfs;
    private FragmentManager fragmentManager;
    private MainActivity act;
    private Context context;
    private SpProgressDialog progress = null;
    private GtfsView data;
    private Gson gson=new Gson();
    private int numOfEntry=0;
    private GtfsViewData gtfsObj;
    private File path;

    public ParseTask(MainActivity act, FragmentManager fragmentManager, GtfsView data, File path){
        this.act=act;
        this.context=act.getApplicationContext();
        this.gtfs=Gtfs.getInstance(context);
        this.fragmentManager=fragmentManager;
        this.data=data;
        gtfsObj= GtfsViewData.getInstance(context);
        this.path=path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = SpProgressDialog.newInstance(R.string.db_build);
        progress.show(fragmentManager,"Progress");
    }

    @Override
    protected void onProgressUpdate(Integer... val) {
        progress.setProgress(val[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try{
            progress.dismiss();
            for(File f : path.listFiles())f.delete();
        }catch(IllegalStateException ie){
            progress.dismiss();
        }
        Log.w("TAG","----------------> "+result);
        if (result.equals("STATUS:OK")){
            gtfsObj.add(data);
            Toast.makeText(context, "Completed.", Toast.LENGTH_SHORT).show();
            act.updateListView();
        }else{
            Toast.makeText(context, "NG", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            String data_id=GtfsViewData.getInstance(context).getDataId();
            data.data_id=data_id;
            build(data_id);
            return "STATUS:OK";
        }catch(Exception e){
            e.printStackTrace();
            return "STATUS:NG";
        }
    }

    private void build(String data_id) throws IOException, ParseException {
        File[] fs=path.listFiles();
        for(File f : fs){
             try{
                BOMInputStream bis = new BOMInputStream(new FileInputStream(f));
                BufferedReader br=new BufferedReader(new InputStreamReader(bis));
                List<String> list=json2Csv(parseCsv(br));
                br.close();
                String name=f.getName().toLowerCase();
                gtfs.insertData(name,list,data_id);
            }catch(Exception e){
                e.printStackTrace();
            }
            numOfEntry++;
            publishProgress(numOfEntry*5);
        }
     }

    private String[][] parseCsv(BufferedReader reader)throws IOException{
        ArrayList<String[]> list=new ArrayList<>();
        String line;
        while((line=reader.readLine())!=null){
//           String[] sp=split(csvDiv,line);
            line=line.replaceAll(",,",", ,");
            line=line.replaceAll("\"","");
            String[] sp=line.split(",");
            list.add(sp);
        }
        if(list.size()==0)return new String[0][0];
        return list.toArray(new String[list.size()][]);
    }

    private static String[] split(Pattern pattern, String line){
        line=line.replace(",,",",\"\",");
        Matcher matcher=pattern.matcher(line);
        List<String> list=new ArrayList<>();
        int index=0;
        int com=0;
        while(index<line.length()){
            if(matcher.find(index+com)){
                String s=matcher.group().replaceAll("\"", "");
                index=matcher.end();
                list.add(s);
                com=1;
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static final String BOM = "\uFEFF";
    private List<String> json2Csv(String[][] csv)throws ParseException{
        List<String> list=new ArrayList<>();
        String[] title=csv[0];
        for(int i=0;i<title.length;i++){
            if(title[i].contains("stop_id"))title[i]="stop_id";
        }
        for(int i=1;i<csv.length;i++){
            list.add(createMap(title,csv[i]));
        }
        return list;
    }

    private String createMap(String[] title,String[] val)throws ParseException{
        Map<String,Object> ret=new HashMap<String,Object>();
        for(int i=0;i<val.length;i++){
            if(title[i]==null)continue;
            if(title[i].endsWith("_lon")||title[i].endsWith("_lat")){
                ret.put(title[i], Double.parseDouble(val[i]));
            }else if(title[i].endsWith("_date")){
                ret.put(title[i], val[i]);
            }else if(title[i].endsWith("_time")){
                ret.put(title[i], val[i]);
            }else{
                ret.put(title[i], val[i].toString());
            }
        }
        return gson.toJson(ret);
    }
}
