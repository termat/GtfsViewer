package net.termat.gtfsviewer.task;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class GeoCorderTask extends AsyncTask<String, Void, String> {
    private String url;
    private String keyword;
    private List<Map<String, Object>> json;
    private GeoCorderTask.Listener listener;

    public GeoCorderTask(String url, String keyword){
        this.url=url;
        try{
            this.keyword= URLEncoder.encode(keyword, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection con = null;
        String result = null;
        Gson gson=new Gson();
         try{
             URL u=new URL(url+ keyword);
             con = (HttpsURLConnection) u.openConnection();
             con.setRequestMethod("GET");
             con.setDoOutput(false);
             con.setDoInput(true);
             con.setReadTimeout(10000);
             con.setConnectTimeout(20000);
             con.addRequestProperty("User-Agent", "Android");
             con.addRequestProperty("Accept-Language", Locale.getDefault().toString());
             con.connect();
             int status=con.getResponseCode();
             if(status== HttpsURLConnection.HTTP_OK){
                 InputStream is = con.getInputStream();
                 InputStreamReader isr=new InputStreamReader(is);
                 BufferedReader br=new BufferedReader(isr);
                 String line=null;
                 StringBuffer buf=new StringBuffer();
                 while((line=br.readLine())!=null){
                     buf.append(line);
                 }
                 json=gson.fromJson(buf.toString(), List.class);
             }
             result="status="+ String.valueOf(status);
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (con != null) {
                 con.disconnect();
             }
         }
        return result;
    }

    public void setListener(GeoCorderTask.Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onSuccess(this.json);
        }
    }

    public interface Listener {
        void onSuccess(List<Map<String, Object>> json);
    }
}
