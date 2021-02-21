package net.termat.gtfsviewer.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.termat.gtfsviewer.R;
import net.termat.gtfsviewer.gtfs.data.GtfsView;
import net.termat.gtfsviewer.gtfs.data.GtfsViewDao;
import net.termat.gtfsviewer.gtfs.data.GtfsViewData;

import java.util.List;

public class RecycleGtfsRtListAdapter extends RecyclerView.Adapter<RecycleGtfsRtListAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private int resource;
    private GtfsViewDao dao;
    private List<GtfsView> gtfs;
    private RecycleGtfsRtListAdapter.OnClickListsner onlistener1,onlistener2;

    public RecycleGtfsRtListAdapter(@NonNull Context context, int resource){
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource=resource;
        dao= GtfsViewData.getInstance(context).getDao();
        gtfs =dao.findAll();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(resource, parent,false);
        RecycleGtfsRtListAdapter.MyViewHolder vh = new RecycleGtfsRtListAdapter.MyViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GtfsView gr= gtfs.get(position);
        holder.name.setText(gr.name);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlistener1.onClick(gr);
            }
        });
        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlistener2.onClick(gr);
            }
        });
    }

    public void updateList(){
        gtfs =dao.findAll();
    }

    public void add(GtfsView g){
        dao.insert(g);
        gtfs.add(g);
    }

    public void delete(GtfsView g){
        dao.delete(g);
        gtfs.remove(g);
    }

    public void update(GtfsView g){
        dao.update(g);
    }

    public GtfsView get(int i){
        return gtfs.get(i);
    }

    @Override
    public int getItemCount() {
        return gtfs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public TextView name;
        public Button button;
        public Button button2;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.item_text);
            button=(Button)view.findViewById(R.id.item_button);
            button2=(Button)view.findViewById(R.id.item_button2);
        }
    }

    public interface OnClickListsner{
        public void onClick(GtfsView item);
    }

    public void setOnItemClickListener(RecycleGtfsRtListAdapter.OnClickListsner listener) {
        this.onlistener1 = listener;
    }

    public void setOnExecClickListener(RecycleGtfsRtListAdapter.OnClickListsner listener) {
        this.onlistener2 = listener;
    }

}
