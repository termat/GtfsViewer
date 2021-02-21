package net.termat.gtfsviewer.components;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import net.termat.gtfsviewer.MapActivity;
import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.gtfs.model.GStop;
import net.termat.gtfsviewer.task.GeoCorderTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;

public class DialogUtil {

    private DialogUtil(){}

    /**
     * 検索ダイアログの表示
     * @param context
     * @param url
     */
    public static void showSearchDialog(MapActivity context, String url, LatLngBounds area,List<Symbol> stop,SymbolManager symbols,String mes){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching...");
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.search));
        builder.setIcon(R.drawable.icon_search_solid);
        final EditText word = new EditText(context);
        if(mes!=null)word.setText(mes);
        word.setHint(context.getString(R.string.search));
        builder.setView(word);
        DialogInterface.OnClickListener yes= (dialog, which) -> {
            String key=word.getText().toString();
            if(key.isEmpty())return;
            GeoCorderTask task=new GeoCorderTask(url,key);
            task.setListener(json -> {
                progressDialog.dismiss();
                List<Symbol> lo;
                if(json!=null){
                    lo= processLocation(json,area,symbols);
                }else{
                    lo=new ArrayList<Symbol>();
                }
                List<Symbol> items=searchStop(stop,key);
                items.addAll(lo);
                dialog.dismiss();
                Log.w("URL",key);
                context.showSymbolList(context.getString(R.string.search_result),new ArrayList<>());
                context.showSymbolList(context.getString(R.string.search_result),items);
            });
            task.execute("");
            progressDialog.show();
        };
        DialogInterface.OnClickListener no= (dialog, which) -> {
            context.speech(0,0);
        };
        builder.setPositiveButton(context.getString(R.string.search), yes);
        builder.setNegativeButton(context.getString(R.string.speech_input), no);
        AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity= Gravity.TOP;
        alertDialog.show();
    }

    private static List<Symbol> searchStop(List<Symbol> stop, String key){
        List<Symbol> ret=new ArrayList<>();
        for(Symbol s : stop){
            if(s.getTextField().contains(key)){
                 ret.add(s);
            }
        }
        return ret;
    }

    public static List<Symbol> processLocation(List<Map<String,Object>> json, LatLngBounds area, SymbolManager symbols){
        List<SymbolOptions> ret=new ArrayList<>();
        for(Map<String,Object> js : json){
            if(ret.size()>6)break;
            Map<String,Object> geom=(Map<String,Object>)js.get("geometry");
            List<Double> dd=(List<Double>)geom.get("coordinates");
            double lon=dd.get(0);
            double lat=dd.get(1);
            LatLng ll=new LatLng(lat,lon);
            if(area.contains(ll)){
                Map<String,Object> prop=(Map<String,Object>)js.get("properties");
                String title=(String)prop.get("title");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "FLAG");
                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(ll)
                        .withTextField(title)
                        .withData(jsonObject)
                        .withTextColor("#000000")
                        .withTextSize(14.0f)
                        .withTextOffset(new Float[]{0.0f, -1.2f})
                        .withTextJustify(TEXT_JUSTIFY_AUTO)
                        .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                        .withIconImage(Symbols.MARKER_FLAG)
                        .withIconSize(0.9f)
                        .withSymbolSortKey(10.0f)
                        .withDraggable(false);
                ret.add(symbolOptions);
            }
        }
        return symbols.create(ret);
    }
}
