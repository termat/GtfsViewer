package net.termat.gtfsviewer.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;

import net.termat.gtfsviewer.R;

import java.util.List;

public class SearchListAdapter extends ArrayAdapter<Symbol> {
    private int resource;
    private List<Symbol> items;
    private LayoutInflater inflater;
    private Bitmap STOP,BUS,FLAG;

    public SearchListAdapter(@NonNull Context context, int resource, List<Symbol> items) {
        super(context, resource,items);
        initImage(context);
        this.resource=resource;
        this.items=items;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView!=null){
            view=convertView;
        }else{
            view=inflater.inflate(resource,null);
        }
        Symbol item=items.get(position);
        ImageView img=(ImageView)view.findViewById(R.id.search_item_img);
        String type=((JsonObject)item.getData()).get("type").getAsString();
        if(type.equals("BUS")){
            img.setImageBitmap(BUS);
        }else if(type.equals("STOP")){
            img.setImageBitmap(STOP);
        }else if(type.equals("FLAG")){
            img.setImageBitmap(FLAG);
        }
        TextView name=(TextView)view.findViewById(R.id.search_item_name);
        name.setText(item.getTextField());
        return view;
    }

    private void initImage(Context context){
        STOP=getBitmapFromVectorDrawable(context, R.drawable.icon_point);
        BUS=getBitmapFromVectorDrawable(context, R.drawable.icon_bus);
        FLAG=getBitmapFromVectorDrawable(context, R.drawable.icon_flag);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId,context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
