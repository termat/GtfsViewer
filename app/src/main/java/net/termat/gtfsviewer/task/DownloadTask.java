package net.termat.gtfsviewer.task;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import net.termat.gtfsviewer.MainActivity;
import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.components.SpProgressDialog;
import net.termat.gtfsviewer.gtfs.data.GtfsView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private FragmentManager fragmentManager;
    private MainActivity act;
    private Context context;
    private SpProgressDialog progress = null;
    private GtfsView data;
    private File zip;

    public DownloadTask(MainActivity act, FragmentManager fragmentManager,GtfsView data){
        this.act=act;
        this.context=act.getApplicationContext();
        this.fragmentManager=fragmentManager;
        this.data=data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = SpProgressDialog.newInstance(R.string.download);
        progress.show(fragmentManager,"Progress");
    }

    protected void onProgressUpdate(Integer... val) {
        progress.setProgress(val[0]);
    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url=new URL(data.gtfs_url);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            ContextWrapper cw=new ContextWrapper(context);
            File dir=cw.getDir("tmp",Context.MODE_PRIVATE);
            zip=new File(dir,"tmp.zip");
            OutputStream output = new FileOutputStream(zip);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        melt(zip);
        File dir=zip.getParentFile();
        zip.delete();
        progress.dismiss();
        ParseTask task = new ParseTask (act, fragmentManager,data, dir);
        task.execute("");
    }

    public static void melt(File zip){
        try (FileInputStream fis = new FileInputStream(zip)) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                try (ZipInputStream zis = new ZipInputStream(bis)) {
                    ZipEntry zipentry;
                    while ((zipentry = zis.getNextEntry()) != null) {
                        try (FileOutputStream fos = new FileOutputStream(zip.getParentFile().getAbsolutePath()+"/"+zipentry.getName());
                             BufferedOutputStream bos = new BufferedOutputStream(fos);
                        ) {
                            byte[] data = new byte[1024]; // 1KB 調整可
                            int count = 0;
                            while ((count = zis.read(data)) != -1) {
                                bos.write(data, 0, count);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
